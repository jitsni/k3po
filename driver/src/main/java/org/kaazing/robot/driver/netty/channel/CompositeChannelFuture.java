/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.netty.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

/**
 * A {@link ChannelFuture} of {@link ChannelFuture}s. It is useful when you want
 * to get notified when all {@link ChannelFuture}s are complete.
 *
 * Note that this future is complete if and only if its containing futures are
 * complete Setting this future to complete does NOT result in containing
 * futures getting completed.
 *
 * This future is not cancelable. If one of the futures in which this future is
 * composed of is canceled. The CompositeChannelFuture2 will be considered
 * failed.
 *
 * @param <E> the type of the child futures.
 */
public class CompositeChannelFuture<E extends ChannelFuture> extends DefaultChannelFuture {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(CompositeChannelFuture.class);

    private final NotifyingListener listener = new NotifyingListener();
    private final AtomicInteger unnotified = new AtomicInteger();
    private volatile boolean constructionFinished;
    private final Collection<E> kids;
    private int successCount;
    private int failedCount;
    private int cancelledCount;
    private final boolean failFast;

    public CompositeChannelFuture(Channel channel, Collection<E> kids) {
        this(channel, kids, false);
    }

    // If we fail fast it means the composite is set to failure as soon as we see a single future fail
    public CompositeChannelFuture(Channel channel, Collection<E> kids, boolean failFast) {
        super(channel, false);

        this.failFast = failFast;
        this.kids = new ArrayList<E>(kids);
        boolean isLogDebugEnabled = LOGGER.isDebugEnabled();

        for (E k : kids) {
            k.addListener(listener);
            unnotified.incrementAndGet();
            if (isLogDebugEnabled) {
                LOGGER.debug("Adding listener |" + listener + "| for future |" + k + "|");
            }
        }
        /*
         * Note that a composite with no children will be automatically set to
         * success
         */
        constructionFinished = true;
        if (unnotified.get() == 0) {
            setSuccess();
        }
    }

    @Override
    public Throwable getCause() {
        Throwable t = super.getCause();
        if (t != null) {
            return t;
        }

        Iterator<E> i = kids.iterator();
        while (i.hasNext()) {
            E future = i.next();
            t = future.getCause();
            if (t != null) {
                /*
                 * If we found one then the listener hasn't been notified yet
                 */
                if (failFast) {
                    setFailure(t);
                }
                return t;
            }
        }
        return null;
    }

    private interface CompositeTrue {
        boolean isTrue(ChannelFuture f);
    }

    @Override
    public boolean isSuccess() {

        if (super.isSuccess()) {
            return true;
        }

        boolean result = this.allTrue(new CompositeTrue() {
            @Override
            public boolean isTrue(ChannelFuture f) {
                return f.isSuccess();
            }
        });

        /*
         * If true we know we are done and the listener just hasn't been
         * notified yet to set this.setSuccess(). So we do this now. But it may
         * have since been marked success sine we last check so make sure we
         * still return true.
         */
        // return result ? (super.setSuccess() || true) : false;
        return result;

    }

    @Override
    public boolean isDone() {

        if (super.isDone()) {
            return true;
        }

        return this.allTrue(new CompositeTrue() {
            @Override
            public boolean isTrue(ChannelFuture f) {
                return f.isDone();
            }
        });

    }

    private boolean allTrue(CompositeTrue predicate) {
        /* An empty list should evaluate to false. Always. */
        if (kids.isEmpty()) {
            return false;
        }

        Iterator<E> i = kids.iterator();
        while (i.hasNext()) {
            E future = i.next();
            if (!predicate.isTrue(future)) {
                return false;
            }
        }
        return true;
    }

    private class NotifyingListener implements ChannelFutureListener, ChannelFutureProgressListener {

        @Override
        public void operationProgressed(ChannelFuture future, long amount, long current, long total) throws Exception {

            if (constructionFinished) {
                setProgress(amount, current, total);
            }
        }

        @Override
        public void operationComplete(final ChannelFuture future) {

            boolean isLogDebugEnabled = LOGGER.isDebugEnabled();
            boolean isSuccess = future.isSuccess();
            boolean isCancelled = future.isCancelled();
            boolean failed = false;

            if (isLogDebugEnabled) {
                LOGGER.debug("listener |" + listener + "| notified that future " + future
                        + " completed. Current unnotified value is " + unnotified.get());
            }

            /* We need to synchronize here due to the addChildren method */
            synchronized (CompositeChannelFuture.this) {

                if (CompositeChannelFuture.super.isDone()) {
                    // Then we must have failed fast.
                    return;
                }
                int currentUnnotified = unnotified.decrementAndGet();

                if (isSuccess) {
                    successCount++;
                } else if (isCancelled) {
                    cancelledCount++;
                } else {
                    failed = true;
                    failedCount++;
                }

                // callSetDone = successCount + failureCount == futures.size();
                if (currentUnnotified == 0 && constructionFinished) {
                    final int totalKids = kids.size();
                    if (totalKids == successCount) {
                        if (isLogDebugEnabled) {
                            LOGGER.debug("Composite completed with success");
                        }
                        setSuccess();
                    } else if (totalKids == cancelledCount) {
                        if (isLogDebugEnabled) {
                            LOGGER.debug("Composite completed all cancelled");
                        }
                        if (!cancel()) {
                            if (!isCancelled()) {
                                // Then the composite was non-cancellable. Set to success
                                setSuccess();
                            }
                        }
                    } else {
                        for (E f : kids) {
                            Throwable t = f.getCause();
                            if (t != null) {
                                if (isLogDebugEnabled) {
                                    LOGGER.debug("Composite completed one or more failed. Setting failed to first", t);
                                }
                                setFailure(t);
                                return;
                            }
                        }
                    }
                } else if (failed && failFast && constructionFinished) {
                    setFailure(future.getCause());
                }

            }
        }
    }
}

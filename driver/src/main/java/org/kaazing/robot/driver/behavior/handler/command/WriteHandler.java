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

package org.kaazing.robot.driver.behavior.handler.command;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.driver.behavior.handler.codec.MessageEncoder;

public class WriteHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteHandler.class);

    private final List<MessageEncoder> encoders;

    public WriteHandler(List<MessageEncoder> encoders) {
        if (encoders == null) {
            throw new NullPointerException("encoders");
        } else if (encoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one encoder");
        }
        this.encoders = encoders;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        ChannelBuffer[] buffers = new ChannelBuffer[encoders.size()];
        int idx = 0;
        for (MessageEncoder encoder : encoders) {
            buffers[idx] = encoder.encode();
            idx++;
        }
        LOGGER.debug("Invoking write command");
        Channels.write(ctx, getHandlerFuture(), wrappedBuffer(buffers));
    }

}

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

package org.kaazing.robot.driver.behavior.handler.codec;

import static java.lang.String.format;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.regex.NamedGroupMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class ReadRegexDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadRegexDecoder.class);

    private final NamedGroupPattern pattern;
    private final Charset charset;
    private final ExpressionContext environment;

    public ReadRegexDecoder(final NamedGroupPattern pattern, final Charset charset, final ExpressionContext environment) {
        this.pattern = pattern;
        this.environment = environment;
        this.charset = charset;
    }

    @Override
    protected Object decodeBufferLast(final ChannelBuffer buffer) throws Exception {
        return decodeBuffer(buffer, true);
    }

    @Override
    protected Object decodeBuffer(final ChannelBuffer buffer) throws Exception {
        return decodeBuffer(buffer, false);
    }

    private Object decodeBuffer(final ChannelBuffer buffer, boolean isLast) throws Exception {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();

        final ChannelBuffer observedBytes = buffer.slice();
        final String observed = observedBytes.toString(charset);

        final NamedGroupMatcher matcher = pattern.matcher(observed);

        // TODO: Need to deal with anchoring
        boolean allInputMatched = matcher.matches();
        boolean prefixMatched = allInputMatched || matcher.lookingAt();
        boolean noMatchMayMatchLater = !prefixMatched && matcher.hitEnd();

        // We keep looking while we match or while we don't match but it is still possible to match
        if ((allInputMatched || !isLast) && noMatchMayMatchLater) {
            if (isDebugEnabled) {
                LOGGER.debug("Waiting for more data to match full regex");
            }
            return null;
        }

        // If we never matched we fail.
        if (!prefixMatched) {
            throw new MessageMismatchException(format("Regex %s mismatch.", pattern.pattern()), pattern.pattern(),
                    observed);
        }

        captureGroups(matcher);

        // skip the bytes we actually matched
        buffer.skipBytes(matcher.end());

        if (isDebugEnabled) {
            LOGGER.debug(format("Regex handler read %d bytes, leaving buffer=%s", matcher.end(), buffer.toString(UTF_8)));
        }

        return buffer;
    }

    private void captureGroups(NamedGroupMatcher matcher) {
        for (String captureName : matcher.groupNames()) {
            String captured = matcher.group(captureName);
            byte[] bytes = captured.getBytes(UTF_8);
            environment.getELResolver().setValue(environment, null, captureName, bytes);
        }
    }
}

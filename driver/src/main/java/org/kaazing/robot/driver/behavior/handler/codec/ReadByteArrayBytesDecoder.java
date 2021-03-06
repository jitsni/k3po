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

import org.jboss.netty.buffer.ChannelBuffer;

import org.kaazing.robot.lang.el.ExpressionContext;

public class ReadByteArrayBytesDecoder extends ReadFixedLengthBytesDecoder<byte[]> {

    public ReadByteArrayBytesDecoder(final int length) {
        super(length);
    }

    public ReadByteArrayBytesDecoder(final int length, final ExpressionContext environment, final String captureName) {
        super(length, environment, captureName);
    }

    // Read the data into an array of bytes
    @Override
    public byte[] readBuffer(final ChannelBuffer buffer) {
        int len = getLength();
        byte[] matched = new byte[len];
        buffer.readBytes(matched, 0, len);
        return matched;
    }
}

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

import java.net.SocketAddress;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

public class ConnectHandler extends AbstractCommandHandler {

    private final SocketAddress remoteAddress;

    public ConnectHandler(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {

        ChannelFuture handlerFuture = getHandlerFuture();
        Channels.connect(ctx, handlerFuture, remoteAddress);
    }

}

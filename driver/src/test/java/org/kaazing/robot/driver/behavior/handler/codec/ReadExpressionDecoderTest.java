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

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.el.ExpressionFactoryUtils;

public class ReadExpressionDecoderTest {

    private ExpressionFactory expressionFactory;
    private ExpressionContext environment;

    @Before
    public void setUp() {
        ChannelPipeline pipeline = pipeline(new SimpleChannelHandler());
        ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();
        channelFactory.newChannel(pipeline);
        pipeline.getContext(SimpleChannelHandler.class);
        expressionFactory = ExpressionFactoryUtils.newExpressionFactory();
        environment = new ExpressionContext();
    }

    @Test
    public void completeMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(expected = MessageMismatchException.class)
    public void noMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x04 }));
    }

    @Test
    public void fragmentedMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);

        remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(expected = MessageMismatchException.class)
    public void onlyPartialMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);

        decoder.decode(copiedBuffer(new byte[] { 0x04 }));
    }

    @Test
    public void completeMatchWithBytesLeftOverOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03, 0x04 }));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer(new byte[] { 0x04 }), remainingBuffer);
    }
}

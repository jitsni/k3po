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

package org.kaazing.robot.driver.behavior.visitor;

import java.util.List;

import org.kaazing.robot.driver.behavior.visitor.InjectBarriersVisitor.State;
import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstChildClosedNode;
import org.kaazing.robot.lang.ast.AstChildOpenedNode;
import org.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import org.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstConnectNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import org.kaazing.robot.lang.ast.AstNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstReadAwaitNode;
import org.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import org.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import org.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import org.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import org.kaazing.robot.lang.ast.AstReadNotifyNode;
import org.kaazing.robot.lang.ast.AstReadOptionNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstStreamableNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteAwaitNode;
import org.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import org.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import org.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import org.kaazing.robot.lang.ast.AstWriteNotifyNode;
import org.kaazing.robot.lang.ast.AstWriteOptionNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;

public class InjectBarriersVisitor implements AstNode.Visitor<AstScriptNode, State> {

    public static enum ReadWriteState {
        NONE, READ, WRITE
    }

    public static final class State {
        private List<AstStreamNode> streams;
        private List<AstStreamableNode> streamables;
        private ReadWriteState readWriteState;
        private int readWriteBarrierCount;
    }

    @Override
    public AstScriptNode visit(AstScriptNode script, State state) throws Exception {

        AstScriptNode newScript = new AstScriptNode();
        newScript.setLocationInfo(script.getLocationInfo());

        state.streams = newScript.getStreams();

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        return newScript;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        state.readWriteState = ReadWriteState.NONE;

        AstAcceptNode newAcceptNode = new AstAcceptNode();
        newAcceptNode.setLocationInfo(acceptNode.getLocationInfo());
        newAcceptNode.setAcceptName(acceptNode.getAcceptName());
        newAcceptNode.setLocation(acceptNode.getLocation());

        state.streamables = newAcceptNode.getStreamables();
        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstAcceptableNode acceptableNode : acceptNode.getAcceptables()) {
            acceptableNode.accept(this, state);
        }

        state.streams.add(newAcceptNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {

        state.readWriteState = ReadWriteState.NONE;

        AstAcceptableNode newAcceptableNode = new AstAcceptableNode();
        newAcceptableNode.setLocationInfo(acceptableNode.getLocationInfo());
        newAcceptableNode.setAcceptName(acceptableNode.getAcceptName());

        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.streams.add(newAcceptableNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        state.readWriteState = ReadWriteState.NONE;

        AstConnectNode newConnectNode = new AstConnectNode();
        newConnectNode.setLocationInfo(connectNode.getLocationInfo());
        newConnectNode.setLocation(connectNode.getLocation());

        state.streamables = newConnectNode.getStreamables();
        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.streams.add(newConnectNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAwaitNode node, State state) throws Exception {

        state.readWriteState = ReadWriteState.NONE;
        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAwaitNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadNotifyNode node, State state) throws Exception {

        state.readWriteState = ReadWriteState.NONE;
        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteNotifyNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {

        conditionallyInjectWriteBarrier(state);
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.WRITE;

        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode childOpenedNode, State state) throws Exception {
        state.streamables.add(childOpenedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode childClosedNode, State state) throws Exception {

        state.streamables.add(childClosedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode openedNode, State state) throws Exception {

        state.streamables.add(openedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode boundNode, State state) throws Exception {

        state.streamables.add(boundNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode connectedNode, State state) throws Exception {

        state.streamables.add(connectedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) throws Exception {

        state.streamables.add(node);
        state.readWriteState = ReadWriteState.READ;
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode disconnectedNode, State state) throws Exception {

        state.streamables.add(disconnectedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode unboundNode, State state) throws Exception {

        state.streamables.add(unboundNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstClosedNode closedNode, State state) throws Exception {

        state.streamables.add(closedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpHeaderNode node, State state) throws Exception {
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.READ;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpHeaderNode node, State state) throws Exception {
        conditionallyInjectWriteBarrier(state);
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.WRITE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpContentLengthNode node, State state) throws Exception {
        conditionallyInjectWriteBarrier(state);
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.WRITE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpMethodNode node, State state) throws Exception {
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.READ;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpMethodNode node, State state) throws Exception {
        conditionallyInjectWriteBarrier(state);
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.WRITE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpParameterNode node, State state) throws Exception {
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.READ;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpParameterNode node, State state) throws Exception {
        conditionallyInjectWriteBarrier(state);
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.WRITE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpVersionNode node, State state) throws Exception {
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.READ;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpVersionNode node, State state) throws Exception {
        conditionallyInjectWriteBarrier(state);
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.WRITE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpStatusNode node, State state) throws Exception {
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.READ;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpStatusNode node, State state) throws Exception {
        conditionallyInjectWriteBarrier(state);
        state.streamables.add(node);
        state.readWriteState = ReadWriteState.WRITE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpRequestNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpResponseNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    private void conditionallyInjectWriteBarrier(State state) {
        List<AstStreamableNode> streamables = state.streamables;

        switch (state.readWriteState) {
        case READ:
            String barrierName = String.format("~read~write~%d", ++state.readWriteBarrierCount);
            AstReadNotifyNode readNotify = new AstReadNotifyNode();
            readNotify.setBarrierName(barrierName);
            AstWriteAwaitNode writeAwait = new AstWriteAwaitNode();
            writeAwait.setBarrierName(barrierName);
            streamables.add(readNotify);
            streamables.add(writeAwait);
            break;
        default:
            break;
        }
    }

    @Override
    public AstScriptNode visit(AstEndOfHttpHeadersNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }
}

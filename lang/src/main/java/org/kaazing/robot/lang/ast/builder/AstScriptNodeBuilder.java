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

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstScriptNode;

public class AstScriptNodeBuilder extends AbstractAstNodeBuilder<AstScriptNode, AstScriptNode> {

    private int line;

    public AstScriptNodeBuilder() {
        this(new AstScriptNode());
    }

    private AstScriptNodeBuilder(AstScriptNode node) {
        super(node, node);
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int line(int line) {
        this.line = line;
        return line;
    }

    @Override
    public AstScriptNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstScriptNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstAcceptNodeBuilder.ScriptNested<AstScriptNodeBuilder> addAcceptStream() {
        return new AstAcceptNodeBuilder.ScriptNested<AstScriptNodeBuilder>(this);
    }

    public AstAcceptableNodeBuilder.ScriptNested<AstScriptNodeBuilder> addAcceptedStream() {
        return new AstAcceptableNodeBuilder.ScriptNested<AstScriptNodeBuilder>(this);
    }

    public AstConnectNodeBuilder.ScriptNested<AstScriptNodeBuilder> addConnectStream() {
        return new AstConnectNodeBuilder.ScriptNested<AstScriptNodeBuilder>(this);
    }

    @Override
    public AstScriptNode done() {
        return node;
    }

}

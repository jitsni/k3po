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

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.LinkedList;
import java.util.List;

import org.kaazing.robot.lang.LocationInfo;

public abstract class AstStreamNode extends AstNode {

    private List<AstStreamableNode> streamables;
    private LocationInfo endLocation;

    public List<AstStreamableNode> getStreamables() {
        if (streamables == null) {
            streamables = new LinkedList<AstStreamableNode>();
        }

        return streamables;
    }

    public LocationInfo getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationInfo info) {
        endLocation = info;
    }

    @Override
    protected int hashTo() {
        int hashCode = super.hashTo();

        if (streamables != null) {
            hashCode <<= 4;
            hashCode ^= streamables.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstStreamNode that) {
        return super.equalTo(that) && equivalent(this.streamables, that.streamables);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        formatNodeLine(sb);
        if (streamables != null) {
            for (AstStreamableNode streamable : streamables) {
                streamable.formatNode(sb);
            }
        }
    }

    protected void formatNodeLine(StringBuilder sb) {
        super.formatNode(sb);
    }
}

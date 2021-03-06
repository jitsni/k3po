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

package org.kaazing.robot.lang.ast.matcher;

public class AstIntLengthBytesMatcher extends AstFixedLengthBytesMatcher {

    @Deprecated
    public AstIntLengthBytesMatcher() {
        super(Integer.SIZE / Byte.SIZE);
    }

    public AstIntLengthBytesMatcher(String captureName) {
        super(Integer.SIZE / Byte.SIZE, captureName);
    }

    @Override
    public String toString() {
        String captureName = getCaptureName();
        if (captureName == null) {
            return "int";

        }
        return String.format("(int:%s)", captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }
}

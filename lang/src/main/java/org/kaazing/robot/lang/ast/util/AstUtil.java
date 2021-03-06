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

package org.kaazing.robot.lang.ast.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.el.ValueExpression;

public final class AstUtil {

    public static boolean equivalent(boolean v, boolean v2) {
        return v == v2;
    }

    public static boolean equivalent(byte v, byte v2) {
        return v == v2;
    }

    public static boolean equivalent(char v, char v2) {
        return v == v2;
    }

    public static boolean equivalent(double v, double v2) {
        return v == v2;
    }

    public static boolean equivalent(float v, float v2) {
        return v == v2;
    }

    public static boolean equivalent(int v, int v2) {
        return v == v2;
    }

    public static boolean equivalent(long v, long v2) {
        return v == v2;
    }

    public static boolean equivalent(short v, short v2) {
        return v == v2;
    }

    public static boolean equivalent(ValueExpression e, ValueExpression e2) {
        return e == e2
                || (e != null && e.getExpressionString().equals(e2.getExpressionString()) && e.getExpectedType().equals(
                        e2.getExpectedType()));
    }

    public static boolean equivalent(Pattern p, Pattern p2) {
        return p == p2 || (p != null && p.pattern().equals(p2.pattern()));
    }

    public static boolean equivalent(Object o, Object o2) {
        return o == o2 || (o != null && o.equals(o2));
    }

    public static boolean equivalent(Collection<?> c, Collection<?> c2) {
        if (c != null && c.isEmpty()) {
            c = null;
        }
        if (c2 != null && c2.isEmpty()) {
            c2 = null;
        }
        return c == c2 || (c != null && c.equals(c2));
    }

    public static boolean equivalent(boolean[] a, boolean[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(byte[] a, byte[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(char[] a, char[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(double[] a, double[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(float[] a, float[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(int[] a, int[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(long[] a, long[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(short[] a, short[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(Object[] a, Object[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    private AstUtil() {
        // utility class
    }
}

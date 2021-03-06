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

package org.kaazing.robot.driver.jmock;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;

class ChannelStateEventChannelStateMatcher extends BaseMatcher<ChannelStateEvent> {

    private final Matcher<ChannelState> expectedState;
    private final Matcher<Object> expectedValue;

    ChannelStateEventChannelStateMatcher(Matcher<ChannelState> expectedState, Matcher<Object> expectedValue) {
        this.expectedState = expectedState;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean matches(Object obj) {
        return (obj instanceof ChannelStateEvent) && matches((ChannelStateEvent) obj);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("channelState ").appendValue(expectedState);
        description.appendText("[value ").appendValue(expectedValue).appendText("]");
    }

    private boolean matches(ChannelStateEvent evt) {
        ChannelState state = evt.getState();
        Object value = evt.getValue();
        return expectedState.matches(state) && expectedValue.matches(value);
    }
}

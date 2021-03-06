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

package org.kaazing.robot.control.event;

public final class FinishedEvent extends CommandEvent {

    private String expectedScript;
    private String observedScript;

    public Kind getKind() {
        return Kind.FINISHED;
    }

    public void setExpectedScript(String expectedScript) {
        this.expectedScript = expectedScript;
    }

    public String getExpectedScript() {
        return expectedScript;
    }

    public void setObservedScript(String observedScript) {
        this.observedScript = observedScript;
    }

    public String getObservedScript() {
        return observedScript;
    }

    @Override
    public int hashCode() {
        int hashCode = hashTo();

        if (expectedScript != null && observedScript != null) {
            hashCode ^= (expectedScript + observedScript).hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof FinishedEvent && equalTo((FinishedEvent) o);
    }

    protected boolean equalTo(FinishedEvent that) {
        return super.equalTo(that) && this.expectedScript == that.expectedScript || this.expectedScript != null
                && this.expectedScript.equals(that.expectedScript) && this.observedScript == that.observedScript
                || this.observedScript != null && this.observedScript.equals(that.observedScript);
    }
}

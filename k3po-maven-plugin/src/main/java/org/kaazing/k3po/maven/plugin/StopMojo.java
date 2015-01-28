/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.k3po.maven.plugin;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.identityHashCode;

import org.apache.maven.plugin.MojoExecutionException;
import org.kaazing.k3po.driver.RobotServer;

/**
 * Stop K3PO
 *
 * @goal stop
 * @phase post-integration-test
 *
 * @requiresDependencyResolution test
 */
public class StopMojo extends AbstractMojo {

    protected void executeImpl() throws MojoExecutionException {

        RobotServer server = getServer();
        if (server == null) {
            getLog().error(format("K3PO not running"));
        }

        try {
            long checkpoint = currentTimeMillis();
            server.stop();
            float duration = (currentTimeMillis() - checkpoint) / 1000.0f;
            getLog().debug(format("K3PO [%08x] stopped in %.3fsec", identityHashCode(server), duration));

            setServer(null);
        }
        catch (Exception e) {
            throw new MojoExecutionException(format("K3PO [%08x] failed to stop", identityHashCode(server)), e);
        }
    }
}

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

package org.kaazing.robot.control;

import static java.lang.String.format;

import java.net.URI;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class RobotControlFactories {

    private RobotControlFactories() {

    }

    public static RobotControlFactory createRobotControlFactory() {
        return createRobotControlFactory(Thread.currentThread().getContextClassLoader());
    }

    public static RobotControlFactory createRobotControlFactory(ClassLoader classLoader) {
        Class<RobotControlFactorySPI> clazz = RobotControlFactorySPI.class;
        ServiceLoader<RobotControlFactorySPI> loader = (classLoader != null) ?
                ServiceLoader.load(clazz, classLoader) : ServiceLoader.load(clazz);
        ConcurrentMap<String, RobotControlFactorySPI> factories = new ConcurrentHashMap<>();
        for (RobotControlFactorySPI factory : loader) {
            // just return first one, maybe in the future we will look for them by a parameter or name
            factories.putIfAbsent(factory.getSchemeName(), factory);
        }
        return new RobotServerFactoryImpl(factories);
    }

    private static class RobotServerFactoryImpl implements RobotControlFactory {
        private final Map<String, RobotControlFactorySPI> factories;

        public RobotServerFactoryImpl(Map<String, RobotControlFactorySPI> factories) {
            this.factories = factories;
        }

        @Override
        public RobotControl newClient(URI controlURI) throws Exception {
            final String schemeName = controlURI.getScheme();
            if (schemeName == null) {
                throw new NullPointerException("scheme");
            }

            RobotControlFactorySPI factory = factories.get(schemeName);
            if (factory == null) {
                throw new IllegalArgumentException(format("Unable to load scheme '%s': No appropriate Robot Control " +
                        "factory found", schemeName));
            }
            return factory.newClient(controlURI);
        }
    }
}

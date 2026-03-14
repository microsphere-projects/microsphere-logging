/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.microsphere.logging.jmx;

import io.microsphere.logging.Logger;
import io.microsphere.logging.Logging;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.LoggingMXBean;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.util.ClassLoaderUtils.getClassLoader;
import static io.microsphere.util.ServiceLoaderUtils.loadServicesList;
import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static java.util.Collections.unmodifiableList;
import static javax.management.ObjectName.getInstance;

/**
 * The Registrar class to register all {@link LoggingMXBean LoggingMXBeans}
 * that are adapted by {@link Logging} instances loaded from {@link ServiceLoader}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingMXBean
 * @see ServiceLoader
 * @since 1.0.0
 */
public class PlatformLoggingMXBeanRegistrar {

    private static final Logger logger = getLogger(PlatformLoggingMXBeanRegistrar.class);

    public static List<ObjectInstance> register() {
        return register(getClassLoader(PlatformLoggingMXBeanRegistrar.class));
    }

    public static List<ObjectInstance> register(ClassLoader classLoader) {
        List<ObjectInstance> instances = new LinkedList<>();
        MBeanServer mBeanServer = getPlatformMBeanServer();
        try {
            Iterable<Logging> serviceLoader = loadServicesList(Logging.class, classLoader);
            for (Logging logging : serviceLoader) {
                logger.info("Registering LoggingMXBean [{}]", logging);
                LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);

                ObjectName objectName = createObjectName(logging);
                instances.add(mBeanServer.registerMBean(adapter, objectName));
                logger.info("Registered LoggingMXBean [{}] with ObjectName [{}]", logging, objectName);
            }
        } catch (Exception e) {
            logger.warn("Failed to register LoggingMXBean [{}]", e);
        }
        return unmodifiableList(instances);
    }

    private static ObjectName createObjectName(Logging logging) throws MalformedObjectNameException {
        String name = logging.getName();
        return getInstance("io.microsphere.logging:type=" + name);
    }
}
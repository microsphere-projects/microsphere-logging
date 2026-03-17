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

import io.microsphere.annotation.Nonnull;
import io.microsphere.annotation.Nullable;
import io.microsphere.logging.Logger;
import io.microsphere.logging.Logging;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.LoggingMXBean;

import static io.microsphere.lang.function.ThrowableSupplier.execute;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.logging.LoggingUtils.loadAll;
import static io.microsphere.util.ClassLoaderUtils.getClassLoader;
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
public abstract class LoggingMXBeanRegistrar {

    private static final Logger logger = getLogger(LoggingMXBeanRegistrar.class);

    public static final String JMX_DOMAIN = "io.microsphere.logging";

    /**
     * Register all {@link LoggingMXBean LoggingMXBeans} that are adapted by {@link Logging} instances
     * loaded from {@link ServiceLoader} by default {@link ClassLoader}.
     *
     * @return non-null
     */
    @Nonnull
    public static List<ObjectInstance> registerAll() {
        return registerAll(getClassLoader(LoggingMXBeanRegistrar.class));
    }

    /**
     * Register all {@link LoggingMXBean LoggingMXBeans} that are adapted by {@link Logging} instances
     * loaded from {@link ServiceLoader} by specified {@link ClassLoader}.
     *
     * @param classLoader the specified {@link ClassLoader}
     * @return non-null
     */
    @Nonnull
    public static List<ObjectInstance> registerAll(ClassLoader classLoader) {
        List<ObjectInstance> instances = new LinkedList<>();
        Iterable<Logging> loggings = loadAll(classLoader);
        for (Logging logging : loggings) {
            ObjectInstance instance = register(logging);
            if (instance != null) {
                instances.add(instance);
            }
        }
        return unmodifiableList(instances);
    }

    /**
     * Register {@link LoggingMXBean LoggingMXBean} that is adapted by specified {@link Logging} instance.
     *
     * @param logging the specified {@link Logging} instance
     * @return the {@link ObjectInstance} if registered successfully , or {@code null} if failed
     */
    @Nullable
    public static ObjectInstance register(Logging logging) {
        MBeanServer mBeanServer = getPlatformMBeanServer();
        logger.info("Registering LoggingMXBean [{}]", logging);
        LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
        ObjectInstance instance = null;
        try {
            ObjectName objectName = createObjectName(logging);
            if (mBeanServer.isRegistered(objectName)) {
                instance = mBeanServer.getObjectInstance(objectName);
                logger.warn("LoggingMXBean [{}] has been registered with ObjectName [{}]", logging, objectName);
            } else {
                instance = mBeanServer.registerMBean(adapter, objectName);
                logger.info("Registered LoggingMXBean [{}] with ObjectName [{}]", logging, objectName);
            }
        } catch (Throwable e) {
            logger.warn("Failed to register LoggingMXBean [{}]", logging, e);
        }
        return instance;
    }

    /**
     * Get all registered {@link LoggingMXBean LoggingMXBeans} as {@link ObjectInstance} instances.
     *
     * @return non-null
     */
    @Nonnull
    public static Set<ObjectInstance> getRegisteredLoggingObjectInstances() {
        MBeanServer mBeanServer = getPlatformMBeanServer();
        return execute(() -> {
            ObjectName objectName = createObjectName("*");
            Set<ObjectInstance> instances = mBeanServer.queryMBeans(objectName, null);
            logger.info("Found {} LoggingMXBean(s) : {}", instances.size(), instances);
            return instances;
        });
    }

    private static ObjectName createObjectName(Logging logging) throws MalformedObjectNameException {
        return createObjectName(logging.getName());
    }

    private static ObjectName createObjectName(String type) throws MalformedObjectNameException {
        return getInstance(JMX_DOMAIN + ":type=" + type);
    }

    private LoggingMXBeanRegistrar() {
    }
}
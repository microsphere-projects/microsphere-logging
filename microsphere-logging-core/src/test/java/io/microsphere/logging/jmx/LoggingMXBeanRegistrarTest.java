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


import io.microsphere.collection.CollectionUtils;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.util.List;
import java.util.Set;

import static io.microsphere.collection.ListUtils.first;
import static io.microsphere.logging.jmx.LoggingMXBeanRegistrar.JMX_DOMAIN;
import static io.microsphere.logging.jmx.LoggingMXBeanRegistrar.getRegisteredLoggingObjectInstances;
import static io.microsphere.logging.jmx.LoggingMXBeanRegistrar.registerAll;
import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LoggingMXBeanRegistrar}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingMXBeanRegistrar
 * @since 1.0.0
 */
class LoggingMXBeanRegistrarTest {

    @Test
    void testConstants() {
        assertEquals("io.microsphere.logging", JMX_DOMAIN);
    }

    @Test
    void testRegisterAll() throws InstanceNotFoundException {
        List<ObjectInstance> instances = registerAll();
        assertEquals(1, instances.size());

        ObjectInstance instance = instances.get(0);
        ObjectName objectName = instance.getObjectName();
        assertEquals("io.microsphere.logging:type=Testing", objectName.toString());

        MBeanServer mBeanServer = getPlatformMBeanServer();
        assertTrue(mBeanServer.isRegistered(objectName));

        assertEquals(instance, mBeanServer.getObjectInstance(objectName));
    }

    @Test
    void testGetRegisteredLoggingObjectInstances() {
        List<ObjectInstance> instances = registerAll();
        Set<ObjectInstance> registeredInstances = getRegisteredLoggingObjectInstances();
        assertEquals(1, instances.size());
        assertEquals(first(instances), CollectionUtils.first(registeredInstances));
    }
}
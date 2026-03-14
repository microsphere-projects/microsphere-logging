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
package io.microsphere.logging.jdk;

import io.microsphere.logging.Logging;
import io.microsphere.reflect.MemberUtils;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LoggingMXBean;

import static java.util.Collections.unmodifiableSet;
import static java.util.logging.LogManager.getLoggingMXBean;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

/**
 * The Standard JDK {@link Logging}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @see LoggingMXBean
 * @since 1.0.0
 */
public class StandardLogging implements Logging {

    public static final Set<String> ALL_LEVELS = findAllLevels();

    private static Set<String> findAllLevels() {
        return unmodifiableSet(of(Level.class.getFields())
                .filter(MemberUtils::isStatic)
                .filter(MemberUtils::isFinal)
                .filter(field -> field.getType().equals(Level.class))
                .map(field -> field.getName())
                .collect(toSet()));
    }

    static final LoggingMXBean loggingMXBean = getLoggingMXBean();

    @Override
    public List<String> getLoggerNames() {
        return loggingMXBean.getLoggerNames();
    }

    @Override
    public Set<String> getSupportedLoggingLevels() {
        return ALL_LEVELS;
    }

    @Override
    public String getLoggerLevel(String loggerName) {
        return loggingMXBean.getLoggerLevel(loggerName);
    }

    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        loggingMXBean.setLoggerLevel(loggerName, levelName);
    }

    @Override
    public String getParentLoggerName(String loggerName) {
        return loggingMXBean.getParentLoggerName(loggerName);
    }

    @Override
    public String getName() {
        return "Java Logging";
    }

    @Override
    public int getPriority() {
        return MIN_PRIORITY;
    }
}
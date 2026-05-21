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

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LoggingMXBean;

import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static java.util.logging.LogManager.getLoggingMXBean;

/**
 * The Java {@link Logging}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @see LoggingMXBean
 * @since 1.0.0
 */
public class JavaLogging implements Logging {

    /**
     * The root logger name : ""
     */
    public static final String ROOT_LOGGER_NAME = "";

    /**
     * The priority of {@link JavaLogging}
     */
    public static final int PRIORITY = NORMAL_PRIORITY + 10;

    /**
     * All Logging Levels : "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL"
     *
     * @see Level
     */
    public static final Set<String> ALL_LEVELS = INSTANCE.resolve(Level.class);

    static final LoggingMXBean loggingMXBean = getLoggingMXBean();

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   String rootName = logging.getRootLoggerName();
     *   // returns "" (empty string for the JDK root logger)
     * }</pre>
     */
    @Override
    public String getRootLoggerName() {
        return ROOT_LOGGER_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   List<String> names = logging.getLoggerNames();
     * }</pre>
     */
    @Override
    public List<String> getLoggerNames() {
        return loggingMXBean.getLoggerNames();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   Set<String> levels = logging.getSupportedLoggingLevels();
     *   // e.g. ["OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL"]
     * }</pre>
     */
    @Override
    public Set<String> getSupportedLoggingLevels() {
        return ALL_LEVELS;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   String level = logging.getLoggerLevel("io.microsphere"); // e.g. "INFO"
     * }</pre>
     */
    @Override
    public String getLoggerLevel(String loggerName) {
        return loggingMXBean.getLoggerLevel(loggerName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   logging.setLoggerLevel("io.microsphere", "FINE");
     * }</pre>
     */
    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        loggingMXBean.setLoggerLevel(loggerName, levelName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   String parentName = logging.getParentLoggerName("io.microsphere.logging");
     *   // returns "io.microsphere"
     * }</pre>
     */
    @Override
    public String getParentLoggerName(String loggerName) {
        return loggingMXBean.getParentLoggerName(loggerName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   String name = logging.getName(); // "Java Logging"
     * }</pre>
     */
    @Override
    public String getName() {
        return "Java Logging";
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   JavaLogging logging = new JavaLogging();
     *   int priority = logging.getPriority();
     *   // returns JavaLogging.PRIORITY
     * }</pre>
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
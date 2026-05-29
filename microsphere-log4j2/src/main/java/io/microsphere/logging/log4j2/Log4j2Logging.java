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

package io.microsphere.logging.log4j2;

import io.microsphere.logging.Logging;
import io.microsphere.logging.log4j2.util.Log4j2Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.getLevelString;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.getLoggers;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME;

/**
 * {@link Logging} class based on the log4j2 framework.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @since 1.0.0
 */
public class Log4j2Logging implements Logging {

    /**
     * The priority of {@link Log4j2Logging}
     */
    public static final int PRIORITY = NORMAL_PRIORITY - 5;

    /**
     * All Logging Levels : "OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"
     *
     * @see Level
     */
    public static final Set<String> ALL_LEVELS = INSTANCE.resolve(Level.class);

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logging logging = new Log4j2Logging();
     *   String rootName = logging.getRootLoggerName();
     *   // returns ""
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
     *   Log4j2Logging logging = new Log4j2Logging();
     *   List<String> names = logging.getLoggerNames();
     * }</pre>
     */
    @Override
    public List<String> getLoggerNames() {
        return getLoggers()
                .stream()
                .map(Logger::getName)
                .collect(toList());
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logging logging = new Log4j2Logging();
     *   Set<String> levels = logging.getSupportedLoggingLevels();
     *   // e.g. ["OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"]
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
     *   Log4j2Logging logging = new Log4j2Logging();
     *   String level = logging.getLoggerLevel("io.microsphere");
     *   // e.g. "INFO"
     * }</pre>
     */
    @Override
    public String getLoggerLevel(String loggerName) {
        return getLevelString(loggerName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logging logging = new Log4j2Logging();
     *   logging.setLoggerLevel("io.microsphere", "DEBUG");
     * }</pre>
     */
    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        Log4j2Utils.setLoggerLevel(loggerName, levelName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logging logging = new Log4j2Logging();
     *   String name = logging.getName();
     *   // returns "Log4j2"
     * }</pre>
     */
    @Override
    public String getName() {
        return "Log4j2";
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logging logging = new Log4j2Logging();
     *   int priority = logging.getPriority();
     *   // returns Log4j2Logging.PRIORITY
     * }</pre>
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}

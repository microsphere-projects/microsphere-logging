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

package io.microsphere.logging.log4j.util;


import io.microsphere.annotation.Nonnull;
import io.microsphere.annotation.Nullable;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

import static org.apache.log4j.Level.toLevel;
import static org.apache.log4j.LogManager.getRootLogger;

/**
 * The Utilities class of Log4j2 Logger
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logger
 * @since 1.0.0
 */
public abstract class LoggerUtils {

    static final LoggerRepository loggerRepository = LogManager.getLoggerRepository();

    public static final String ROOT_LOGGER_NAME = getRootLogger().getName();

    /**
     * Get the LoggerRepository
     *
     * @return LoggerRepository , never be null
     */
    @Nonnull
    public static LoggerRepository getLoggerRepository() {
        return loggerRepository;
    }

    /**
     * Get the Logger by specified name
     *
     * @param loggerName Logger name
     * @return Logger , maby be null
     */
    @Nullable
    public static Logger getLogger(@Nonnull String loggerName) {
        return loggerRepository.getLogger(loggerName);
    }

    /**
     * Get the Logger by the request class
     *
     * @param requestClass request class
     * @return Logger , maby be null
     */
    @Nullable
    public static Logger getLogger(@Nonnull Class<?> requestClass) {
        return getLogger(requestClass.getName());
    }

    /**
     * Get the Level of Logger by the specified logger name
     *
     * @param loggerName Logger name
     * @return the Level of Logger , may be null
     */
    @Nullable
    public static Level getLevel(@Nonnull String loggerName) {
        Logger logger = getLogger(loggerName);
        return getLevel(logger);
    }

    /**
     * Get the Level of Logger
     *
     * @param logger Logger
     * @return the Level of Logger , may be null
     */
    @Nullable
    public static Level getLevel(@Nullable Logger logger) {
        Level level = null;
        if (logger != null) {
            level = logger.getEffectiveLevel();
        }
        return level;
    }

    /**
     * Set the Level of Logger by the specified logger name
     *
     * @param loggerName Logger name
     * @param levelName  level string, if it's <code>null</code>, then {@link Level#DEBUG} will be taken.
     */
    public static void setLoggerLevel(String loggerName, String levelName) {
        Logger logger = getLogger(loggerName);
        setLoggerLevel(logger, levelName);
    }

    /**
     * Set the Level of Logger
     *
     * @param logger      Logger
     * @param levelString level string, if it's <code>null</code>, then {@link Level#DEBUG} will be taken.
     */
    public static void setLoggerLevel(@Nullable Logger logger, @Nullable String levelString) {
        setLoggerLevel(logger, toLevel(levelString));
    }

    /**
     * Set the Level of Logger
     *
     * @param logger Logger
     * @param level  level
     */
    public static void setLoggerLevel(@Nullable Logger logger, @Nonnull Level level) {
        if (logger != null) {
            logger.setLevel(level);
        }
    }

    /**
     * Get the Level String of Logger by the specified logger name
     *
     * @param loggerName Logger name
     * @return the Level String of Logger , may be null
     */
    @Nullable
    public static String getLevelString(@Nonnull String loggerName) {
        Logger logger = getLogger(loggerName);
        Level level = getLevel(logger);
        return getLevelString(level);
    }

    /**
     * Get the Level String of Logger
     *
     * @param level Level
     * @return the Level String of Logger , may be null
     */
    @Nullable
    public static String getLevelString(@Nullable Level level) {
        return level == null ? null : level.toString();
    }

    private LoggerUtils() {
    }
}
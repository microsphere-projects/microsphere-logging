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
package io.microsphere.logging.log4j2.util;

import io.microsphere.annotation.Nonnull;
import io.microsphere.annotation.Nullable;
import io.microsphere.util.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Collection;
import java.util.function.Consumer;

import static org.apache.logging.log4j.Level.toLevel;

/**
 * The utilities class for Log4j2
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggerContext
 * @see Configuration
 * @see Logger
 * @since 1.0.0
 */
public abstract class Log4j2Utils implements Utils {


    /**
     * Get the {@link Configuration}
     *
     * @return non-null
     */
    public static Configuration getConfiguration() {
        LoggerContext loggerContext = getLoggerContext();
        return loggerContext.getConfiguration();
    }

    /**
     * Get the Logger by specified name
     *
     * @param loggerName Logger name
     * @return Logger , may be null
     */
    @Nullable
    public static Logger getLogger(@Nonnull String loggerName) {
        return getLoggerContext().getLogger(loggerName);
    }

    /**
     * Get the Logger by the request class
     *
     * @param requestClass request class
     * @return Logger , may be null
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
            level = logger.getLevel();
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

    /**
     * Find the {@link Appender} by its name
     *
     * @param appenderName the name of {@link Appender}
     * @param <T>          the type of {@link Appender}
     * @return <code>null</code> if can't be found
     */
    public static <T extends Appender> T findAppender(String appenderName) {
        Configuration configuration = getConfiguration();
        return configuration.getAppender(appenderName);
    }

    /**
     * Get the {@link LoggerContext}
     *
     * @return non-null
     */
    public static LoggerContext getLoggerContext() {
        return (LoggerContext) LogManager.getContext(false);
    }

    /**
     * Get all {@link Logger loggers}
     *
     * @return non-null
     */
    public static Collection<Logger> getLoggers() {
        return getLoggerContext().getLoggers();
    }

    /**
     * Apply the given {@link Consumer} to each {@link Logger} in the current {@link LoggerContext}.
     *
     * @param loggerConsumer the consumer to apply
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Utils.doInLogger(logger -> logger.setLevel(Level.DEBUG));
     * }</pre>
     */
    public static void doInLogger(Consumer<Logger> loggerConsumer) {
        getLoggers().forEach(loggerConsumer);
    }

    /**
     * Add the given {@link Appender} to the specified {@link Logger} instances.
     *
     * @param appender the {@link Appender} to add
     * @param loggers  the {@link Logger} instances to which the appender is added
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   Collection<Logger> loggers = Log4j2Utils.getLoggers();
     *   Log4j2Utils.addAppender(appender, loggers);
     * }</pre>
     */
    public static void addAppender(Appender appender, Iterable<Logger> loggers) {
        LoggerContext loggerContext = getLoggerContext();
        addAppender(loggerContext, appender, loggers);
    }

    /**
     * Add the given {@link Appender} to all {@link Logger} instances in the current {@link LoggerContext}.
     *
     * @param appender the {@link Appender} to add to all loggers
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   Log4j2Utils.addAppenderForAllLoggers(appender);
     * }</pre>
     */
    public static void addAppenderForAllLoggers(Appender appender) {
        LoggerContext loggerContext = getLoggerContext();
        addAppender(loggerContext, appender, loggerContext.getLoggers());
    }

    /**
     * Remove the given {@link Appender} from the specified {@link Logger} instances.
     *
     * @param appender the {@link Appender} to remove
     * @param loggers  the {@link Logger} instances from which the appender is removed
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   Collection<Logger> loggers = Log4j2Utils.getLoggers();
     *   Log4j2Utils.removeAppender(appender, loggers);
     * }</pre>
     */
    public static void removeAppender(Appender appender, Iterable<Logger> loggers) {
        LoggerContext loggerContext = getLoggerContext();
        removeAppender(loggerContext, appender, loggers);
    }

    /**
     * Remove the given {@link Appender} from all {@link Logger} instances in the current {@link LoggerContext}.
     *
     * @param appender the {@link Appender} to remove from all loggers
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = Log4j2Utils.findAppender(InMemoryAppender.NAME);
     *   Log4j2Utils.removeAppenderForAllLoggers(appender);
     * }</pre>
     */
    public static void removeAppenderForAllLoggers(Appender appender) {
        LoggerContext loggerContext = getLoggerContext();
        removeAppender(loggerContext, appender, loggerContext.getLoggers());
    }

    static void addAppender(LoggerContext loggerContext, Appender appender, Iterable<Logger> loggers) {
        if (loggerContext == null || appender == null || loggers == null) {
            return;
        }
        Configuration configuration = loggerContext.getConfiguration();
        synchronized (loggerContext) {
            if (!appender.isStarted()) {
                appender.start();
            }
            configuration.addAppender(appender);
            for (Logger logger : loggers) {
                LoggerConfig loggerConfig = logger.get();
                if (loggerConfig != null) {
                    loggerConfig.addAppender(appender, null, null);
                }
            }
            loggerContext.updateLoggers();
        }
    }

    static void removeAppender(LoggerContext loggerContext, Appender appender, Iterable<Logger> loggers) {
        if (loggerContext == null || appender == null || loggers == null) {
            return;
        }
        Configuration configuration = loggerContext.getConfiguration();
        String appenderName = appender.getName();
        synchronized (loggerContext) {
            if (!appender.isStopped()) {
                appender.stop();
            }
            configuration.getAppenders().remove(appenderName);
            for (Logger logger : loggers) {
                LoggerConfig loggerConfig = logger.get();
                if (loggerConfig != null) {
                    loggerConfig.removeAppender(appenderName);
                }
            }
            loggerContext.updateLoggers();
        }
    }

    private Log4j2Utils() {
    }
}
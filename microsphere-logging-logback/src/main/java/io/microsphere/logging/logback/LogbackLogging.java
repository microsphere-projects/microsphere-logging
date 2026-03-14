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

package io.microsphere.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.microsphere.logging.Logging;

import java.util.List;
import java.util.Set;

import static ch.qos.logback.classic.Level.toLevel;
import static io.microsphere.constants.SymbolConstants.DOT;
import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static io.microsphere.util.StringUtils.substringBeforeLast;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;
import static org.slf4j.LoggerFactory.getILoggerFactory;

/**
 * {@link Logging} class based on the logback framework.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @since 1.0.0
 */
public class LogbackLogging implements Logging {

    static final LoggerContext loggerContext = (LoggerContext) getILoggerFactory();

    @Override
    public List<String> getLoggerNames() {
        return loggerContext.getLoggerList()
                .stream().map(Logger::getName)
                .collect(toList());
    }

    @Override
    public Set<String> getSupportedLoggingLevels() {
        return INSTANCE.resolve(Level.class);
    }

    @Override
    public String getLoggerLevel(String loggerName) {
        Logger logger = getLogger(loggerName);
        Level level = null;
        if (logger != null) {
            level = logger.getLevel();
            if (level == null) {
                level = logger.getEffectiveLevel();
            }
        }
        return valueOf(level);
    }

    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        Logger logger = getLogger(loggerName);
        setLoggerLevel(logger, levelName);
    }

    protected void setLoggerLevel(Logger logger, String levelName) {
        if (logger != null) {
            logger.setLevel(toLevel(levelName));
        }
    }

    @Override
    public String getParentLoggerName(String loggerName) {
        if (ROOT_LOGGER_NAME.equals(loggerName)) {
            return null;
        }
        String parentLoggerName = substringBeforeLast(loggerName, DOT);
        if (!loggerName.equals(parentLoggerName)) {
            Logger logger = getLogger(parentLoggerName);
            if (logger != null) {
                return parentLoggerName;
            }
        }
        return ROOT_LOGGER_NAME;
    }

    @Override
    public String getName() {
        return "Logback";
    }

    protected Logger getLogger(String loggerName) {
        return loggerContext.getLogger(loggerName);
    }
}

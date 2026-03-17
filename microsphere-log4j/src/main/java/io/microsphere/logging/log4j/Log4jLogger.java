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

package io.microsphere.logging.log4j;

import io.microsphere.lang.DelegatingWrapper;
import io.microsphere.logging.AbstractLogger;
import org.apache.log4j.Logger;

import static io.microsphere.logging.log4j.util.LoggerUtils.getLogger;
import static org.apache.log4j.Level.ERROR;
import static org.apache.log4j.Level.WARN;

/**
 * The Logger adapter class based Log4j {@link Logger}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AbstractLogger
 * @see Logger
 * @since 1.0.0
 */
class Log4jLogger extends AbstractLogger implements DelegatingWrapper {

    private final Logger logger;

    public Log4jLogger(String loggerName) {
        super(loggerName);
        this.logger = getLogger(loggerName);
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String message) {
        this.logger.trace(message);
    }

    @Override
    public void trace(String message, Throwable t) {
        this.logger.trace(message, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String message) {
        this.logger.debug(message);

    }

    @Override
    public void debug(String message, Throwable t) {
        this.logger.debug(message, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void info(String message, Throwable t) {
        this.logger.info(message, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isEnabledFor(WARN);
    }

    @Override
    public void warn(String message) {
        this.logger.warn(message);
    }

    @Override
    public void warn(String message, Throwable t) {
        this.logger.warn(message, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isEnabledFor(ERROR);
    }

    @Override
    public void error(String message) {
        this.logger.error(message);
    }

    @Override
    public void error(String message, Throwable t) {
        this.logger.error(message, t);
    }

    @Override
    public Object getDelegate() {
        return this.logger;
    }
}
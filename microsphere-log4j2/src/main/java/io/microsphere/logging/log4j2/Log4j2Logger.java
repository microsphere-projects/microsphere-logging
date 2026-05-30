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

import io.microsphere.lang.DelegatingWrapper;
import io.microsphere.logging.AbstractLogger;
import org.apache.logging.log4j.Logger;

import static io.microsphere.logging.log4j2.util.Log4j2Utils.getLogger;

/**
 * The Logger adapter class based Log4j2 {@link Logger}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AbstractLogger
 * @see Logger
 * @since 1.0.0
 */
class Log4j2Logger extends AbstractLogger implements DelegatingWrapper {

    private final Logger logger;

    /**
     * Creates a new {@link Log4j2Logger} for the given logger name.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     * }</pre>
     * @param loggerName the name of the logger
     *
     */
    public Log4j2Logger(String loggerName) {
        super(loggerName);
        this.logger = getLogger(loggerName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   if (logger.isTraceEnabled()) {
     *     logger.trace("trace message");
     *   }
     * }</pre>
     */
    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.trace("entering method foo");
     * }</pre>
     */
    @Override
    public void trace(String message) {
        this.logger.trace(message);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.trace("trace with exception", new RuntimeException("cause"));
     * }</pre>
     */
    @Override
    public void trace(String message, Throwable t) {
        this.logger.trace(message, t);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   if (logger.isDebugEnabled()) {
     *     logger.debug("debug message");
     *   }
     * }</pre>
     */
    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.debug("processing item: {}", item);
     * }</pre>
     */
    @Override
    public void debug(String message) {
        this.logger.debug(message);

    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.debug("debug with exception", new RuntimeException("cause"));
     * }</pre>
     */
    @Override
    public void debug(String message, Throwable t) {
        this.logger.debug(message, t);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   if (logger.isInfoEnabled()) {
     *     logger.info("application started");
     *   }
     * }</pre>
     */
    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.info("application started");
     * }</pre>
     */
    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.info("info with exception", new RuntimeException("cause"));
     * }</pre>
     */
    @Override
    public void info(String message, Throwable t) {
        this.logger.info(message, t);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   if (logger.isWarnEnabled()) {
     *     logger.warn("low memory");
     *   }
     * }</pre>
     */
    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.warn("unexpected configuration value");
     * }</pre>
     */
    @Override
    public void warn(String message) {
        this.logger.warn(message);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.warn("warn with exception", new RuntimeException("cause"));
     * }</pre>
     */
    @Override
    public void warn(String message, Throwable t) {
        this.logger.warn(message, t);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   if (logger.isErrorEnabled()) {
     *     logger.error("operation failed");
     *   }
     * }</pre>
     */
    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.error("operation failed");
     * }</pre>
     */
    @Override
    public void error(String message) {
        this.logger.error(message);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   logger.error("error with exception", new RuntimeException("cause"));
     * }</pre>
     */
    @Override
    public void error(String message, Throwable t) {
        this.logger.error(message, t);
    }

    /**
     * Returns the underlying Log4j2 {@link Logger} delegate.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2Logger logger = new Log4j2Logger("io.microsphere");
     *   org.apache.logging.log4j.Logger delegate = (org.apache.logging.log4j.Logger) logger.getDelegate();
     * }</pre>
     */
    @Override
    public Object getDelegate() {
        return this.logger;
    }
}

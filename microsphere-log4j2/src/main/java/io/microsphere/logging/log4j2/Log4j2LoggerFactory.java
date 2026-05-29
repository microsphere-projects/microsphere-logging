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

import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

import static io.microsphere.logging.log4j2.Log4j2Logging.PRIORITY;

/**
 * {@link LoggerFactory} for Log4j2
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggerFactory
 * @since 1.0.0
 */
public class Log4j2LoggerFactory extends LoggerFactory {

    public static final String LOG4J2_LOGGER_CLASS_NAME = "org.apache.logging.log4j.Logger";

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2LoggerFactory factory = new Log4j2LoggerFactory();
     *   String className = factory.getDelegateLoggerClassName();
     *   // returns "org.apache.logging.log4j.Logger"
     * }</pre>
     */
    @Override
    protected String getDelegateLoggerClassName() {
        return LOG4J2_LOGGER_CLASS_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2LoggerFactory factory = new Log4j2LoggerFactory();
     *   Logger logger = factory.createLogger("io.microsphere");
     * }</pre>
     */
    @Override
    public Logger createLogger(String name) {
        return new Log4j2Logger(name);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4j2LoggerFactory factory = new Log4j2LoggerFactory();
     *   int priority = factory.getPriority();
     *   // returns Log4j2Logging.PRIORITY
     * }</pre>
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}

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

import io.microsphere.lang.DelegatingWrapper;
import io.microsphere.logging.Logging;

import java.util.List;
import java.util.logging.LoggingMXBean;

/**
 * {@link LoggingMXBean} Adapter based on the {@link Logging} delegate.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingMXBean
 * @see Logging
 * @since 1.0.0
 */
public class LoggingMXBeanAdapter implements LoggingMXBean, DelegatingWrapper {

    private final Logging logging;

    /**
     * Creates a new {@link LoggingMXBeanAdapter} wrapping the given {@link Logging} instance.
     *
     * @param logging the {@link Logging} delegate; must not be {@code null}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     * }</pre>
     */
    public LoggingMXBeanAdapter(Logging logging) {
        this.logging = logging;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     *   List<String> names = adapter.getLoggerNames();
     * }</pre>
     */
    @Override
    public List<String> getLoggerNames() {
        return this.logging.getLoggerNames();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     *   String level = adapter.getLoggerLevel("io.microsphere");
     * }</pre>
     */
    @Override
    public String getLoggerLevel(String loggerName) {
        return this.logging.getLoggerLevel(loggerName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     *   adapter.setLoggerLevel("io.microsphere", "DEBUG");
     * }</pre>
     */
    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        this.logging.setLoggerLevel(loggerName, levelName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     *   String parentName = adapter.getParentLoggerName("io.microsphere.logging");
     *   // returns "io.microsphere"
     * }</pre>
     */
    @Override
    public String getParentLoggerName(String loggerName) {
        return this.logging.getParentLoggerName(loggerName);
    }

    /**
     * Returns the underlying {@link Logging} delegate.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     *   Logging delegate = (Logging) adapter.getDelegate();
     * }</pre>
     */
    @Override
    public Object getDelegate() {
        return this.logging;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     *   int hash = adapter.hashCode();
     * }</pre>
     */
    @Override
    public int hashCode() {
        return this.logging.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter1 = new LoggingMXBeanAdapter(logging);
     *   LoggingMXBeanAdapter adapter2 = new LoggingMXBeanAdapter(logging);
     *   boolean equal = adapter1.equals(adapter2);
     * }</pre>
     */
    @Override
    public boolean equals(Object obj) {
        return this.logging.equals(obj);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Logging logging = LoggingUtils.load();
     *   LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);
     *   String str = adapter.toString();
     *   // e.g. "LoggingMXBeanAdapter[Log4j2]"
     * }</pre>
     */
    @Override
    public String toString() {
        return "LoggingMXBeanAdapter[" + logging + "]";
    }
}
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

    public LoggingMXBeanAdapter(Logging logging) {
        this.logging = logging;
    }

    @Override
    public List<String> getLoggerNames() {
        return this.logging.getLoggerNames();
    }

    @Override
    public String getLoggerLevel(String loggerName) {
        return this.logging.getLoggerLevel(loggerName);
    }

    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        this.logging.setLoggerLevel(loggerName, levelName);
    }

    @Override
    public String getParentLoggerName(String loggerName) {
        return this.logging.getParentLoggerName(loggerName);
    }

    @Override
    public Object getDelegate() {
        return this.logging;
    }

    @Override
    public int hashCode() {
        return this.logging.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.logging.equals(obj);
    }

    @Override
    public String toString() {
        return "LoggingMXBeanAdapter[" + logging + "]";
    }
}
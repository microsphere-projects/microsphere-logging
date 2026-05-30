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
package io.microsphere.logging.log4j2.appender;

import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

import static io.microsphere.collection.SetUtils.newConcurrentSkipListSet;
import static io.microsphere.logging.log4j2.LogEventComparator.INSTANCE;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.findAppender;

/**
 * In-Memory {@link Appender} records any {@link LogEvent}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Appender
 * @since 1.0.0
 */
public class InMemoryAppender extends AbstractLifeCycle implements Appender {

    /**
     * The name of {@link InMemoryAppender}
     */
    public static final String NAME = "InMemory";

    private final ConcurrentSkipListSet<LogEvent> logEvents = newConcurrentSkipListSet(INSTANCE);

    /**
     * Appends the given {@link LogEvent} to the in-memory collection.
     *
     * @param event the {@link LogEvent} to record
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   appender.start();
     *   LogEvent event = Log4jLogEvent.newBuilder()
     *       .setLoggerName("test.logger")
     *       .setLevel(Level.INFO)
     *       .setMessage(new SimpleMessage("hello"))
     *       .build();
     *   appender.append(event);
     * }</pre>
     */
    @Override
    public void append(LogEvent event) {
        logEvents.add(event);
    }

    /**
     * Returns the name of this appender, which is {@value #NAME}.
     *
     * @return the constant name {@value #NAME}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   String name = appender.getName(); // "InMemory"
     * }</pre>
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Returns {@code null} because this appender does not use a {@link Layout}.
     *
     * @return always {@code null}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   Layout<?> layout = appender.getLayout(); // null
     * }</pre>
     */
    @Override
    public Layout<? extends Serializable> getLayout() {
        return null;
    }

    /**
     * Returns {@code false} because this appender does not silently ignore exceptions.
     *
     * @return always {@code false}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   boolean ignore = appender.ignoreExceptions(); // false
     * }</pre>
     */
    @Override
    public boolean ignoreExceptions() {
        return false;
    }

    /**
     * Returns {@code null} because this appender does not use an {@link ErrorHandler}.
     *
     * @return always {@code null}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   ErrorHandler handler = appender.getHandler(); // null
     * }</pre>
     */
    @Override
    public ErrorHandler getHandler() {
        return null;
    }

    /**
     * No-op: this appender does not support setting an {@link ErrorHandler}.
     *
     * @param handler ignored
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   appender.setHandler(null); // no-op
     * }</pre>
     */
    @Override
    public void setHandler(ErrorHandler handler) {
    }

    /**
     * Initializes the appender lifecycle.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   appender.initialize();
     * }</pre>
     */
    @Override
    public void initialize() {
        super.initialize();
    }

    /**
     * Starts the appender lifecycle.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   appender.start();
     *   // appender is now ready to record events
     * }</pre>
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Stops the appender lifecycle and clears all recorded {@link LogEvent} instances.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = new InMemoryAppender();
     *   appender.start();
     *   appender.append(event);
     *   appender.stop(); // clears buffered events
     * }</pre>
     */
    @Override
    public void stop() {
        super.stop();
        this.logEvents.clear();
    }

    /**
     * Transfer all log events to another {@link Appender}
     *
     * @param appender another {@link Appender}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender source = new InMemoryAppender();
     *   source.start();
     *   source.append(event);
     *   FileAppender target = ...;
     *   source.transfer(target);
     * }</pre>
     */
    public void transfer(Appender appender) {
        LogEvent logEvent;
        while ((logEvent = logEvents.pollFirst()) != null) {
            appender.append(logEvent);
        }
    }

    /**
     * Finds and returns the {@link InMemoryAppender} registered in the current Log4j2 configuration,
     * or {@code null} if none is registered.
     *
     * @return the registered {@link InMemoryAppender}, or {@code null} if not found
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   InMemoryAppender appender = InMemoryAppender.findInMemoryAppender();
     *   if (appender != null) {
     *     appender.transfer(anotherAppender);
     *   }
     * }</pre>
     */
    public static InMemoryAppender findInMemoryAppender() {
        return findAppender(NAME);
    }
}

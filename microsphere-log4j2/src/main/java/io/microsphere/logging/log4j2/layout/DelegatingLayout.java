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
package io.microsphere.logging.log4j2.layout;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;

import java.io.Serializable;
import java.util.Map;

/**
 * Delegating {@link Layout}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Layout
 * @since 1.0.0
 */
public class DelegatingLayout<T extends Serializable> implements Layout<T> {

    private Layout<T> delegate;

    /**
     * Creates a new {@link DelegatingLayout} wrapping the given delegate {@link Layout}.
     *
     * @param delegate the {@link Layout} to delegate to
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Layout<String> patternLayout = PatternLayout.newBuilder().withPattern("%m").build();
     *   DelegatingLayout<String> layout = new DelegatingLayout<>(patternLayout);
     * }</pre>
     */
    public DelegatingLayout(Layout<T> delegate) {
        this.delegate = delegate;
    }

    /**
     * Creates a new {@link DelegatingLayout} with no delegate set. The delegate must be
     * provided via {@link #setDelegate(Layout)} before use.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<String> layout = new DelegatingLayout<>();
     *   layout.setDelegate(PatternLayout.newBuilder().withPattern("%m").build());
     * }</pre>
     */
    public DelegatingLayout() {
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<?> layout = new DelegatingLayout<>(delegate);
     *   byte[] footer = layout.getFooter();
     * }</pre>
     */
    @Override
    public byte[] getFooter() {
        return delegate.getFooter();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<?> layout = new DelegatingLayout<>(delegate);
     *   byte[] header = layout.getHeader();
     * }</pre>
     */
    @Override
    public byte[] getHeader() {
        return delegate.getHeader();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<?> layout = new DelegatingLayout<>(delegate);
     *   byte[] bytes = layout.toByteArray(logEvent);
     * }</pre>
     */
    @Override
    public byte[] toByteArray(LogEvent event) {
        return delegate.toByteArray(event);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<?> layout = new DelegatingLayout<>(delegate);
     *   Object serialized = layout.toSerializable(logEvent);
     * }</pre>
     */
    @Override
    public T toSerializable(LogEvent event) {
        return delegate.toSerializable(event);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<?> layout = new DelegatingLayout<>(delegate);
     *   String contentType = layout.getContentType();
     * }</pre>
     */
    @Override
    public String getContentType() {
        return delegate.getContentType();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<?> layout = new DelegatingLayout<>(delegate);
     *   Map<String, String> format = layout.getContentFormat();
     * }</pre>
     */
    @Override
    public Map<String, String> getContentFormat() {
        return delegate.getContentFormat();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<?> layout = new DelegatingLayout<>(delegate);
     *   layout.encode(logEvent, destination);
     * }</pre>
     */
    @Override
    public void encode(LogEvent source, ByteBufferDestination destination) {
        delegate.encode(source, destination);
    }

    /**
     * Returns the underlying delegate {@link Layout}.
     *
     * @return the delegate {@link Layout}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<String> layout = new DelegatingLayout<>(patternLayout);
     *   Layout<String> delegate = layout.getDelegate();
     * }</pre>
     */
    public Layout<T> getDelegate() {
        return delegate;
    }

    /**
     * Sets a new delegate {@link Layout}.
     *
     * @param delegate the new delegate {@link Layout}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   DelegatingLayout<String> layout = new DelegatingLayout<>();
     *   layout.setDelegate(PatternLayout.newBuilder().withPattern("%d %m").build());
     * }</pre>
     */
    public void setDelegate(Layout<T> delegate) {
        this.delegate = delegate;
    }
}

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

package io.microsphere.logging;

import io.microsphere.annotation.Nonnull;
import io.microsphere.annotation.Nullable;
import io.microsphere.lang.Prioritized;

import java.util.List;

import static io.microsphere.util.ClassLoaderUtils.getClassLoader;
import static io.microsphere.util.ServiceLoaderUtils.loadFirstService;
import static io.microsphere.util.ServiceLoaderUtils.loadServicesList;

/**
 * The Utilities class of Logging
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @since 1.0.0
 */
public abstract class LoggingUtils {

    /**
     * Load all {@link Logging} implementations in priority from SPI
     *
     * @return non-null
     */
    @Nonnull
    public static List<Logging> loadAll() {
        return loadAll(getClassLoader(LoggingUtils.class));
    }

    /**
     * Load all {@link Logging} implementations in priority from SPI
     *
     * @param classLoader the {@link ClassLoader} which is used to load {@link Logging} implementations
     * @return non-null
     * @throws IllegalArgumentException if no {@link Logging} was declared in META-INF/services
     */
    @Nonnull
    public static List<Logging> loadAll(@Nullable ClassLoader classLoader) throws IllegalArgumentException {
        return loadServicesList(Logging.class, classLoader, true);
    }

    /**
     * Load the highest {@link Prioritized priority} {@link Logging} from SPI
     *
     * @return non-null
     * @throws IllegalArgumentException
     */
    @Nonnull
    public static Logging load() throws IllegalArgumentException {
        return load(getClassLoader(LoggingUtils.class));
    }

    /**
     * Load the highest {@link Prioritized priority} {@link Logging} from SPI
     *
     * @param classLoader the {@link ClassLoader} which is used to load {@link Logging} implementations
     * @return non-null
     * @throws IllegalArgumentException
     */
    @Nonnull
    public static Logging load(@Nullable ClassLoader classLoader) throws IllegalArgumentException {
        return loadFirstService(Logging.class, classLoader, true);
    }

    private LoggingUtils() {
    }
}
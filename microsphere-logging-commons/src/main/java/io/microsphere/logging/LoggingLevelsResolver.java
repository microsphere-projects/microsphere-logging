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

import java.util.Set;

/**
 * The class to resolve all log levels for the specified logging framework.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging#getSupportedLoggingLevels()
 * @since 1.0.0
 */
public interface LoggingLevelsResolver {

    /**
     * Get all logging levels based on the specified logging level class.
     *
     * @param levelClass the specified logging level class , for example the {@link Class} {@link java.util.logging.Level}
     * @return allall logging levels , for example [TRACE , DEBUG , INFO , WARN , ERROR]
     */
    @Nonnull
    Set<String> resolve(Class<?> levelClass);
}
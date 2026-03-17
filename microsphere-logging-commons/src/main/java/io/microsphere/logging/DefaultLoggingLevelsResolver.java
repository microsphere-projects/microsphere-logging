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

import io.microsphere.reflect.MemberUtils;

import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

/**
 * The default implementation class of {@link LoggingLevelsResolver}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingLevelsResolver
 * @since 1.0.0
 */
public class DefaultLoggingLevelsResolver implements LoggingLevelsResolver {

    public static DefaultLoggingLevelsResolver INSTANCE = new DefaultLoggingLevelsResolver();

    protected DefaultLoggingLevelsResolver() {
    }

    /**
     * Resolve the supported logging levels from the specified logging level class
     * <p>
     * For example , the {@link java.util.logging.Level} class has many fields , but only some of them are static and final ,
     * such as SEVERE , WARNING , INFO , etc. , these fields represent the supported logging levels.
     *
     * @param levelClass the specified logging level class , for example the {@link Class} {@link java.util.logging.Level}
     * @return non-null
     */
    @Override
    public Set<String> resolve(Class<?> levelClass) {
        return unmodifiableSet(of(levelClass.getFields())
                .filter(MemberUtils::isStatic)
                .filter(MemberUtils::isFinal)
                .filter(field -> field.getType().equals(levelClass))
                .map(field -> field.getName())
                .collect(toSet()));
    }
}
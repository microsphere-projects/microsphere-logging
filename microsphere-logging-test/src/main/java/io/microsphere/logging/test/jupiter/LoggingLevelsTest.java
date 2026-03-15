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

package io.microsphere.logging.test.jupiter;

import io.microsphere.logging.test.jupiter.extension.logging.LoggingLevelsTestExtension;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link TestTemplate @TestTemplate} for Logging Levels.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see TestTemplate
 * @since 1.0.0
 */
@Target({ANNOTATION_TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
@Documented
@TestTemplate
@ExtendWith(LoggingLevelsTestExtension.class)
public @interface LoggingLevelsTest {

    /**
     * The target class(es) which will be applied Logging Levels
     *
     * @return non-null
     */
    Class<?>[] loggingClasses() default {};

    /**
     * The Logging Levels
     *
     * @return the Logging Levels , for example : "DEBUG" , "INFO" , "WARN" , "ERROR" , "TRACE"
     */
    String[] levels();
}
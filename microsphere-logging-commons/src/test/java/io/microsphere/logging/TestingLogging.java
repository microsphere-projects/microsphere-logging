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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.microsphere.collection.ListUtils.ofList;
import static io.microsphere.collection.MapUtils.ofMap;
import static io.microsphere.logging.DefaultLoggingLevelsResolverTest.JAVA_LOGGING_LEVELS;

/**
 * Testing {@link Logging}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @since 1.0.0
 */
public class TestingLogging implements Logging {

    private final Map<String, String> loggerNameToLevelMap = new HashMap<>();

    @Override
    public List<String> getLoggerNames() {
        return ofList(this.loggerNameToLevelMap.keySet());
    }

    @Override
    public Set<String> getSupportedLoggingLevels() {
        return JAVA_LOGGING_LEVELS;
    }

    @Override
    public String getLoggerLevel(String loggerName) {
        return this.loggerNameToLevelMap.get(loggerName);
    }

    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        this.loggerNameToLevelMap.put(loggerName, levelName);
    }

    @Override
    public String getParentLoggerName(String loggerName) {
        return "";
    }

    public void init(String... loggerNamesAndLevels) {
        this.loggerNameToLevelMap.putAll(ofMap(loggerNamesAndLevels));
    }

    @Override
    public String getName() {
        return "Testing";
    }
}
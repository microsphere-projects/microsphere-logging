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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.Test;

import static io.microsphere.logging.log4j2.LogEventComparator.INSTANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LogEventComparator} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LogEventComparator
 * @since 1.0.0
 */
class LogEventComparatorTest {

    @Test
    void testInstanceIsSingleton() {
        assertNotNull(INSTANCE);
    }

    @Test
    void testCompareEarlierThanLater() {
        LogEvent earlier = newEventWithMillis(1000L);
        LogEvent later = newEventWithMillis(2000L);

        assertTrue(INSTANCE.compare(earlier, later) < 0);
    }

    @Test
    void testCompareLaterThanEarlier() {
        LogEvent earlier = newEventWithMillis(1000L);
        LogEvent later = newEventWithMillis(2000L);

        assertTrue(INSTANCE.compare(later, earlier) > 0);
    }

    @Test
    void testCompareEqualTimes() {
        LogEvent event1 = newEventWithMillis(1000L);
        LogEvent event2 = newEventWithMillis(1000L);

        assertEquals(0, INSTANCE.compare(event1, event2));
    }

    @Test
    void testCompareSameInstance() {
        LogEvent event = newEventWithMillis(1000L);

        assertEquals(0, INSTANCE.compare(event, event));
    }

    @Test
    void testCompareWithNanoOfMillisecondPrecision() {
        LogEvent event1 = newEventWithInstant(1000L, 100);
        LogEvent event2 = newEventWithInstant(1000L, 200);

        assertTrue(INSTANCE.compare(event1, event2) < 0);
        assertTrue(INSTANCE.compare(event2, event1) > 0);
        assertEquals(0, INSTANCE.compare(event1, event1));
    }

    @Test
    void testCompareNanoOfMillisecondEqualToZero() {
        LogEvent event1 = newEventWithInstant(1000L, 0);
        LogEvent event2 = newEventWithInstant(1000L, 0);

        assertEquals(0, INSTANCE.compare(event1, event2));
    }

    private static LogEvent newEventWithMillis(long timeMillis) {
        return Log4jLogEvent.newBuilder()
                .setLoggerName("test.logger")
                .setLoggerFqcn(LogEventComparatorTest.class.getName())
                .setLevel(Level.INFO)
                .setMessage(new SimpleMessage("test"))
                .setTimeMillis(timeMillis)
                .build();
    }

    private static LogEvent newEventWithInstant(long epochMillis, int nanoOfMillisecond) {
        MutableInstant instant = new MutableInstant();
        instant.initFromEpochMilli(epochMillis, nanoOfMillisecond);
        return Log4jLogEvent.newBuilder()
                .setLoggerName("test.logger")
                .setLoggerFqcn(LogEventComparatorTest.class.getName())
                .setLevel(Level.INFO)
                .setMessage(new SimpleMessage("test"))
                .setInstant(instant)
                .build();
    }
}

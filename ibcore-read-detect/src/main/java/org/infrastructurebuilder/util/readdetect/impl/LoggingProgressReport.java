/*
 * @formatter:off
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @formatter:on
 */
package org.infrastructurebuilder.util.readdetect.impl;

import java.net.URI;

import org.slf4j.Logger;

/**
 * {@link ProgressReport} implementation that logs operation progress at INFO priority.
 */
public final class LoggingProgressReport implements ProgressReport {

    private static final long KBYTE = 1024L;
    private static final char K_UNIT = 'K';
    private static final char B_UNIT = 'b';

    private final Logger log;

    private char unit;
    private long total;
    private long completed;

    public LoggingProgressReport(Logger log) {
        this.log = log;
    }

    @Override
    public void initiate(URI uri, long total) {
        this.total = total;
        this.completed = 0L;
        this.unit = total >= KBYTE ? K_UNIT : B_UNIT;
        log.info(String.format( "%s: %s", "Downloading", uri));
    }

    @Override
    public void update(long bytesRead) {
        completed += bytesRead;
        final String totalInUnits;
        final long completedInUnits;
        if (unit == K_UNIT) {
            totalInUnits = total == -1 ? "?" : Long.toString(total / KBYTE) + unit;
            completedInUnits = completed / KBYTE;
        } else {
            totalInUnits = total == -1 ? "?" : Long.toString(total);
            completedInUnits = completed;
        }
        log.info(String.format("%d/%s", completedInUnits, totalInUnits));
    }

    @Override
    public void completed() {
        log.info(String.format("%s %s",
                "downloaded",
                unit == K_UNIT ? Long.toString(completed / KBYTE) + unit : Long.toString(completed)));
    }

    @Override
    public void error(Exception ex) {
        log.error("error",ex);
    }

}

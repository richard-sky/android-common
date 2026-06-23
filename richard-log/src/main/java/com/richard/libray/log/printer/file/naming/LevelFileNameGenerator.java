/*
 * Copyright 2015 Elvis Hew
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
 */

package com.richard.libray.log.printer.file.naming;

import android.text.TextUtils;

import com.richard.libray.log.LogLevel;

/**
 * Generate file name according to the log level, different levels lead to different file names.
 */
public class LevelFileNameGenerator implements FileNameGenerator {

    @Override
    public boolean isFileNameChangeable() {
        return true;
    }

    /**
     * Generate a file name which represent a specific log level.
     */
    @Override
    public String generateFileName(String bizType, int logLevel, long timestamp) {
        if (TextUtils.isEmpty(bizType)) {
            return LogLevel.getLevelName(logLevel);
        }
        return String.format("%s_%s.%s", LogLevel.getLevelName(logLevel), bizType, extensionName);
    }
}

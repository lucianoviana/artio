/*
 * Copyright 2015-2025 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.artio.library;

public interface LibraryConnectHandler
{
    /**
     * Invoked when the {@link FixLibrary} instance this is registered on completes a connect.
     *
     * @param library the library that has connected
     */
    void onConnect(FixLibrary library);

    /**
     * Invoked when the {@link FixLibrary} instance this is registered get's disconnected.
     *
     * @param library the library that has disconnected
     */
    void onDisconnect(FixLibrary library);
}

/**
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
package io.methvin.watcher;

import java.io.IOException;

@FunctionalInterface
public interface DirectoryChangeListener {

  void onEvent(DirectoryChangeEvent event) throws IOException;

  default boolean isWatching() {
    return true;
  }

  // A handler for uncaught exceptions. This can rethrow the exception to terminate the watcher.
  default void onException(Exception e) {
    DirectoryWatcher.logger.debug("Got exception while watching", e);
  }
}

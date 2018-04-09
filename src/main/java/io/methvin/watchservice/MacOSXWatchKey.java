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
package io.methvin.watchservice;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.Watchable;
import java.util.concurrent.atomic.AtomicBoolean;

class MacOSXWatchKey extends AbstractWatchKey {
  private final AtomicBoolean cancelled = new AtomicBoolean(false);
  private final boolean reportCreateEvents;
  private final boolean reportModifyEvents;
  private final boolean reportDeleteEvents;

  public MacOSXWatchKey(AbstractWatchService macOSXWatchService, Iterable<? extends WatchEvent.Kind<?>> events, int queueSize) {
    super(macOSXWatchService, null, events, queueSize);
    boolean reportCreateEvents = false;
    boolean reportModifyEvents = false;
    boolean reportDeleteEvents = false;

    for (WatchEvent.Kind<?> event : events) {
      if (event == StandardWatchEventKinds.ENTRY_CREATE) {
        reportCreateEvents = true;
      } else if (event == StandardWatchEventKinds.ENTRY_MODIFY) {
        reportModifyEvents = true;
      } else if (event == StandardWatchEventKinds.ENTRY_DELETE) {
        reportDeleteEvents = true;
      }
    }
    this.reportCreateEvents = reportCreateEvents;
    this.reportDeleteEvents = reportDeleteEvents;
    this.reportModifyEvents = reportModifyEvents;
  }

  @Override
  public void cancel() {
    cancelled.set(true);
  }

  public boolean isReportCreateEvents() {
    return reportCreateEvents;
  }

  public boolean isReportModifyEvents() {
    return reportModifyEvents;
  }

  public boolean isReportDeleteEvents() {
    return reportDeleteEvents;
  }

  @Override
  public Watchable watchable() {
    return null;
  }
}

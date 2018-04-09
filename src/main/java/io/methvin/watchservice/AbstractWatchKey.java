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

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import io.methvin.watchservice.AbstractWatchService.Event;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link WatchKey} for an {@link AbstractWatchService}.
 */
class AbstractWatchKey implements WatchKey {

  private static WatchEvent<Object> overflowEvent(int count) {
    return new Event<>(OVERFLOW, count, null);
  }

  private final AbstractWatchService watcher;
  private final Watchable watchable;
  private final Set<Kind<?>> subscribedTypes;

  private final AtomicReference<AbstractWatchKey.State> state = new AtomicReference<>(State.READY);
  private final AtomicBoolean valid = new AtomicBoolean(true);
  private final AtomicInteger overflow = new AtomicInteger();

  private final BlockingQueue<WatchEvent<?>> events;

  public AbstractWatchKey(
    AbstractWatchService watcher,
    @Nullable Watchable watchable,
    Iterable<? extends WatchEvent.Kind<?>> subscribedTypes,
    int queueSize
  ) {
    this.watcher = requireNonNull(watcher);
    this.watchable = watchable; // nullable for Watcher poison
    this.events = new ArrayBlockingQueue<>(queueSize);

    Set<Kind<?>> types = new HashSet<Kind<?>>();
    subscribedTypes.forEach(types::add);
    this.subscribedTypes = Collections.unmodifiableSet(types);
  }

  /**
   * Gets the current state of this key, State.READY or SIGNALLED.
   */
  AbstractWatchKey.State state() {
    return state.get();
  }

  /**
   * Gets whether or not this key is subscribed to the given type of event.
   */
  public boolean subscribesTo(WatchEvent.Kind<?> eventType) {
    return subscribedTypes.contains(eventType);
  }

  /**
   * Posts the given event to this key. After posting one or more events, {@link #signal()} must
   * be called to cause the key to be enqueued with the watch service.
   */
  public void post(WatchEvent<?> event) {
    if (!events.offer(event)) {
      overflow.incrementAndGet();
    }
  }

  /**
   * Sets the state to SIGNALLED and enqueues this key with the watcher if it was previously in
   * the READY state.
   */
  public void signal() {
    if (state.getAndSet(State.SIGNALLED) == State.READY) {
      watcher.enqueue(this);
    }
  }

  @Override
  public boolean isValid() {
    return watcher.isOpen() && valid.get();
  }

  @Override
  public List<WatchEvent<?>> pollEvents() {
    // note: it's correct to be able to retrieve more events from a key without calling reset()
    // reset() is ONLY for "returning" the key to the watch service to potentially be retrieved by
    // another thread when you're finished with it
    List<WatchEvent<?>> result = new ArrayList<>(events.size());
    events.drainTo(result);
    int overflowCount = overflow.getAndSet(0);
    if (overflowCount != 0) {
      result.add(overflowEvent(overflowCount));
    }
    return Collections.unmodifiableList(result);
  }

  @Override
  public boolean reset() {
    // calling reset() multiple times without polling events would cause key to be placed in
    // watcher queue multiple times, but not much that can be done about that
    if (isValid() && state.compareAndSet(State.SIGNALLED, State.READY)) {
      // requeue if events are pending
      if (!events.isEmpty()) {
        signal();
      }
    }

    return isValid();
  }

  @Override
  public void cancel() {
    valid.set(false);
    watcher.cancelled(this);
  }

  @Override
  public Watchable watchable() {
    return watchable;
  }

  enum State {
    READY, SIGNALLED
  }

  // WatchEvent not WatchEvent.Kind
  final void signalEvent(WatchEvent.Kind<Path> kind, Path context) {
    post(new Event<>(kind, 1, context));
    signal();
  }
}

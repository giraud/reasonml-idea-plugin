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
package io.methvin.watchservice.jna;

import com.sun.jna.*;

public interface CarbonAPI extends Library {
  CarbonAPI INSTANCE = (CarbonAPI) Native.loadLibrary("Carbon", CarbonAPI.class);

  CFArrayRef CFArrayCreate(
    CFAllocatorRef allocator, // always set to Pointer.NULL
    Pointer[] values,
    CFIndex numValues,
    Void callBacks // always set to Pointer.NULL
  );

  CFStringRef CFStringCreateWithCharacters(
    Void alloc, //  always pass NULL
    char[] chars,
    CFIndex numChars);

  public FSEventStreamRef FSEventStreamCreate(
    Pointer v, // always use Pointer.NULL
    FSEventStreamCallback callback,
    Pointer context, // always use Pointer.NULL
    CFArrayRef pathsToWatch,
    long sinceWhen, // use -1 for events since now
    double latency, // in seconds
    int flags // 0 is good for now

  );

  boolean FSEventStreamStart(FSEventStreamRef streamRef);

  void FSEventStreamStop(FSEventStreamRef streamRef);

  void FSEventStreamScheduleWithRunLoop(FSEventStreamRef streamRef, CFRunLoopRef runLoop, CFStringRef runLoopMode);

  CFRunLoopRef CFRunLoopGetCurrent();

  void CFRunLoopRun();

  void CFRunLoopStop(CFRunLoopRef rl);

  public interface FSEventStreamCallback extends Callback {
    @SuppressWarnings({"UnusedDeclaration"})
    void invoke(FSEventStreamRef streamRef, Pointer clientCallBackInfo, NativeLong numEvents, Pointer eventPaths, Pointer eventFlags, Pointer eventIds);
  }


}

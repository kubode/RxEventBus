RxEventBus
===

Simple event bus using RxJava.

[![GitHub release](https://img.shields.io/github/release/kubode/RxEventBus.svg?maxAge=2592000)]()
[![Build Status](https://travis-ci.org/kubode/RxEventBus.svg?branch=master)](https://travis-ci.org/kubode/RxEventBus)
[![codecov](https://codecov.io/gh/kubode/RxEventBus/branch/master/graph/badge.svg)](https://codecov.io/gh/kubode/RxEventBus)
[![license](https://img.shields.io/github/license/kubode/RxEventBus.svg?maxAge=2592000)]()


Usage
---

Add dependency to `build.gradle`.

```gradle
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.kubode:rxeventbus:${latestVersion}'
}
```

Define event class.

```java
public class Event {
    public final int answer;
    public Event(int answer) {
        this.answer = answer;
    }
}
```

Subscribe handler.

```java
RxEventBus bus = new RxEventBus();
// Do unsubscribe() to stop event handling.
Subscription subscription = bus.subscribe(Event.class, { event -> println(event.answer) });

// Supports scheduler.
bus.subscribe(ScheduledEvent.class, { event -> println("scheduled") }, Schedulers.newThread());
```

Post event.

```java
bus.post(new Event(42));
// prints: 42

// Can detect unhandled.
bus.post(new OtherEvent(), { unhandledEvent -> println("unhandled") });
// prints: unhandled
```


License
---

```text
Copyright 2016 Masatoshi Kubode

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

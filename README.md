RxEventBus
===

[![Maven Central](https://img.shields.io/maven-central/v/com.github.kubode/rxeventbus.svg?maxAge=2592000)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.kubode%22%20AND%20a%3A%22rxeventbus%22)

Simple event bus using RxJava.


Usage
---

Add dependency to `build.gradle`.

```gradle
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

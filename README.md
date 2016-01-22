RxEventBus
===

An event bus with RxJava.


Usage
---

Add dependency to `build.gradle`.

```gradle
dependencies {
    compile 'com.github.kubode:rxeventbus:1.0.0'
}
```

Define event class.

```java
public class MyEvent extends Event {
    public final int answer;
    public MyEvent(int answer) {
        this.answer = answer;
    }
}
```

Subscribe handler.

```java
RxEventBus bus = new RxEventBus();
// Do unsubscribe() on stop event handling.
Subscription subscription = bus.subscribe(MyEvent.class, { event -> System.out.println(event.answer) });
// Supports scheduler.
bus.subscribe(MyEvent.class, { event -> System.out.println(event.answer) }, Schedulers.io());
```

Post event.

```java
bus.post(new MyEvent(42));
// Output: 42
bus.post(new OtherEvent(), { unhandledEvent -> System.out.println("unhandled") });
// Output: unhandled
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

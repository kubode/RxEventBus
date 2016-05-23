package com.github.kubode.rxeventbus;

import org.junit.Before;
import org.junit.Test;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static org.junit.Assert.*;

public class RxEventBusTest {

    private RxEventBus bus;

    private final Action1<Event> handledAction = new Action1<Event>() {
        @Override
        public void call(Event event) {
            event.handledCount++;
        }
    };
    private final Action1<Event> unhandledAction = new Action1<Event>() {
        @Override
        public void call(Event event) {
            event.unhandled = true;
        }
    };

    @Before
    public void setUp() {
        bus = new RxEventBus();
    }

    @Test
    public void post_unhandled_do_nothing() {
        Event event = new Event();
        bus.post(event);
        assertEquals(new Event(0, false), event);
    }

    @Test
    public void post_unhandled_call_unhandled_function() {
        Event event = new Event();
        bus.post(event, unhandledAction);
        assertEquals(new Event(0, true), event);
    }

    @Test
    public void post_unhandled_after_unsubscribe() {
        Event event = new Event();
        Subscription subscription = bus.subscribe(Event.class, new FailAction<Event>());
        subscription.unsubscribe();
        bus.post(event, unhandledAction);
        assertEquals(new Event(0, true), event);
    }

    @Test
    public void post_unhandled_with_other_event() {
        Event event = new Event();
        Subscription subscription = bus.subscribe(OtherEvent.class, new FailAction<OtherEvent>());
        bus.post(event, unhandledAction);
        subscription.unsubscribe();
        assertEquals(new Event(0, true), event);
    }

    @Test
    public void post_handled() {
        Event event = new Event();
        Subscription subscription = bus.subscribe(Event.class, handledAction);
        bus.post(event, new FailAction<Event>());
        subscription.unsubscribe();
        assertEquals(new Event(1, false), event);
    }

    @Test
    public void post_handled_2_times() {
        Event event = new Event();
        Subscription subscription = new CompositeSubscription(
                bus.subscribe(Event.class, handledAction),
                bus.subscribe(Event.class, handledAction));
        bus.post(event, new FailAction<Event>());
        subscription.unsubscribe();
        assertEquals(new Event(2, false), event);
    }

    @Test
    public void post_handled_new_thread() throws InterruptedException {
        final Event event = new Event();
        final Thread mainThread = Thread.currentThread();
        final Object lock = new Object();
        Subscription subscription = bus.subscribe(Event.class, new Action1<Event>() {
            @Override
            public void call(Event handledEvent) {
                assertNotEquals(mainThread, Thread.currentThread());
                assertEquals(event, handledEvent);
                handledEvent.handledCount++;
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }, Schedulers.newThread());
        bus.post(event, new FailAction<Event>());
        synchronized (lock) {
            lock.wait(1000);
        }
        subscription.unsubscribe();
        assertEquals(new Event(1, false), event);
    }

    private static class Event {

        int handledCount;
        boolean unhandled;

        Event() {
            this(0, false);
        }

        Event(int handledCount, boolean unhandled) {
            this.handledCount = handledCount;
            this.unhandled = unhandled;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Event event = (Event) o;
            if (handledCount != event.handledCount) return false;
            return unhandled == event.unhandled;
        }

        @Override
        public int hashCode() {
            int result = handledCount;
            result = 31 * result + (unhandled ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "handledCount=" + handledCount +
                    ", unhandled=" + unhandled +
                    '}';
        }
    }

    private static class OtherEvent extends Event {
    }

    private static class FailAction<T> implements Action1<T> {
        @Override
        public void call(T t) {
            fail();
        }
    }
}

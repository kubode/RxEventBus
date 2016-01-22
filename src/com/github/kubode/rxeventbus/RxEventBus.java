package com.github.kubode.rxeventbus;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * An implementation of event bus using {@link PublishSubject}.
 * <p/>
 * MT-Safe.
 * <p/>
 * Not support generics.
 */
public class RxEventBus {
    private final Subject<Event, Event> subject = new SerializedSubject<>(PublishSubject.<Event>create());

    /**
     * Posts an {@link Event} to subscribed handlers.
     * <p/>
     * If no handlers have been subscribed for event's class, unhandled will be called with unhandled event.
     *
     * @param <E>       Type of event.
     * @param event     An event to post.
     * @param unhandled Will be called if event is not handled.
     *                  Note: If handler subscribed by using async {@link Scheduler}, it can't guarantee event is actually handled.
     */
    public <E extends Event> void post(E event, Action1<E> unhandled) {
        subject.onNext(event);
        if (event.handledCount == 0) {
            unhandled.call(event);
        }
    }

    /**
     * An overload method of {@link #post(Event, Action1)} that do nothing on unhandled.
     *
     * @see #post(Event, Action1)
     */
    public <E extends Event> void post(E event) {
        post(event, new Action1<E>() {
            @Override
            public void call(E e) {
            }
        });
    }

    /**
     * Subscribes handler to receive events type of specified class.
     * <p/>
     * You should call {@link Subscription#unsubscribe()} if you want to stop receiving events.
     *
     * @param <E>       Type of event.
     * @param clazz     Type of event that you want to receive.
     * @param handler   An event handler function that called if an event is posted.
     * @param scheduler handler will dispatched on this.
     */
    public <E extends Event> Subscription subscribe(Class<E> clazz, Action1<E> handler, Scheduler scheduler) {
        return subject
                .ofType(clazz)
                .doOnNext(new Action1<E>() {
                    @Override
                    public void call(E e) {
                        e.handledCount++;
                    }
                })
                .observeOn(scheduler)
                .subscribe(handler);
    }

    /**
     * An overload method of {@link #subscribe(Class, Action1, Scheduler)} that scheduler specified by {@link Schedulers#immediate()}
     *
     * @see #subscribe(Class, Action1, Scheduler)
     */
    public <E extends Event> Subscription subscribe(Class<E> clazz, Action1<E> handler) {
        return subscribe(clazz, handler, Schedulers.immediate());
    }
}

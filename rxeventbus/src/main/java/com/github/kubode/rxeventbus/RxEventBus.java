package com.github.kubode.rxeventbus;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple event bus using {@link PublishSubject}.
 * <p>
 * Notes:
 * <ul>
 * <li>MT-Safe.</li>
 * <li>Not supports generics.</li>
 * </ul>
 */
public class RxEventBus {

    private final Subject<Object, Object> subject = new SerializedSubject<>(PublishSubject.create());
    private final Map<Class, Integer> classRefCounts = new HashMap<>();

    /**
     * Post an event to subscribed handlers.
     * It can detect event is not handled.
     *
     * @param <E>       Type of {@code event}.
     * @param event     An event to post.
     * @param unhandled It will be called if {@code event} is not handled.
     *                  Note: If handler subscribed by using async {@link Scheduler}, it can't guarantee {@code event} is actually handled.
     */
    public <E> void post(@Nonnull E event, @Nullable Action1<E> unhandled) {
        if (getRefCount(event.getClass()) > 0) {
            subject.onNext(event);
        } else {
            if (unhandled != null) {
                unhandled.call(event);
            }
        }
    }

    /**
     * Post an event to subscribed handlers.
     * Do nothing on unhandled.
     *
     * @param <E>   Type of {@code event}.
     * @param event An event to post.
     * @see #post(Object, Action1)
     */
    public <E> void post(@Nonnull E event) {
        post(event, null);
    }

    /**
     * Subscribe {@code handler} to receive events type of specified class.
     * <p>
     * You should call {@link Subscription#unsubscribe()} if you want to stop receiving events.
     *
     * @param <E>       Type of {@code event}.
     * @param clazz     Type of event that you want to receive.
     * @param handler   It will be called when {@code clazz} and the same type of events were posted.
     * @param scheduler {@code handler} will dispatched to this scheduler.
     * @return A {@link Subscription} which can stop observing by calling {@link Subscription#unsubscribe()}.
     */
    public <E> Subscription subscribe(@Nonnull final Class<E> clazz, @Nonnull Action1<E> handler, @Nonnull Scheduler scheduler) {
        incrementRefCount(clazz);
        return subject
                .ofType(clazz)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        decrementRefCount(clazz);
                    }
                })
                .observeOn(scheduler)
                .subscribe(handler);
    }

    /**
     * Subscribe {@code handler} to receive events type of specified class.
     * <p>
     * Handler scheduled by {@link Schedulers#immediate()}
     *
     * @param <E>     Type of {@code event}.
     * @param clazz   Type of event that you want to receive.
     * @param handler It will be called when {@code clazz} and the same type of events were posted.
     * @return A {@link Subscription} which can stop observing by calling {@link Subscription#unsubscribe()}.
     * @see #subscribe(Class, Action1, Scheduler)
     */
    public <E> Subscription subscribe(@Nonnull Class<E> clazz, @Nonnull Action1<E> handler) {
        return subscribe(clazz, handler, Schedulers.immediate());
    }

    private synchronized int getRefCount(Class clazz) {
        if (classRefCounts.containsKey(clazz)) {
            return classRefCounts.get(clazz);
        } else {
            return 0;
        }
    }

    private synchronized void setRefCount(Class clazz, int refCount) {
        if (refCount == 0) {
            classRefCounts.remove(clazz);
        } else {
            classRefCounts.put(clazz, refCount);
        }
    }

    private synchronized void incrementRefCount(Class clazz) {
        setRefCount(clazz, getRefCount(clazz) + 1);
    }

    private synchronized void decrementRefCount(Class clazz) {
        setRefCount(clazz, getRefCount(clazz) - 1);
    }
}

package com.github.kubode.rxeventbus;

/**
 * Argument of {@link RxEventBus#post(Object)} must extend this class.
 *
 * @deprecated Since 1.1.0, {@link RxEventBus#post(Object)} can post any type. It will be removed since 1.2.0.
 */
@Deprecated
public abstract class Event {
}

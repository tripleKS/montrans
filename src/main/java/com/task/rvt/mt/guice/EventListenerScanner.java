package com.task.rvt.mt.guice;

import java.util.EventListener;

import javax.inject.Inject;

import com.google.inject.Injector;

public class EventListenerScanner extends Scanner<EventListener> {
    @Inject
    public EventListenerScanner(Injector injector) {
        super(injector, EventListener.class);
    }
}
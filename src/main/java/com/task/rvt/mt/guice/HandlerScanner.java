package com.task.rvt.mt.guice;

import javax.inject.Inject;

import org.eclipse.jetty.server.Handler;

import com.google.inject.Injector;

public class HandlerScanner extends Scanner<Handler> {
    @Inject
    public HandlerScanner(Injector injector) {
        super(injector, Handler.class);
    }
}

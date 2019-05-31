package com.task.rvt.mt.bootstrap;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import java.util.Map;

public class MoneyTransferServletModule extends ServletModule {

    private final String path;

    public MoneyTransferServletModule(String path) {
        this.path = path;
    }

    @Override
    protected void configureServlets() {
        bind(GuiceResteasyBootstrapServletContextListener.class);

        bind(HttpServletDispatcher.class).in(Singleton.class);

        if (path == null) {
            serve("/*").with(HttpServletDispatcher.class);
        } else {
            final Map<String, String> initParams = ImmutableMap.of("resteasy.servlet.mapping.prefix", path);
            serve(path + "/*").with(HttpServletDispatcher.class, initParams);
        }
    }
}
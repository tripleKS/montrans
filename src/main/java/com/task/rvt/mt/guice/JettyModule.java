package com.task.rvt.mt.guice;

import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;

public class JettyModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(GuiceFilter.class);
    }
}

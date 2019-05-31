package com.task.rvt.mt.bootstrap;

import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;
import com.task.rvt.mt.guice.EventListenerScanner;
import com.task.rvt.mt.guice.HandlerScanner;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

public class JettyStarter {
    private static final String APPLICATION_PATH = "/transfer";
    private static final String CONTEXT_ROOT = "/";

    private final GuiceFilter filter;
    private final EventListenerScanner eventListenerScanner;
    private final HandlerScanner handlerScanner;

    @Inject
    public JettyStarter(GuiceFilter filter, EventListenerScanner eventListenerScanner, HandlerScanner handlerScanner) {
        this.filter = filter;
        this.eventListenerScanner = eventListenerScanner;
        this.handlerScanner = handlerScanner;
    }

    public void runServer(int port) throws Exception {
        Server server = new Server(getThreadPool());

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        final ServletContextHandler context = new ServletContextHandler(server, CONTEXT_ROOT);

        FilterHolder filterHolder = new FilterHolder(filter);
        context.addFilter(filterHolder, APPLICATION_PATH + "/*", null);

        final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
        context.addServlet(defaultServlet, CONTEXT_ROOT);

        eventListenerScanner.accept(context::addEventListener);

        final HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(server.getHandler());
        handlerScanner.accept(handlers::addHandler);

        server.setHandler(handlers);
        server.start();
        server.join();
    }

    private ThreadPool getThreadPool() {
        int maxThreads = 100;
        int minThreads = 10;
        int idleTimeout = 120;
        return new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
    }
}

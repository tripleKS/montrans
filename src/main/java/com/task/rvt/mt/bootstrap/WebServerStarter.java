package com.task.rvt.mt.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.task.rvt.mt.guice.JettyModule;
import com.task.rvt.mt.guice.MoneyTransferModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebServerStarter {
    private static final Logger LOG = LogManager.getLogger(WebServerStarter.class);

    private static final String APPLICATION_PATH = "/transfer";

    private static final int MAX_PORT = 65535;

    public void startServer(int port) throws Exception {
        validatePort(port);

        LOG.info("Initializing web server");

        final Injector injector = Guice.createInjector(new JettyModule(), new MoneyTransferServletModule(APPLICATION_PATH), new MoneyTransferModule());

        injector.getInstance(JettyStarter.class).runServer(port);
    }

    private void validatePort(int port) {
        if (port < 0 || port > MAX_PORT) {
            throw new IllegalArgumentException("Illegal port number: " + port);
        }

        if (port == 0) {
            LOG.warn("Jetty server will be started on random port");
        }
    }
}

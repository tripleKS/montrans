package com.task.rvt.mt.bootstrap;

import org.junit.Test;

public class WebServerStarterTest {
    @Test(expected = IllegalArgumentException.class)
    public void startJettyOnNegativePort() throws Exception {
        WebServerStarter starter = new WebServerStarter();
        starter.startServer(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void startJettyOnTooBigPort() throws Exception {
        WebServerStarter starter = new WebServerStarter();
        starter.startServer(66666);
    }

}
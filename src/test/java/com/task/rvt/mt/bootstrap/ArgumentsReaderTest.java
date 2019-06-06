package com.task.rvt.mt.bootstrap;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentsReaderTest {
    @Test
    public void testPortDefined() {
        String[] args = new String[]{"-Dport=8080"};
        int expectedPort = 8080;
        assertThat(ArgumentsReader.getPortFromArgumentsOr0(args)).isEqualTo(expectedPort);
    }

    @Test
    public void testPortIsCaseSensitive() {
        String[] args = new String[]{"-DPort=8080"};
        int expectedPort = 0;
        assertThat(ArgumentsReader.getPortFromArgumentsOr0(args)).isEqualTo(expectedPort);
    }

    @Test
    public void testPortMissing(){
        String[] args = new String[]{""};
        assertThat(ArgumentsReader.getPortFromArgumentsOr0(args)).isZero();
    }

    @Test
    public void testPortMalformed(){
        String[] args = new String[]{"-DjettyPort=ph911"};
        assertThat(ArgumentsReader.getPortFromArgumentsOr0(args)).isZero();
    }
}
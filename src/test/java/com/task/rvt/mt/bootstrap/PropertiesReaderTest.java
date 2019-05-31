package com.task.rvt.mt.bootstrap;

import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesReaderTest {
    @Test
    public void testReadingExistingFile(){
        Properties props = PropertiesReader.readPropertiesFromFile("test.properties");
        assertThat(props).hasSize(1);
    }

    @Test
    public void testReadingMissingFile(){
        Properties props = PropertiesReader.readPropertiesFromFile("imagined.properties");
        assertThat(props).isEmpty();
    }
}
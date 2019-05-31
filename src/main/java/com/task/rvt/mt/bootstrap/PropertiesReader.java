package com.task.rvt.mt.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesReader {
    private static final Logger LOG = LogManager.getLogger(PropertiesReader.class);

    private PropertiesReader() {
    }

    public static Properties readPropertiesFromFile(String propsPath) {
        LOG.info("Reading key-value pairs from a properties file: [{}]", propsPath);

        Properties properties = new Properties();

        try (InputStream input =  PropertiesReader.class.getClassLoader().getResourceAsStream(propsPath)) {
            if (input != null) {
                properties.load(input);
            } else {
                LOG.warn("Cannot read properties file by the specified path. Empty map is returned. The specified path:[{}]", propsPath);
            }
        } catch (IOException e) {
            LOG.warn("Reading properties file failed. Empty map is returned.", e);
        }

        return properties;
    }
}

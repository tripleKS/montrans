package com.task.rvt.mt;

import com.task.rvt.mt.bootstrap.ArgumentsReader;
import com.task.rvt.mt.bootstrap.DbBootstrap;
import com.task.rvt.mt.bootstrap.WebServerStarter;
import com.task.rvt.mt.bootstrap.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;

public class TransferMoneyApplication {
    private static final Logger LOG = LogManager.getLogger(TransferMoneyApplication.class);

    public static void main(String[] args) throws Exception {
        LOG.info("Starting application with arguments:[{}]", Arrays.asList(args));

        String propertiesPath = ArgumentsReader.getDbPropsFileFromArgumentsOrDefault(args);
        Properties props = PropertiesReader.readPropertiesFromFile(propertiesPath);
        DbBootstrap dbInitializer = new DbBootstrap(props);
        dbInitializer.prepareDb();

        int port = ArgumentsReader.getPortFromArgumentsOr0(args);
        new WebServerStarter().startServer(port);
    }
}

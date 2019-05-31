package com.task.rvt.mt.bootstrap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class ArgumentsReader {
    private static final Logger LOG = LogManager.getLogger(ArgumentsReader.class);

    private static final String ARG_PORT = "jettyPort";
    private static final String ARG_NAME = "property=value";
    private static final String JAVA_PARAM_CONVENTION = "D";

    private static final int DEFAULT_JETTY_PORT = 0;
    private static final CommandLineParser parser = new DefaultParser();
    private static final Options options;

    static {
        Option option = Option.builder()
                .longOpt(JAVA_PARAM_CONVENTION)
                .argName(ARG_NAME)
                .hasArgs()
                .valueSeparator()
                .numberOfArgs(2)
                .build();

        options = new Options();
        options.addOption(option);
    }

    public static int getPortFromArgumentsOr0(String[] args) {
        int port = DEFAULT_JETTY_PORT;
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(JAVA_PARAM_CONVENTION)) {
                Properties properties = cmd.getOptionProperties(JAVA_PARAM_CONVENTION);
                port = Integer.valueOf(properties.getProperty(ARG_PORT));

                LOG.info("Jetty port is set to: {}", port);
            }
        } catch (ParseException | NumberFormatException e) {
            LOG.warn("Failure during parsing Jetty port from program arguments. Root cause: [{}]", e.toString());
        }

        return port;
    }

    public static String getDbPropsFileFromArgumentsOrDefault(String[] args) {
        return "database.properties";
    }
}

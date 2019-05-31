package com.task.rvt.mt.bootstrap;

import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.task.rvt.mt.db.DbConnectionProperties.PROP_DB_PASSWORD;
import static com.task.rvt.mt.db.DbConnectionProperties.PROP_DB_URL;
import static com.task.rvt.mt.db.DbConnectionProperties.PROP_DB_USER;

public class DbBootstrap {
    private static final Logger LOG = LogManager.getLogger(DbBootstrap.class);

    private static final String DB_PREPARE_SCHEMA = ";DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:db-scripts/create-schema.sql'\\;runscript from 'classpath:db-scripts/insert-data.sql'";

    private String url;

    public DbBootstrap(Properties dbProperties) {
        validateDbConnectionDetails(dbProperties);

        this.url = dbProperties.getProperty(PROP_DB_URL);
    }

    private void validateDbConnectionDetails(Properties dbProperties) {
        if (Strings.isNullOrEmpty(dbProperties.getProperty(PROP_DB_URL))) {
            LOG.error("Cannot initialize DB. Missing DB URL. The URL has to be initialized by the key: [{}]", PROP_DB_URL);
            throw new IllegalArgumentException("Failed to initialize DB. Missing '" + PROP_DB_URL + "'");
        }

        if (dbProperties.get(PROP_DB_USER) == null || dbProperties.get(PROP_DB_PASSWORD) == null) {
            LOG.error("Cannot initialize DB. Missing principal-credentials data. The Data has to be initialized by the keys: [{}, {}]", PROP_DB_USER, PROP_DB_PASSWORD);
            throw new IllegalArgumentException("Failed to initialize DB. Missing '" + PROP_DB_USER + "' and/or '" + PROP_DB_PASSWORD + "'");
        }

    }

    public void prepareDb() throws SQLException {
        Connection connection = DriverManager.getConnection(url + DB_PREPARE_SCHEMA, "", "");
        connection.close();
    }
}

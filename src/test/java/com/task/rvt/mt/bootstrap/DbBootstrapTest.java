package com.task.rvt.mt.bootstrap;

import org.junit.Test;

import java.sql.SQLException;
import java.util.Properties;

import static com.task.rvt.mt.db.DbConnectionProperties.PROP_DB_PASSWORD;
import static com.task.rvt.mt.db.DbConnectionProperties.PROP_DB_URL;
import static com.task.rvt.mt.db.DbConnectionProperties.PROP_DB_USER;

public class DbBootstrapTest {

    @Test(expected = IllegalArgumentException.class)
    public void testMissingDbUrl() throws SQLException {
        Properties dbProperties = new Properties();
        DbBootstrap preparer = new DbBootstrap(dbProperties);
        preparer.prepareDb();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDbUrl() throws SQLException {
        Properties dbProperties = new Properties();
        dbProperties.put(PROP_DB_URL, "");
        DbBootstrap preparer = new DbBootstrap(dbProperties);
        preparer.prepareDb();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingDbUser() throws SQLException {
        Properties dbProperties = new Properties();
        dbProperties.put(PROP_DB_URL, "foo");
        dbProperties.put(PROP_DB_PASSWORD, "");
        DbBootstrap preparer = new DbBootstrap(dbProperties);
        preparer.prepareDb();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingDbPassword() throws SQLException {
        Properties dbProperties = new Properties();
        dbProperties.put(PROP_DB_URL, "bar");
        dbProperties.put(PROP_DB_USER, "");
        DbBootstrap preparer = new DbBootstrap(dbProperties);
        preparer.prepareDb();
    }
}
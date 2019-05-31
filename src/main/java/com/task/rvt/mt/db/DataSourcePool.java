package com.task.rvt.mt.db;

import lombok.Getter;
import org.apache.commons.dbcp2.BasicDataSource;

public class DataSourcePool {
    private static final int CONNECTION_POOL_SIZE = 10;
    private static final String DRIVER_CLASS_NAME = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:montrans";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    @Getter
    private BasicDataSource dataSource = new BasicDataSource();

    public DataSourcePool() {
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setInitialSize(CONNECTION_POOL_SIZE);
    }
}
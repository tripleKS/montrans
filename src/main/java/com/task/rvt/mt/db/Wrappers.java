package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Account;

import java.sql.ResultSet;
import java.sql.SQLException;

final class Wrappers {
    static Account wrapAccount(ResultSet rs) throws SQLException {
        return new Account(rs.getString("account_number"),
                rs.getLong("customer_id"),
                rs.getBigDecimal("balance"),
                rs.getLong("version"));
    }

    static Customer wrapCustomer(ResultSet rs) throws SQLException {
        return new Customer(rs.getLong("id"),
                rs.getString("personal_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"));
    }

    private Wrappers() {
    }
}

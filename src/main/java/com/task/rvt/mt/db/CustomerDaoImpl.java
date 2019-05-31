package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Customer;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    private static final String ALL_CUSTOMERS = "select * from customer";
    private static final String CUSTOMER_BY_PERSONAL_ID = "select * from customer where personal_id = ?";
    private static final String CUSTOMER_BY_ID = "select * from customer where id = ?";

    @Inject
    private DataSourcePool dataSourcePool;

    @Override
    public List<Customer> listCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = dataSourcePool.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(ALL_CUSTOMERS);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                customers.add(Wrappers.wrapCustomer(rs));
            }
        }

        return customers;
    }

    @Override
    public Customer getCustomerByPersonalId(String personalId) throws SQLException {
        Customer customer = Customer.CUSTOMER_ZERO;

        try (Connection connection = dataSourcePool.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(CUSTOMER_BY_PERSONAL_ID);
            statement.setString(1, personalId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                customer = Wrappers.wrapCustomer(rs);
            }
        }

        return customer;
    }

    @Override
    public Customer getCustomerById(long customerId) throws SQLException {
        Customer customer = Customer.CUSTOMER_ZERO;

        try (Connection connection = dataSourcePool.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(CUSTOMER_BY_ID);
            statement.setLong(1, customerId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                customer = Wrappers.wrapCustomer(rs);
            }
        }

        return customer;
    }
}

package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Customer;

import java.sql.SQLException;
import java.util.List;

public interface CustomerDao {
    List<Customer> listCustomers() throws SQLException;

    Customer getCustomerByPersonalId(String personalId) throws SQLException;

    Customer getCustomerById(long customerId) throws SQLException;
}

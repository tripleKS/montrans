package com.task.rvt.mt.services;

import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Account;

import java.util.List;

public interface CustomerService {
    List<Customer> listCustomers() throws MTransferException;

    Customer getCustomerByPersonalId(String personalId) throws MTransferException;

    List<Account> listCustomerAccounts(long customerId) throws MTransferException;

    Customer getCustomerById(long customerId) throws MTransferException;
}

package com.task.rvt.mt.services;

import com.task.rvt.mt.db.AccountDao;
import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.db.CustomerDao;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Transaction;
import com.task.rvt.mt.util.MTransferException;

import javax.inject.Inject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.task.rvt.mt.util.Converters.getDateFromString;
import static com.task.rvt.mt.util.ErrorCodes.FORBIDDEN;
import static com.task.rvt.mt.util.ErrorCodes.INTERNAL_ERROR;
import static com.task.rvt.mt.util.ErrorCodes.NOT_FOUND;

public class CustomerServiceImpl implements CustomerService {
    @Inject
    private CustomerDao customerDao;
    @Inject
    private AccountDao accountDao;

    @Override
    public List<Customer> listCustomers() throws MTransferException {
        try {
            return customerDao.listCustomers();
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }

    @Override
    public Customer getCustomerByPersonalId(String personalId) throws MTransferException {
        try {
            Customer customer = customerDao.getCustomerByPersonalId(personalId);
            if (Customer.CUSTOMER_ZERO.equals(customer)) {
                throw new MTransferException("Customer with personalId [" + personalId + "] is not registered in the system.", NOT_FOUND);
            }

            return customer;
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }

    @Override
    public Customer getCustomerById(long customerId) throws MTransferException {
        try {
            Customer customer = customerDao.getCustomerById(customerId);
            if (Customer.CUSTOMER_ZERO.equals(customer)) {
                throw new MTransferException("Customer with id [" + customerId + "] was not found in the system.", NOT_FOUND);
            }

            return customer;
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }

    @Override
    public List<Account> listCustomerAccounts(long customerId) throws MTransferException {
        try {
            getCustomerById(customerId);

            return accountDao.listCustomerAccountsById(customerId);
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }
}

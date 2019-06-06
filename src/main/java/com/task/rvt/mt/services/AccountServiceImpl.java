package com.task.rvt.mt.services;

import com.task.rvt.mt.db.AccountDao;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Transfer;
import com.task.rvt.mt.util.MTransferException;

import javax.inject.Inject;
import java.sql.SQLException;

import static com.task.rvt.mt.model.Account.ACCOUNT_ZERO;
import static com.task.rvt.mt.util.ErrorCodes.INTERNAL_ERROR;
import static com.task.rvt.mt.util.ErrorCodes.NOT_FOUND;

public class AccountServiceImpl implements AccountService {
    @Inject
    private AccountDao accountDao;

    @Override
    public Account getAccountByNumber(String accountNumber) throws MTransferException {
        try {
            Account account = accountDao.getAccountByNumber(accountNumber);
            if(ACCOUNT_ZERO.equals(account)){
                throw new MTransferException("Account with number [" + accountNumber + "] was not found in the system.", NOT_FOUND);
            }

            return account;
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }

    @Override
    public Customer getAccountOwner(String accountNumber) throws MTransferException{
        try {
            Customer customer = accountDao.getAccountOwner(accountNumber);
            if (Customer.CUSTOMER_ZERO.equals(customer)) {
                throw new MTransferException("Customer to whom account ["+accountNumber+"] belong to was not found in the system. inconsistent data?", NOT_FOUND);
            }

            return customer;
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }

    @Override
    public void doTransfer(Transfer transfer) throws MTransferException {
        try {
            accountDao.transferMoney(transfer);
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }
}

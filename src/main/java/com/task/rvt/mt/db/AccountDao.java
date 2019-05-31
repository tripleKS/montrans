package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Transfer;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;

public interface AccountDao {
    List<Account> listAccounts() throws SQLException;

    List<Account> listCustomerAccountsById(long customerId) throws SQLException;

    Customer getAccountOwner(@NotNull String accountNumber) throws SQLException;

    Account getAccountByNumber(@NotNull String accountNumber) throws SQLException;

    void transferMoney(@NotNull Transfer transfer) throws SQLException;
}

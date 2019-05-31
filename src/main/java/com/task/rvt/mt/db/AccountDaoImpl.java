package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Transfer;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.task.rvt.mt.model.Account.ACCOUNT_ZERO;

public class AccountDaoImpl implements AccountDao {
    private static final String ALL_ACCOUNTS = "select * from account";
    private static final String CUSTOMER_ACCOUNTS = "select * from account where customer_id = ?";
    private static final String ACCOUNT_BY_NUMBER = "select * from account where account_number = ?";
    private static final String ACCOUNT_OWNER = "select c.* from customer c, account a where c.id = a.customer_id and a.account_number = ?";
    private static final String REDUCE_BALANCE = "update account SET balance = balance - ? WHERE account_number = ?";
    private static final String INCREASE_BALANCE = "update account SET balance = balance + ? WHERE account_number = ?";
    private static final String LOG_TRANSACTION = "insert into transaction_history (account_from, account_to, amount) VALUES(?, ?, ?);";

    @Inject
    private DataSourcePool dataSourcePool;

    @Override
    public List<Account> listAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = dataSourcePool.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(ALL_ACCOUNTS);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                accounts.add(Wrappers.wrapAccount(rs));
            }
        }

        return accounts;
    }

    @Override
    public List<Account> listCustomerAccountsById(long customerId) {
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = dataSourcePool.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(CUSTOMER_ACCOUNTS);
            statement.setLong(1, customerId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                accounts.add(Wrappers.wrapAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    @Override
    public Customer getAccountOwner(String accountNumber) throws SQLException {
        Customer customer = Customer.CUSTOMER_ZERO;

        try (Connection connection = dataSourcePool.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(ACCOUNT_OWNER);
            statement.setString(1, accountNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                customer = Wrappers.wrapCustomer(rs);
            }
        }

        return customer;
    }

    @Override
    public Account getAccountByNumber(String accountNumber) throws SQLException {
        Account account = ACCOUNT_ZERO;

        try (Connection connection = dataSourcePool.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(ACCOUNT_BY_NUMBER);
            statement.setString(1, accountNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                account = Wrappers.wrapAccount(rs);
            }
        }

        return account;
    }

    @Override
    public void transferMoney(@NotNull Transfer transfer) throws SQLException {
        try (Connection connection = dataSourcePool.getDataSource().getConnection();
             PreparedStatement reduceBalanceSql = connection.prepareStatement(REDUCE_BALANCE);
             PreparedStatement increaseBalanceSql = connection.prepareStatement(INCREASE_BALANCE);
             PreparedStatement logTransferSql = connection.prepareStatement(LOG_TRANSACTION)) {

            try {
                reduceBalanceSql.setBigDecimal(1, transfer.getAmount());
                reduceBalanceSql.setString(2, transfer.getAccountFrom());

                increaseBalanceSql.setBigDecimal(1, transfer.getAmount());
                increaseBalanceSql.setString(2, transfer.getAccountTo());

                logTransferSql.setString(1, transfer.getAccountFrom());
                logTransferSql.setString(2, transfer.getAccountTo());
                logTransferSql.setBigDecimal(3, transfer.getAmount());

                reduceBalanceSql.executeUpdate();
                increaseBalanceSql.executeUpdate();
                logTransferSql.execute();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
    }
}

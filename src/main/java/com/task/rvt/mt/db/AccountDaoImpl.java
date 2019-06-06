package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Transfer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOG = LogManager.getLogger(AccountDaoImpl.class);

    private static final String ALL_ACCOUNTS = "select * from account";
    private static final String CUSTOMER_ACCOUNTS = "select * from account where customer_id = ?";
    private static final String ACCOUNT_BY_NUMBER = "select * from account where account_number = ?";
    private static final String ACCOUNT_OWNER = "select c.* from customer c, account a where c.id = a.customer_id and a.account_number = ?";
    private static final String REDUCE_BALANCE = "update account SET balance = balance - ?, version = version + 1 WHERE account_number = ? AND version = ?";
    private static final String INCREASE_BALANCE = "update account SET balance = balance + ?, version = version + 1 WHERE account_number = ? AND version = ?";
    private static final String LOG_TRANSACTION = "insert into transactions_history (account_from, account_to, amount) VALUES(?, ?, ?);";

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

            Account accountFrom = getAccountByNumber(transfer.getAccountFrom());
            Account accountTo = getAccountByNumber(transfer.getAccountTo());
            try {
                reduceBalanceSql.setBigDecimal(1, transfer.getAmount());
                reduceBalanceSql.setString(2, transfer.getAccountFrom());
                reduceBalanceSql.setLong(3, accountFrom.getVersion());
                int reduceUpdated = reduceBalanceSql.executeUpdate();
                if (reduceUpdated == 0) {
                    throw new SQLException("'From' account has been updated in the interim. Source account:[{}]", transfer.getAccountFrom());
                }

                increaseBalanceSql.setBigDecimal(1, transfer.getAmount());
                increaseBalanceSql.setString(2, transfer.getAccountTo());
                increaseBalanceSql.setLong(3, accountTo.getVersion());
                int increaseUpdated = increaseBalanceSql.executeUpdate();
                if (increaseUpdated == 0) {
                    throw new SQLException("'To' account has been updated in the interim. Destination account:[{}]", transfer.getAccountTo());
                }

                logTransferSql.setString(1, transfer.getAccountFrom());
                logTransferSql.setString(2, transfer.getAccountTo());
                logTransferSql.setBigDecimal(3, transfer.getAmount());
                logTransferSql.execute();
                connection.commit();
            } catch (Exception e) {
                LOG.warn("Money transfer rolled back. Error occurred: ", e);
                connection.rollback();
                throw e;
            }
        }
    }
}

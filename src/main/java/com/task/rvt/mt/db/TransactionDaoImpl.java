package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Transaction;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {
    @Inject
    private DataSourcePool dataSourcePool;

    private static final String USER_ACCOUNT_TRANSACTIONS = "select * from transactions_history trax, account acc where"
            + " (trax.account_from = acc.account_number OR trax.account_to = acc.account_number)"
            + " AND trax.transaction_date > ? AND trax.transaction_date < ?"
            + " AND acc.account_number = ? AND acc.customer_id = ?";

    @Override
    public List<Transaction> listTransactions(long customerId, String accountNumber, long from, long to) throws SQLException {
        List<Transaction> traxes = new ArrayList<>();

        try (Connection connection = dataSourcePool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_ACCOUNT_TRANSACTIONS)) {
            statement.setTimestamp(1, new Timestamp(from));
            statement.setTimestamp(2, new Timestamp(to));
            statement.setString(3, accountNumber);
            statement.setLong(4, customerId);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                traxes.add(Wrappers.wrapTransaction(rs));
            }
        }

        return traxes;
    }
}

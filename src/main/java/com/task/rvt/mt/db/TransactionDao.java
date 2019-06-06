package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface TransactionDao {
    List<Transaction> listTransactions(long customerId, String accountNumber, long from, long to) throws SQLException;
}

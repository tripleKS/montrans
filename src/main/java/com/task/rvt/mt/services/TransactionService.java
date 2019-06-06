package com.task.rvt.mt.services;

import com.task.rvt.mt.model.Transaction;
import com.task.rvt.mt.util.MTransferException;

import java.util.List;

public interface TransactionService {
    List<Transaction> listTransactions(long customerId, String accountNumber, String fromDate, String toDate) throws MTransferException;
}

package com.task.rvt.mt.services;

import com.task.rvt.mt.model.Transaction;
import com.task.rvt.mt.model.Transfer;
import com.task.rvt.mt.util.MTransferException;

import java.util.List;

public interface TransferService {
    void transferMoneyBetweenAccounts(long customerId, Transfer transfer) throws MTransferException;
}

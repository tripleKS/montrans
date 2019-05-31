package com.task.rvt.mt.services;

import com.task.rvt.mt.model.Transfer;

public interface TransferService {
    void transferMoneyBetweenAccounts(long customerId, Transfer transfer) throws MTransferException;
}

package com.task.rvt.mt.services;

import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Transfer;
import com.task.rvt.mt.util.MTransferException;

public interface AccountService {
    Account getAccountByNumber(String accountNumber) throws MTransferException;

    Customer getAccountOwner(String accountNumber) throws MTransferException;

    void doTransfer(Transfer transfer) throws MTransferException;
}

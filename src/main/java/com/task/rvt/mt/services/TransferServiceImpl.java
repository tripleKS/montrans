package com.task.rvt.mt.services;

import com.google.inject.Inject;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Transfer;

import static com.task.rvt.mt.services.ErrorCodes.FORBIDDEN;

public class TransferServiceImpl implements TransferService {
    @Inject
    private AccountService accountService;
    @Inject
    private CustomerService customerService;


    @Override
    public void transferMoneyBetweenAccounts(long customerId, Transfer transfer) throws MTransferException {
        validateTransfer(customerId, transfer);

        accountService.doTransfer(transfer);
    }

    private void validateTransfer(long customerId, Transfer transfer)  throws MTransferException {
        customerService.getCustomerById(customerId);

        Customer accountOwner = accountService.getAccountOwner(transfer.getAccountFrom());
        if(accountOwner.getId() != customerId) {
            throw new MTransferException("Customer is allowed to do transfers only from own accounts. " +
                    "Customer with id [" + customerId + "] does not have account with number[" + transfer.getAccountFrom() + "].", FORBIDDEN);
        }

        Account sourceAccount = accountService.getAccountByNumber(transfer.getAccountFrom());
        if(sourceAccount.getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new MTransferException("Not enough money in the account[" + transfer.getAccountFrom() + "].", FORBIDDEN);
        }

        accountService.getAccountByNumber(transfer.getAccountTo());
    }
}

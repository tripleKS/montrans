package com.task.rvt.mt.services;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.task.rvt.mt.db.TransactionDao;
import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Transaction;
import com.task.rvt.mt.util.MTransferException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.task.rvt.mt.util.Converters.getDateFromString;
import static com.task.rvt.mt.util.ErrorCodes.BAD_REQUEST;
import static com.task.rvt.mt.util.ErrorCodes.FORBIDDEN;
import static com.task.rvt.mt.util.ErrorCodes.INTERNAL_ERROR;

public class TransactionServiceImpl implements TransactionService {
    @Inject
    private AccountService accountService;
    @Inject
    private CustomerService customerService;
    @Inject
    private TransactionDao transactionDao;

    @Override
    public List<Transaction> listTransactions(long customerId, String accountNumber, String fromDate, String toDate) throws MTransferException {
        validateTransactionsFilter(customerId, accountNumber);

        validateTimeIntervalStrings(fromDate, toDate);

        LocalDate from = getDateFromString(fromDate);
        LocalDate to = getDateFromString(toDate);

        try {
            Date date1 = Date.from(from.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date date2 = Date.from(to.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
            return transactionDao.listTransactions(customerId, accountNumber, date1.getTime(), date2.getTime());
        } catch (SQLException e) {
            throw new MTransferException("Internal error", e, INTERNAL_ERROR);
        }
    }

    private void validateTransactionsFilter(long customerId, String accountNumber) throws MTransferException {
        customerService.getCustomerById(customerId);

        Customer accountOwner = accountService.getAccountOwner(accountNumber);
        if (accountOwner.getId() != customerId) {
            throw new MTransferException("Customer is allowed to do transfers only from own accounts. " +
                    "Customer with id [" + customerId + "] does not have account with number[" + accountNumber + "].", FORBIDDEN);
        }
    }

    private void validateTimeIntervalStrings(String fromDate, String toDate) throws MTransferException {
        if (Strings.isNullOrEmpty(fromDate)) {
            throw new MTransferException("Mandatory 'from' request parameter is not defined. Expected date format is 'yyyyMMdd'.", BAD_REQUEST);
        }
        if (Strings.isNullOrEmpty(toDate)){
            throw new MTransferException("Mandatory 'to' request parameter is not defined. Expected date format is 'yyyyMMdd'.", BAD_REQUEST);
        }
    }
}

package com.task.rvt.mt.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Transaction {
    private final long id;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final Date transactionDate;
}

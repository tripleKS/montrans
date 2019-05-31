package com.task.rvt.mt.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    public static final Account ACCOUNT_ZERO = new Account("", 0, BigDecimal.valueOf(0));
    private final String accountNumber;
    private final long customerId;
    private final BigDecimal balance;
}

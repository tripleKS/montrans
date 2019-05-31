package com.task.rvt.mt.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transfer {
    private String accountFrom;
    private String accountTo;
    private BigDecimal amount;
}

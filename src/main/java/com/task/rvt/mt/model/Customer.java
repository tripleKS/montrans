package com.task.rvt.mt.model;

import lombok.Data;

@Data
public class Customer {
    public static final Customer CUSTOMER_ZERO = new Customer(0,"","","","");

    private final long id;
    private final String personalId;
    private final String name;
    private final String phone;
    private final String email;
}

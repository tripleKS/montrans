package com.task.rvt.mt.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.task.rvt.mt.db.AccountDao;
import com.task.rvt.mt.db.AccountDaoImpl;
import com.task.rvt.mt.db.CustomerDao;
import com.task.rvt.mt.db.CustomerDaoImpl;
import com.task.rvt.mt.rests.CustomerRest;
import com.task.rvt.mt.services.AccountService;
import com.task.rvt.mt.services.AccountServiceImpl;
import com.task.rvt.mt.services.CustomerService;
import com.task.rvt.mt.services.CustomerServiceImpl;
import com.task.rvt.mt.services.TransferService;
import com.task.rvt.mt.services.TransferServiceImpl;

public class MoneyTransferModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(CustomerRest.class);
        binder.bind(CustomerService.class).to(CustomerServiceImpl.class);
        binder.bind(CustomerDao.class).to(CustomerDaoImpl.class);
        binder.bind(AccountService.class).to(AccountServiceImpl.class);
        binder.bind(AccountDao.class).to(AccountDaoImpl.class);
        binder.bind(TransferService.class).to(TransferServiceImpl.class);
    }
}

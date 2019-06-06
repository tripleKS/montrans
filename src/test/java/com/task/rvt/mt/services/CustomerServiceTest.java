package com.task.rvt.mt.services;

import com.task.rvt.mt.db.AccountDao;
import com.task.rvt.mt.db.CustomerDao;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.util.MTransferException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static com.task.rvt.mt.model.Customer.CUSTOMER_ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;
    @Mock
    private AccountDao accountDao;
    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    public void testGetAccountByNumberEmptyResult() throws Exception {
        when(customerDao.listCustomers()).thenReturn(new ArrayList<>());

        assertThat(customerService.listCustomers()).isEmpty();
    }

    @Test
    public void testGetAccountByNumberCatchesSQLException() throws Exception {
        when(customerDao.listCustomers()).thenThrow(new SQLException());

        assertThatThrownBy(() -> customerService.listCustomers())
                .isInstanceOf(MTransferException.class)
                .hasMessage("Internal error");
    }

    @Test
    public void testGetCustomerByPersonalIdCatchesSQLException() throws Exception {
        when(customerDao.getCustomerByPersonalId(any())).thenThrow(SQLException.class);

        assertThatThrownBy(() -> customerService.getCustomerByPersonalId("foo"))
                .isInstanceOf(MTransferException.class)
                .hasMessage("Internal error");
    }

    @Test
    public void testGetCustomerByPersonalIdMissing() throws Exception {
        when(customerDao.getCustomerByPersonalId(any())).thenReturn(CUSTOMER_ZERO);

        assertThatThrownBy(() -> customerService.getCustomerByPersonalId("PF0009-12"))
                .isInstanceOf(MTransferException.class)
                .hasMessage("Customer with personalId [PF0009-12] is not registered in the system.");
    }

    @Test
    public void testGetCustomerByPersonalIdExisting() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.getName()).thenReturn("Fox");
        when(customerDao.getCustomerByPersonalId(any())).thenReturn(customer);

        assertThat(customerService.getCustomerByPersonalId("PF0009-12").getName()).isEqualTo("Fox");
    }

    @Test
    public void testGetCustomerByIdCatchesSQLException() throws Exception {
        when(customerDao.getCustomerById(anyLong())).thenThrow(SQLException.class);

        assertThatThrownBy(() -> customerService.getCustomerById(12L))
                .isInstanceOf(MTransferException.class)
                .hasMessage("Internal error");
    }

    @Test
    public void testGetCustomerByIdMissing() throws Exception {
        when(customerDao.getCustomerById(anyLong())).thenReturn(CUSTOMER_ZERO);

        assertThatThrownBy(() -> customerService.getCustomerById(12L))
                .isInstanceOf(MTransferException.class)
                .hasMessage("Customer with id [12] was not found in the system.");
    }

    @Test
    public void testGetCustomerByIdExisting() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(12L);
        when(customer.getName()).thenReturn("Fox");
        when(customerDao.getCustomerById(anyLong())).thenReturn(customer);

        assertThat(customerService.getCustomerById(12).getName()).isEqualTo("Fox");
        assertThat(customerService.getCustomerById(12).getId()).isEqualTo((12L));
    }

    @Test
    public void testListCustomerAccountsCatchsSQLException() throws Exception {
        when(customerDao.getCustomerById(anyLong())).thenThrow(SQLException.class);

        assertThatThrownBy(() -> customerService.listCustomerAccounts(12L))
                .isInstanceOf(MTransferException.class)
                .hasMessage("Internal error");
    }


    @Test
    public void testListCustomerAccountsMissingCustomer() throws Exception {
        when(customerDao.getCustomerById(anyLong())).thenReturn(CUSTOMER_ZERO);

        assertThatThrownBy(() -> customerService.listCustomerAccounts(12L))
                .isInstanceOf(MTransferException.class)
                .hasMessage("Customer with id [12] was not found in the system.");
    }

    @Test
    public void testListCustomerAccountsExistingCustomerMissingAccounts() throws Exception {
        Customer customer = mock(Customer.class);
        when(customerDao.getCustomerById(anyLong())).thenReturn(customer);
        when(accountDao.listCustomerAccountsById(anyLong())).thenReturn(new ArrayList<>());

        assertThat(customerService.listCustomerAccounts(12L)).isEmpty();
    }

    @Test
    public void testListCustomerAccountsExistingCustomerExistingAccounts() throws Exception {
        Customer customer = mock(Customer.class);
        when(customerDao.getCustomerById(anyLong())).thenReturn(customer);
        Account account = mock(Account.class);
        when(accountDao.listCustomerAccountsById(anyLong())).thenReturn(Collections.singletonList(account));

        assertThat(customerService.listCustomerAccounts(12L)).hasSize(1);
    }
}
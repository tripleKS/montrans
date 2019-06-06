package com.task.rvt.mt.services;

import com.task.rvt.mt.db.AccountDao;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.util.MTransferException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.SQLException;

import static com.task.rvt.mt.model.Account.ACCOUNT_ZERO;
import static com.task.rvt.mt.model.Customer.CUSTOMER_ZERO;
import static com.task.rvt.mt.util.ErrorCodes.INTERNAL_ERROR;
import static com.task.rvt.mt.util.ErrorCodes.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    private AccountDao accountDao;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void testGetMissingAccountByNumber() throws Exception {
        when(accountDao.getAccountByNumber(anyString())).thenReturn(ACCOUNT_ZERO);

        Throwable mte = catchThrowable(() -> accountService.getAccountByNumber("foo") );
        assertThat(mte)
                .isInstanceOf(MTransferException.class)
                .hasMessage("Account with number [foo] was not found in the system.");
        assertThat(((MTransferException)mte).getErrorCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testGetAccountByNumberSqlException() throws Exception {
        when(accountDao.getAccountByNumber(anyString())).thenThrow(new SQLException("Some reason"));

        Throwable mte = catchThrowable(() -> accountService.getAccountByNumber("foo") );
        assertThat(mte)
                .isInstanceOf(MTransferException.class)
                .hasMessage("Internal error");
        assertThat(((MTransferException)mte).getErrorCode()).isEqualTo(INTERNAL_ERROR);
    }

    @Test
    public void testGetAccountByNumber() throws Exception {
        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(new BigDecimal("0.9"));
        when(accountDao.getAccountByNumber(anyString())).thenReturn(account);

        Account calculated = accountService.getAccountByNumber("foo");
        assertThat(calculated.getBalance()).isEqualTo(new BigDecimal("0.9"));
    }

    @Test
    public void testAccountDoesnotBelongToAnyCustomerException() throws Exception {
        when(accountDao.getAccountOwner(anyString())).thenReturn(CUSTOMER_ZERO);

        Throwable mte = catchThrowable(() -> accountService.getAccountOwner("account-number") );
        assertThat(mte)
                .isInstanceOf(MTransferException.class)
                .hasMessage("Customer to whom account [account-number] belong to was not found in the system. inconsistent data?");
        assertThat(((MTransferException)mte).getErrorCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testGetAccountOwnerSqlException() throws Exception {
        when(accountDao.getAccountOwner(anyString())).thenThrow(new SQLException());

        Throwable mte = catchThrowable(() -> accountService.getAccountOwner("account-number") );
        assertThat(mte)
                .isInstanceOf(MTransferException.class)
                .hasMessage("Internal error");
        assertThat(((MTransferException)mte).getErrorCode()).isEqualTo(INTERNAL_ERROR);
    }

    @Test
    public void testGetAccount() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(1001L);
        when(accountDao.getAccountOwner(anyString())).thenReturn(customer);

        Customer calculated = accountService.getAccountOwner("account-number");
        assertThat(calculated.getId()).isEqualTo(1001L);
    }
}
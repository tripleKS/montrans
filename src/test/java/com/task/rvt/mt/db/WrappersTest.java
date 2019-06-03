package com.task.rvt.mt.db;

import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.model.Customer;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WrappersTest {
    @Test
    public void testWrappingAccount() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("account_number")).thenReturn("000");
        when(rs.getLong("customer_id")).thenReturn(1L);
        when(rs.getBigDecimal("balance")).thenReturn(BigDecimal.valueOf(-1009));
        when(rs.getLong("version")).thenReturn(10L);
        Account calculated = Wrappers.wrapAccount(rs);
        assertThat(calculated.getAccountNumber()).isEqualTo("000");
        assertThat(calculated.getCustomerId()).isEqualTo(1L);
        assertThat(calculated.getBalance()).isNegative().isEqualTo(BigDecimal.valueOf(-1009));
        assertThat(calculated.getVersion()).isEqualTo(10L);
    }

    @Test
    public void testWrappingAccountThrowsException() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("account_number")).thenThrow(new SQLException("Some error"));
        assertThatThrownBy(() -> Wrappers.wrapAccount(rs))
                .isInstanceOf(SQLException.class)
                .hasMessage("Some error");
    }

    @Test
    public void testWrappingCustomer() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("personal_id")).thenReturn("abrakadabra");
        when(rs.getString("name")).thenReturn("Mr. Hare");
        when(rs.getString("phone")).thenReturn("+000 987 098");
        when(rs.getString("email")).thenReturn("some@example.com");
        Customer calculated = Wrappers.wrapCustomer(rs);

        assertThat(calculated.getId()).isEqualTo(1L);
        assertThat(calculated.getPersonalId()).isEqualTo("abrakadabra");
        assertThat(calculated.getName()).containsIgnoringCase("hare");
        assertThat(calculated.getPhone()).containsIgnoringCase("987");
        assertThat(calculated.getEmail()).contains("@");
    }

    @Test
    public void testWrappingCustomerThrowsException() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenThrow(new SQLException("Error"));
        assertThatThrownBy(() -> Wrappers.wrapCustomer(rs))
                .isInstanceOf(SQLException.class)
                .hasMessage("Error");
    }
}
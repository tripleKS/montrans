package com.task.rvt.mt.rests;

import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Transfer;
import com.task.rvt.mt.services.CustomerService;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.services.MTransferException;
import com.task.rvt.mt.services.TransferService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.task.rvt.mt.services.ErrorCodes.FORBIDDEN;
import static com.task.rvt.mt.services.ErrorCodes.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerRestTest {
    @Mock
    private CustomerService customerService;
    @Mock
    private TransferService transferService;
    @InjectMocks
    private CustomerRest customerRest;

    @Test
    public void testEmptyCustomersList() throws Exception {
        when(customerService.listCustomers()).thenReturn(new ArrayList<>());
        Response response = customerRest.listCustomers();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(List.class);
        assertThat((List) response.getEntity()).isEmpty();
    }

    @Test
    public void testCustomersListNotEmpty() throws Exception {
        List<Customer> customers = new ArrayList<>();
        Customer customer = mock(Customer.class);
        customers.add(customer);
        when(customerService.listCustomers()).thenReturn(customers);
        Response response = customerRest.listCustomers();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(List.class);
        assertThat((List) response.getEntity()).hasSize(1);
    }

    @Test
    public void testMissingCustomerByPersonalId() throws Exception {
        when(customerService.getCustomerByPersonalId(anyString())).thenThrow(new MTransferException("Customer with personalId [09098] is not registered in the system.", NOT_FOUND));

        Response response = customerRest.getCustomerByPersonalId("09098");
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThat(response.getEntity().toString()).isEqualTo("Customer with personalId [09098] is not registered in the system.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFoundCustomerByPersonalId() throws Exception {
        Customer mockedCustomer = mock(Customer.class);
        when(mockedCustomer.getId()).thenReturn(1L);
        when(mockedCustomer.getPersonalId()).thenReturn("PASSPORT001");
        when(customerService.getCustomerByPersonalId(anyString())).thenReturn(mockedCustomer);

        Response response = customerRest.getCustomerByPersonalId(anyString());

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(Map.class);
        Map<String, Object> enhancedCustomerInfo = (Map) response.getEntity();

        assertThat(enhancedCustomerInfo.get(CustomerRest.JSON_DATA_KEY)).isInstanceOf(Customer.class);
        Customer calculatedCustomer = (Customer) enhancedCustomerInfo.get(CustomerRest.JSON_DATA_KEY);
        assertThat(calculatedCustomer.getPersonalId()).isEqualTo("PASSPORT001");

        assertThat(enhancedCustomerInfo.get(CustomerRest.JSON_LINKS_KEY)).isInstanceOf(Map.class);
        Map<String, String> calculatedLinks = (Map) enhancedCustomerInfo.get(CustomerRest.JSON_LINKS_KEY);
        assertThat(calculatedLinks.get(CustomerRest.JSON_ACCOUNTS_KEY)).isEqualTo("GET:/transfer/customers/1/accounts");
    }

    @Test
    public void testGetAccountsOfMissingCustomer() throws Exception {
        when(customerService.listCustomerAccounts(anyLong())).thenThrow(new MTransferException("Customer with id [1] was not found in the system.", NOT_FOUND));
        Response response = customerRest.listCustomerAccounts(1L);

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThat(response.getEntity().toString()).isEqualTo("Customer with id [1] was not found in the system.");
    }

    @Test
    public void testGetExistingCustomerMissingAccounts() throws Exception {
        when(customerService.listCustomerAccounts(anyLong())).thenReturn(new ArrayList<>());
        Response response = customerRest.listCustomerAccounts(anyLong());

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(List.class);
        assertThat((List) response.getEntity()).isEmpty();
    }

    @Test
    public void testGetExistingCustomerExistingAccounts() throws Exception {
        List<Account> accounts = new ArrayList<>();
        Account account = mock(Account.class);
        accounts.add(account);
        when(customerService.listCustomerAccounts(anyLong())).thenReturn(accounts);

        Response response = customerRest.listCustomerAccounts(anyLong());
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(List.class);
        assertThat((List) response.getEntity()).hasSize(1);
    }

    @Test
    public void testDoTransferForMissingCustomer() throws Exception {
        doThrow(new MTransferException("Customer with id [1] was not found in the system.", NOT_FOUND)).when(transferService).transferMoneyBetweenAccounts(anyLong(), any());
        Transfer transfer = mock(Transfer.class);
        Response response = customerRest.transferMoney(1L, transfer);

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThat(response.getEntity().toString()).isEqualTo("Customer with id [1] was not found in the system.");
    }

    @Test
    public void testDoTransferFromStrangerAccount() throws Exception {
        doThrow(new MTransferException("Customer is allowed to do transfers only from own accounts. " +
                "Customer with id [1] does not have account with number[1].", FORBIDDEN))
                .when(transferService).transferMoneyBetweenAccounts(anyLong(), any());
        Transfer transfer = mock(Transfer.class);
        Response response = customerRest.transferMoney(1L, transfer);

        assertThat(response.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
        assertThat(response.getEntity().toString()).contains("Customer with id [1] does not have account with number[1]");
    }

    @Test
    public void testDoTransferLowBalance() throws Exception {
        doThrow(new MTransferException("Not enough money in the account[1].", FORBIDDEN))
                .when(transferService).transferMoneyBetweenAccounts(anyLong(), any());
        Transfer transfer = mock(Transfer.class);
        Response response = customerRest.transferMoney(1L, transfer);

        assertThat(response.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
        assertThat(response.getEntity().toString()).contains("Not enough money in the account[1].");
    }

    @Test
    public void testDoTransferMissingDestinationAccount() throws Exception {
        doThrow(new MTransferException("Account with number [1] was not found in the system.", FORBIDDEN))
                .when(transferService).transferMoneyBetweenAccounts(anyLong(), any());

        Transfer transfer = mock(Transfer.class);
        Response response = customerRest.transferMoney(1L, transfer);

        assertThat(response.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
        assertThat(response.getEntity().toString()).contains("Account with number [1] was not found in the system.");
    }
}
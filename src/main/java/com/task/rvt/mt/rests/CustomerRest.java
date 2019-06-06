package com.task.rvt.mt.rests;

import com.task.rvt.mt.model.Customer;
import com.task.rvt.mt.model.Transaction;
import com.task.rvt.mt.model.Transfer;
import com.task.rvt.mt.model.Account;
import com.task.rvt.mt.services.CustomerService;
import com.task.rvt.mt.services.TransactionService;
import com.task.rvt.mt.util.MTransferException;
import com.task.rvt.mt.services.TransferService;
import com.task.rvt.mt.util.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/customers")
public class CustomerRest {
    private static final Logger LOG = LogManager.getLogger(CustomerRest.class);

    private static final int LABEL_LENGTH = 4;

    static final String JSON_DATA_KEY = "customer";
    static final String JSON_LINKS_KEY = "links";
    static final String JSON_ACCOUNTS_KEY = "accounts";
    static final String JSON_DO_TRANSFER_KEY = "transferMoneyBetweenAccounts";

    @Inject
    private CustomerService customerService;
    @Inject
    private TransferService transferService;
    @Inject
    private TransactionService transactionService;

    @GET
    @Produces("application/json")
    public Response listCustomers() {
        String label = RandomStringGenerator.generateString(LABEL_LENGTH);
        LOG.debug("Getting list of all customers. LBL: {}", label);
        long start = System.currentTimeMillis();

        Response response;
        try {
            List<Customer> customers = customerService.listCustomers();
            response = Response.ok(customers).build();
        } catch (MTransferException e) {
            LOG.warn("Getting list of customers failed.", e);
            response = Response.status(Response.Status.fromStatusCode(e.getErrorCode())).entity(e.getMessage()).build();
        }

        long end = System.currentTimeMillis();
        LOG.debug("Getting all customers took ms, LBL: [{}] , {}", (end - start), label);

        return response;
    }

    @GET
    @Path("/{personalId}")
    @Produces("application/json")
    public Response getCustomerByPersonalId(@PathParam("personalId") String personalId) {
        String label = RandomStringGenerator.generateString(LABEL_LENGTH);
        LOG.debug("Getting customer by personal Id. LBL: {}", label);
        long start = System.currentTimeMillis();

        Response response;
        try {
            Customer customer = customerService.getCustomerByPersonalId(personalId);
            Map<String, Object> enhancedCustomerInfo = updateCustomerInfoWithUriLinks(customer);
            response = Response.ok(enhancedCustomerInfo).build();
        } catch (MTransferException e) {
            LOG.warn("Getting customers by id failed. LBL: {}", e);
            response = Response.status(Response.Status.fromStatusCode(e.getErrorCode())).entity(e.getMessage()).build();
        }

        long end = System.currentTimeMillis();
        LOG.debug("Fetching customer info by personal id took ms, LBL: [{}], {}", (end - start), label);

        return response;
    }

    private Map<String, Object> updateCustomerInfoWithUriLinks(Customer customer) {
        Map<String, Object> enhancedCustomerInfo = new HashMap<>();
        enhancedCustomerInfo.put(JSON_DATA_KEY, customer);
        enhancedCustomerInfo.put(JSON_LINKS_KEY, getCustomerLinks(customer));

        return enhancedCustomerInfo;
    }

    private Map<String, String> getCustomerLinks(Customer customer) {
        Map<String, String> customerLinks = new HashMap<>();
        // init uri dynamically
        customerLinks.put(JSON_ACCOUNTS_KEY, "GET:/transfer/customers/" + customer.getId() + "/accounts");
        customerLinks.put(JSON_DO_TRANSFER_KEY, "POST:/transfer/customers/" + customer.getId() + "/transfer");

        return customerLinks;
    }

    @GET
    @Path("/{customerId}/accounts")
    @Produces("application/json")
    public Response listCustomerAccounts(@PathParam("customerId") long customerId) {
        String label = RandomStringGenerator.generateString(LABEL_LENGTH);
        LOG.debug("Getting all customer's accounts. LBL: [{}]", label);
        long start = System.currentTimeMillis();

        Response response;
        try {
            List<Account> accounts = customerService.listCustomerAccounts(customerId);
            response = Response.ok(accounts).build();
        } catch (MTransferException e) {
            LOG.warn("Getting customer's accounts failed.", e);
            response = Response.status(Response.Status.fromStatusCode(e.getErrorCode())).entity(e.getMessage()).build();
        }

        long end = System.currentTimeMillis();
        LOG.debug("Getting all customer's accounts took ms, LBL: [{}], {}", (end - start), label);

        return response;
    }

    @POST
    @Path("/{customerId}/transfer")
    @Consumes("application/json")
    public Response transferMoney(@PathParam("customerId") long customerId, Transfer transfer) {
        String label = RandomStringGenerator.generateString(LABEL_LENGTH);
        LOG.debug("Transfer money from account[" + transfer.getAccountFrom() + "] to account[" + transfer.getAccountTo() + "]. LBL: {}", label);
        long start = System.currentTimeMillis();

        Response response;
        try {
            transferService.transferMoneyBetweenAccounts(customerId, transfer);
            response = Response.ok("Transfer completed").build();
        } catch (MTransferException e) {
            LOG.warn("Transfer did not happen. LBL: {}", label, e);
            response = Response.status(Response.Status.fromStatusCode(e.getErrorCode())).entity(e.getMessage()).build();
        }

        long end = System.currentTimeMillis();
        LOG.debug("Transferring took ms, LBL: [{}], {}", (end - start), label);

        return response;
    }

    @GET
    @Path("/{customerId}/accounts/{accountNumber}/transactions")
    @Produces("application/json")
    public Response listTransactions(@PathParam("customerId") long customerId, @PathParam("accountNumber") String accountNumber,
                                     @QueryParam("from") String fromDate, @QueryParam("to") String toDate) {
        String label = RandomStringGenerator.generateString(LABEL_LENGTH);
        LOG.debug("Getting transactional history of customer account. [Customer, Account, from-date, to-date], LBL: [{},{},{},{}], {}", customerId, accountNumber, fromDate, toDate, label);
        long start = System.currentTimeMillis();

        Response response;
        try {
            List<Transaction> traxes = transactionService.listTransactions(customerId, accountNumber, fromDate, toDate);
            response = Response.ok(traxes).build();
        } catch (MTransferException e) {
            LOG.warn("Retrieving transactions failed. LBL: {}", label, e);
            response = Response.status(Response.Status.fromStatusCode(e.getErrorCode())).entity(e.getMessage()).build();
        }

        long end = System.currentTimeMillis();
        LOG.debug("Retrieving transactional history  took ms, LBL: [{}], {}", (end - start), label);

        return response;
    }
}

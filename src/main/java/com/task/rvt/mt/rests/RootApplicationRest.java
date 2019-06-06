package com.task.rvt.mt.rests;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/links")
public class RootApplicationRest {

    @GET
    @Produces("application/json")
    public Response listEndpoints() {
        Map<String, String> uris = listAvailableEndpoints();
        return Response.ok(uris).build();
    }

    private Map<String, String> listAvailableEndpoints(){
        Map<String, String> uris = new HashMap<>();

        uris.put("List customers(GET request): ", "/transfer/customers");
        uris.put("Get customer by personal Id(f.e. passport) (GET request): ", "/transfer/customers/{personalId}");
        uris.put("List customer accounts by 'internal customer id' (GET request): ", "/transfer/customers/{customerId}/accounts");
        uris.put("Do transfer from customer account(POST request). Request Body is json in form '{\"accountFrom\": \"...\",\"accountTo\": \"...\",\"amount\": ...}': ", "/transfer/customers/{customerId}/transfer");
        uris.put("Get transaction history", "/transfer/customers/{customerId}/accounts/{accountNumber}/transactions?from=yyyyDDmm&to=yyyyDDmm");

        return uris;
    }

}

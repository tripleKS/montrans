
Building the example project:
-----------------------------

To build the fat JAR:

    gradle clean build fatJar

To run:

    java -jar build/libs/montrans-all-1.0-SNAPSHOT.jar -Dport=8888
    

To execute requests:

    ..../transfer/links - get available endpoints 
    ..../transfer/customers - list of customers
    ..../transfer/customers/{personalId} - get customer by personnal id
    ..../transfer/customers/{customerId}/accounts - get customer accounts by customer id
    ..../transfer/customers/{customerId}/accounts/{accountNumber}/transactions?from=yyyyDDmm&to=yyyyDDmm
    ....//transfer/customers/{customerId}/transfer - 'do' transfer between accounts while body is in form:
```json
    {
        "accountFrom": "...",
        "accountTo": "...",
        "amount": ...
    }
```
    
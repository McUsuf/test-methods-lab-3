package org.nsu.fit.tests.api.auth;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class AuthCustomerTest {
    private RestClient restClient;

    private AccountTokenPojo adminToken;
    private AccountTokenPojo customerToken;
    private CustomerPojo customerPojo;

    @BeforeClass
    public void beforeClass() {
        restClient = new RestClient();
    }

    @Test(description = "Authenticate as admin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication as customer feature")
    public void authAsAdminTest() {
        adminToken = restClient.authenticate("admin", "setup");
        assertNotNull(adminToken);
    }

    @Test(description = "Create new customer as admin then authenticate as customer", dependsOnMethods = "authAsAdminTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication as customer feature")
    public void authAsCustomerTest() {
        customerPojo = restClient.createAutoGeneratedCustomer(adminToken);
        customerToken = restClient.authenticate(customerPojo.login, customerPojo.pass);
        assertNotNull(customerToken);
    }

    @Test(description = "Me as Customer", dependsOnMethods = "authAsCustomerTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication as customer feature")
    public void getMeAsCustomer() {
        CustomerPojo result = restClient.meAsCustomer(customerToken);
        assertEquals(customerPojo.firstName, result.firstName);
        assertEquals(customerPojo.lastName, result.lastName);
        assertEquals(customerPojo.login, result.login);
        assertEquals(customerPojo.pass, result.pass);
        assertEquals(customerPojo.balance, result.balance);
    }

    @AfterClass
    public void afterClass() {
        restClient.deleteCustomer(customerPojo, adminToken);
    }
}

package org.nsu.fit.tests.api.customer;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class GetCustomerTest {
    private RestClient restClient;

    private AccountTokenPojo adminToken;
    private CustomerPojo customerPojo;

    @BeforeClass
    public void beforeClass() {
        restClient = new RestClient();
    }

    @Test(description = "Authenticate as admin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Get customer feature")
    public void authAsAdminTest() {
        adminToken = restClient.authenticate("admin", "setup");
        assertNotNull(adminToken);
    }

    @Test(description = "Create new customer by admin", dependsOnMethods = "authAsAdminTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Get customer feature")
    public void customerCreateTest() {
        Faker faker = new Faker();
        customerPojo = new CustomerPojo();
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();
        customerPojo.pass = faker.internet().password(7, 11);
        customerPojo.login = faker.internet().emailAddress();
        customerPojo.balance = faker.number().numberBetween(0, 100);

        CustomerPojo result;
        result = restClient.createCustomer(customerPojo, adminToken);

        assertNotNull(result);
        assertEquals(customerPojo.firstName, result.firstName);
        assertEquals(customerPojo.lastName, result.lastName);
        assertEquals(customerPojo.login, result.login);
        assertEquals(customerPojo.balance, result.balance);
    }

    @Test(description = "Get customer by admin", dependsOnMethods = "customerCreateTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Get customer feature")
    public void customerGetTest(){
        List<CustomerPojo> customerPojoList = restClient.getCustomers(adminToken, customerPojo.login);
        assertNotNull(customerPojoList);
        assertNotEquals(customerPojoList.size(), 0);
        CustomerPojo resCustomerPojo = customerPojoList.get(0);
        assertEquals(customerPojo.firstName, resCustomerPojo.firstName);
        assertEquals(customerPojo.lastName, resCustomerPojo.lastName);
        assertEquals(customerPojo.login, resCustomerPojo.login);
        assertEquals(customerPojo.balance, resCustomerPojo.balance);
        customerPojo.id = resCustomerPojo.id;
    }

    @AfterClass
    public void afterClass() {
        restClient.deleteCustomer(customerPojo, adminToken);
    }
}

package org.nsu.fit.tests.api.customer;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class DeleteCustomerTest {
    private RestClient restClient;

    private AccountTokenPojo adminToken;
    private CustomerPojo customerPojo;

    @BeforeClass
    public void beforeClass() {
        restClient = new RestClient();
    }

    @Test(description = "Authenticate as admin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete customer feature")
    public void authAsAdminTest() {
        adminToken = restClient.authenticate("admin", "setup");
        assertNotNull(adminToken);
    }

    @Test(description = "Create new customer by admin", dependsOnMethods = "authAsAdminTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete customer feature")
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
        customerPojo.id = result.id;
        assertNotNull(result);
        assertEquals(customerPojo.firstName, result.firstName);
        assertEquals(customerPojo.lastName, result.lastName);
        assertEquals(customerPojo.login, result.login);
        assertEquals(customerPojo.balance, result.balance);
    }

    @Test(description = "Delete customer by admin", dependsOnMethods = "customerCreateTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete customer feature")
    public void customerDeleteTest(){
        restClient.deleteCustomer(customerPojo, adminToken);
    }

    @Test(description = "Try to get deleted customer by admin", dependsOnMethods = "customerDeleteTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete customer feature")
    public void customerGetTest(){
        List<CustomerPojo> customerPojoList = restClient.getCustomers(adminToken, customerPojo.login);
        assertNotNull(customerPojoList);
        assertEquals(0, customerPojoList.size());
    }
}

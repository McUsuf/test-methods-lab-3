package org.nsu.fit.tests.ui.customer;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.services.browser.BrowserService;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.nsu.fit.tests.ui.screen.AdminScreen;
import org.nsu.fit.tests.ui.screen.LoginScreen;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class CancelCustomerCreateTest {
    private Browser browser = null;
    private RestClient restClient;
    private AccountTokenPojo adminToken;
    private Faker faker;
    private CustomerPojo customerPojo;
    private String canceledLogin;
    private AdminScreen result;

    @BeforeClass
    public void beforeClass() {
        browser = BrowserService.openNewBrowser();
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
        faker = new Faker();
    }

    @Test(description = "Cancel customer creation via UI.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Cancel creating customer feature")
    public void cancelCustomerCreationTest() {
        String customerFirstName = faker.name().firstName();
        String customerLastName = faker.name().lastName();
        String customerLogin = faker.internet().emailAddress();
        String customerPass = faker.internet().password(7, 11);

        canceledLogin = customerLogin;

        result = new LoginScreen(browser)
                .loginAsAdmin()
                .createCustomer()
                .fillEmail(customerLogin)
                .fillPassword(customerPass)
                .fillFirstName(customerFirstName)
                .fillLastName(customerLastName)
                .clickCancel();

        List<CustomerPojo> customers = restClient.getCustomers(adminToken, customerLogin);

        assertNotNull(customers);

        for (CustomerPojo customer : customers) {
            if (customer.firstName.equals(customerFirstName) &&
                    customer.lastName.equals(customerLastName) &&
                    customer.login.equals(customerLogin) &&
                    customer.pass.equals(customerPass)) {
                customerPojo = customer;
            }
        }
        assertNull(customerPojo);
    }

    @Test(description = "Check customer not appears in table", dependsOnMethods = "cancelCustomerCreationTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Cancel creating customer feature")
    public void checkCustomerExists() {
        result.searchCustomer(canceledLogin);
        assertFalse(browser.isElementPresent(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[2]/div/div/div/table/tbody/tr[td='" + canceledLogin + "']/td")));
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
        }
    }
}

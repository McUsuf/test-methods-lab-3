package org.nsu.fit.tests.api.plan;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.PlanPojo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class DeletePlanClass {
    private RestClient restClient;
    private AccountTokenPojo adminToken;
    private PlanPojo planPojo;

    @BeforeClass
    public void beforeClass() {
        restClient = new RestClient();
    }

    @Test(description = "Authenticate as admin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete plan feature")
    public void authAsAdminTest() {
        adminToken = restClient.authenticate("admin", "setup");
        assertNotNull(adminToken);
    }

    @Test(description = "Create plan as admin", dependsOnMethods = "authAsAdminTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete plan feature")
    public void createPlanTest() {
        Faker faker = new Faker();
        planPojo = new PlanPojo();
        planPojo.details = faker.commerce().productName();
        planPojo.name = faker.name().title();
        planPojo.fee = faker.number().numberBetween(1, 10);

        PlanPojo result = restClient.createPlan(adminToken, planPojo);
        assertNotNull(restClient);
        assertEquals(planPojo.name, result.name);
        assertEquals(planPojo.details, result.details);
        assertEquals(planPojo.fee, result.fee);
        planPojo.id = result.id;
    }

    @Test(description = "Delete plan as admin", dependsOnMethods = "createPlanTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete plan feature")
    public void deletePlanTest() {
        restClient.deletePlan(adminToken, planPojo);
    }

    @Test(description = "Try to get deleted plan as admin", dependsOnMethods = "deletePlanTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete plan feature")
    public void getPlanTest() {
        List<PlanPojo> planPojoList = restClient.getPlans(adminToken);
        assertNotNull(planPojoList);
        assertEquals(0, planPojoList.size());
    }
}

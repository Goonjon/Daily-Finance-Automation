package testrunner;

import config.Setup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.AdminLoginPage;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;

public class AdminLoginTestRunner extends Setup {  // Extends base Setup class
    private AdminLoginPage adminLoginPage;

    @BeforeClass
    public void setUp() {
        adminLoginPage = new AdminLoginPage(driver);  // Reuses driver initialized in Setup class
    }

    @Test(description = "Login with Admin credentials from terminal")
    public void testAdminLogin() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Set a suitable timeout
        //hardcoded admin login cred for optional test purpose
        adminLoginPage.doLogin("admin@test.com","admin123");

        // Retrieve admin credentials securely from system properties
        //String adminUsername = System.getProperty("username");
        //String adminPassword = System.getProperty("password");

        // Perform login using admin credentials securely
        //adminLoginPage.doLogin(adminUsername, adminPassword);

        // Assert successful login by verifying Admin Dashboard header
        String headerActual = driver.findElement(By.tagName("h2")).getText();
        String headerExpected = "Admin Dashboard";
        Assert.assertTrue(headerActual.contains(headerExpected), "Login was not successful; 'Admin Dashboard' header not found.");
    }

    @Test(priority = 2, description = "Verify that the updated user email is displayed on the admin dashboard")
    public void testSearchUpdatedUserEmail() throws IOException, ParseException {
        // Retrieve the last registered user email
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/test/resources/users.json"));
        JSONObject userObj = (JSONObject) jsonArray.get(jsonArray.size() - 1);
        String updatedEmail = (String) userObj.get("email");

        // Perform search operation
        adminLoginPage.searchUserEmail(updatedEmail);

        // Assert the updated email is found in the dashboard
        Assert.assertTrue(adminLoginPage.isEmailPresentInDashboard(updatedEmail),
                "Updated email not found on the admin dashboard.");
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

}


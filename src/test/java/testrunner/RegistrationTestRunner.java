package testrunner;

import com.github.javafaker.Faker;
import config.Setup;
import config.UserModel;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import utils.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.Properties;

public class RegistrationTestRunner extends Setup {
    private String googleAccessToken;

    @BeforeClass
    public void setupConfig() throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        prop.load(fis);
        googleAccessToken = prop.getProperty("google_access_token");
    }

    @Test(priority = 1, description = "Register a user with all fields and verify email notification")
    public void userRegByAllFields() throws InterruptedException, IOException, ParseException, org.json.simple.parser.ParseException {
        RegistrationPage userReg = new RegistrationPage(driver, googleAccessToken);
        Faker faker = new Faker();
        userReg.btnRegister.click();
        Thread.sleep(2000);

        // Generate random test data
        String firstname = faker.name().firstName();
        String lastname = faker.name().lastName();

        //generate dynamic email like goonjonpromy1234+75@gmail.com
        //String email = "goonjonpromy1234+" + (int) (Math.random() * 100) + "@gmail.com";
        //generate dynamic email with long digit to avoid duplicate issue
        String email = "goonjonpromy1234+" +
                String.format("%04d", (int)(Math.random() * 10_000)) +
                String.format("%04d", (int)(Math.random() * 10_000)) +
                "@gmail.com";

        String password = faker.internet().password();
        String phonenumber = "01505" + Utils.generateRandomNumber(100000, 999999);
        String address = faker.address().fullAddress();

        UserModel userModel = new UserModel();
        userModel.setFirstname(firstname);
        userModel.setLastname(lastname);
        userModel.setEmail(email);
        userModel.setPassword(password);
        userModel.setPhonenumber(phonenumber);
        userModel.setAddress(address);
        userReg.doRegistration(userModel);

        // Assert success message after registration
        doRegAssertion();

        // Verify that a "Congratulations" email is received
        String emailSnippet = userReg.readLatestMail();
        System.out.println("Latest Email Snippet: " + emailSnippet);
        //delay for getting gmail message properly (optional)
//        try {
//            Thread.sleep(5000);
//        } catch (Exception exc) {
//
//        }
        // Assertion to check for "Congratulations" in the email
        Assert.assertTrue(emailSnippet.contains(" Welcome to our platform!"), "Email does not contain the expected 'Congratulations' text.");

        // Store user information in JSON after successful registration and email verification
        JSONObject userObj = new JSONObject();
        userObj.put("firstName", firstname);
        userObj.put("lastName", lastname);
        userObj.put("email", email);
        userObj.put("password", password);
        userObj.put("phoneNumber", phonenumber);
        userObj.put("address", address);
        Utils.saveUserInfo("./src/test/resources/users.json", userObj);
    }

    public void doRegAssertion() throws InterruptedException {
        Thread.sleep(2000);

        // Wait for the success message
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast")));

        // Get and verify the success message
        String successMessageActual = driver.findElement(By.className("Toastify__toast")).getText();
        String successMessageExpected = "successfully";
        System.out.println(successMessageActual);
        Assert.assertTrue(successMessageActual.contains(successMessageExpected), "Registration success message is not displayed.");
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}

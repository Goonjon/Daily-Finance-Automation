package testrunner;

import config.Setup;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ResetPassword;
import utils.Utils;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class ResetPasswordTestRunner extends Setup {
    private ResetPassword resetPassword;
    private Properties prop;
    private String newPassword; // Declare newPassword here

    @BeforeMethod
    public void setupTest() throws IOException {
        // Load properties
        prop = new Properties();
        try (FileInputStream fileInput = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(fileInput);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration properties file.");
        }

        // Initialize ResetPassword with driver and prop
        resetPassword = new ResetPassword(driver, prop.getProperty("google_access_token"));
    }

    @Test(priority = 1, description = "Negative Test - Empty Email Field")
    public void testResetPasswordWithEmptyEmail() {
        resetPassword.clickResetButton();
        resetPassword.enterEmail(""); // Leaving email field blank
        resetPassword.sendResetBtn();

        String validationError = resetPassword.emailInput.getAttribute("validationMessage");
        Assert.assertTrue(validationError.contains("Please fill out this field"),
                "Error message does not contain expected text for empty email field.");
    }

    @Test(priority = 2, description = "Negative Test - Unregistered Email")
    public void testResetPasswordWithUnregisteredEmail() {
        resetPassword.clearEmailField(); // Ensure the email field is clear before input
        resetPassword.enterEmail("unregisteredemail@example.com"); // Invalid or unregistered email
        resetPassword.sendResetBtn();

        String expectedError = "Your email is not registered";
        String actualError = resetPassword.getErrorMessage();
        Assert.assertEquals(actualError, expectedError, "Error message mismatch for unregistered email.");
    }

    @Test(priority = 3, description = "Reset Password with Registered Email")
    public void resetPasswordWithRegisteredEmail() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/test/resources/users.json"));

        if (jsonArray.isEmpty()) {
            System.out.println("No registered users found in users.json.");
            return;
        }

        JSONObject userObj = (JSONObject) jsonArray.get(jsonArray.size() - 1);
        String email = (String) userObj.get("email");

        System.out.println("Last registered user email: " + email);

        resetPassword.clearEmailField();
        resetPassword.enterEmail(email);
        resetPassword.sendResetBtn();

        String expectedMessage = "Password reset link sent to your email";
        String actualMessage = resetPassword.getResetSuccessMessage();
        Assert.assertEquals(actualMessage, expectedMessage, "Success message mismatch.");
    }

    public String getEmailList() {
        RestAssured.baseURI = "https://gmail.googleapis.com";
        Response res = given().contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("google_access_token"))
                .when().get("/gmail/v1/users/me/messages");
        JsonPath jsonPath = res.jsonPath();
        return jsonPath.get("messages[0].id");
    }

    public String getResetLinkFromEmail() {
        String messageId = getEmailList();
        RestAssured.baseURI = "https://gmail.googleapis.com";
        Response res = given().contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("google_access_token"))
                .when().get("/gmail/v1/users/me/messages/" + messageId);

        JsonPath jsonPath = res.jsonPath();
        String snippet = jsonPath.get("snippet");

        // Extract the reset link from the snippet
        String linkPrefix = "https://dailyfinance.roadtocareer.net/reset-password?token=";
        int startIndex = snippet.indexOf(linkPrefix);
        if (startIndex != -1) {
            int endIndex = snippet.indexOf(" ", startIndex);
            if (endIndex == -1) endIndex = snippet.length();
            return snippet.substring(startIndex, endIndex).trim();
        }
        return null;
    }

    @Test(priority = 4, description = "Navigate to Reset Password Link from Email")
    public void navigateToResetPasswordLink() {
        // Get the reset link from the latest email
        String resetLink = getResetLinkFromEmail();
        Assert.assertNotNull(resetLink, "Reset link not found in the email.");

        // Navigate to the reset password link
        driver.get(resetLink);

    }

    @Test(priority = 5, description = "Reset Password - Fill new password form")
    public void testFillNewPasswordForm() {
        // Fill in new password and confirm password fields
        newPassword = "123456"; //we have declared the pass in the public class to access from any test method
        resetPassword.enterNewPassword(newPassword);
        resetPassword.enterConfirmPassword(newPassword);

        // Submit the form
        resetPassword.clickConfirmResetPasswordBtn();

        // Expected message text that confirms the password reset
        String expectedSuccessMessage = "Password reset successfully";
        // Locate the element containing the message if available
        String pageContent = driver.findElement(By.xpath("//p[text()='Password reset successfully']")).getText();

        // Assert that the page content contains the success message
        Assert.assertTrue(pageContent.contains(expectedSuccessMessage),
                "Password reset success message not found in page content.");


    }

    @Test(priority = 6, description = "Login with New Password after Reset Password")
    public void loginWithNewPassword() throws IOException, ParseException {
        // Retrieve the last registered user email
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/test/resources/users.json"));

        if (jsonArray.isEmpty()) {
            System.out.println("No registered users found in users.json.");
            return;
        }

        JSONObject userObj = (JSONObject) jsonArray.get(jsonArray.size() - 1);
        String email = (String) userObj.get("email");
        //String newPassword = "123456"; // Manual hardcoded way to enter new password

        //methods that directly handles login after resetting password to ensure login successful
        resetPassword.enterLoginEmail(email);         // Input the email
        resetPassword.enterLoginPassword(newPassword); //get the pass from test method-5
        resetPassword.clickLoginButton();        // Click the login button

    }


    @Test(priority = 7, description = "Add two random items to the item list")
    public void testAddTwoItems() throws InterruptedException {
        // Click "Add Cost" to go to the add cost page
        resetPassword.btnAddCost.click();

        // Adding the first item
        resetPassword.enterItemName("Ice-Cream");
        resetPassword.enterAmount("50");
        resetPassword.adjustQuantity(2);  // Set quantity to 2
        resetPassword.enterPurchaseDate("10/15/2024");
        resetPassword.selectMonth("October");
        resetPassword.enterRemarks("First test item");
        resetPassword.clickSubmitButton();

        // Wait for alert and accept
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        resetPassword.btnAddCost.click();

        // Adding the second item
        resetPassword.enterItemName("Chicken Fry");
        resetPassword.enterAmount("75");
        resetPassword.adjustQuantity(1);  // Set quantity to 1
        resetPassword.enterPurchaseDate("07/22/2024");
        resetPassword.selectMonth("July");
        resetPassword.enterRemarks("Second test item");
        resetPassword.clickSubmitButton();

        wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

    }

    @Test(priority = 8, description = "Verify two items are showing in the item list")
    public void testVerifyTwoItemsInList() {

        // Wait for the item list to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table/tbody/tr/td")));

        // Locate the rows containing the specific item names
        List<WebElement> firstItem = driver.findElements(By.xpath("//td[text()='Ice-Cream']"));
        List<WebElement> secondItem = driver.findElements(By.xpath("//td[text()='Chicken Fry']"));

        // Assert that each item exists in the table
        Assert.assertEquals(firstItem.size(), 1, "Item 1 not found in the item list.");
        Assert.assertEquals(secondItem.size(), 1, "Item 2 not found in the item list.");

        // Alternatively, assert the total number of items found
        Assert.assertEquals(firstItem.size() + secondItem.size(), 2, "The item list does not contain exactly 2 items.");
    }

    @Test(priority = 9, description = "Update user gmail with a new gmail")
    public void updateUserProfileWithNewGmail() throws InterruptedException, IOException, ParseException {
        // Click on profile icon
        resetPassword.profileIcon.click();

        // Click on the first menu item (assuming it navigates to the profile page)
        resetPassword.profileMenuItem.get(0).click();

        // Click "Edit" on the profile page
        resetPassword.clickEditButton();

        // Update new Gmail
        //String updateEmail = "goonjonpromy1234+5@gmail.com";
        //String updateEmail = "goonjonpromy1234+" + (int) (Math.random() * 100) + "@gmail.com";
        //long digit dynamic email to avoid duplicate issue
        String updateEmail = "goonjonpromy1234+" +
                String.format("%04d", (int)(Math.random() * 10_000)) +
                String.format("%04d", (int)(Math.random() * 10_000)) +
                "@gmail.com";


        resetPassword.updateEmailInput(updateEmail);

        // Click on update button
        resetPassword.btnUpdate.click();

        // Handle browser alert popup after clicking the update button
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();  // Click OK on the alert

        // Log and assert profile update success
        String successMessageExpected = "User updated successfully";  // Adjust this based on actual UI message
        System.out.println("New gmail updated successfully.");

        // Store the updated email in JSON file using Utils
        JSONObject userObj = new JSONObject();
        userObj.put("email", updateEmail);
        Utils.saveUserInfo("./src/test/resources/users.json", userObj);
    }

    @Test(priority = 10, description = "Logout from profile page")
    public void doLogout() {
        resetPassword.doLogout();

    }


    @AfterMethod
    public void delay() throws InterruptedException {
        Thread.sleep(2000);
    }
}

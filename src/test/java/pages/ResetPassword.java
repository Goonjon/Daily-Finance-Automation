package pages;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ResetPassword {
    public WebDriver driver;
    private String googleAccessToken;

    // Locators using @FindBy
    @FindBy(css = "a[href='/forgot-password']")
    public WebElement resetButton;

    @FindBy(id = ":r0:")
    public WebElement emailInput;

    @FindBy(css = "button[type='submit']")
    public WebElement sendResetBtn;

    @FindBy(xpath = "//p[text()='Your email is not registered']")
    public WebElement errorMessage;

    @FindBy(xpath = "//p[text()='Password reset link sent to your email']")
    public WebElement resetSuccessMessage;

    @FindBy(css = "input[type='password']")  // Replace with actual locator for new password field
    public WebElement newPasswordInput;

    @FindBy(css = "#\\:r1\\:")  // Replace with actual locator for confirm password field
    public WebElement confirmPasswordInput;

    @FindBy(css = "button[type='submit']")  // Adjust selector if needed
    public WebElement confirmResetPasswordBtn;

    //Successfully login with new password elements
    @FindBy(id="email") // Adjust the locator as per your HTML
    public WebElement loginEmailInput;
    @FindBy(id="password") // Adjust the locator as per your HTML
    public WebElement loginPasswordInput;
    @FindBy(xpath = "//button[text()='Login']") // Adjust as needed
    public WebElement loginButton;

    //elements for add cost page
    @FindBy(className = "add-cost-button")
    public WebElement btnAddCost;
    @FindBy(id = "itemName")
    public WebElement itemNameField;
    @FindBy(id = "amount")
    public WebElement amountField;
    @FindBy(xpath = "//input[@type='number' and @readonly]")
    public WebElement quantityField;
    @FindBy(xpath = "//button[text()='+']")
    public WebElement btnQuantityPlus;
    @FindBy(xpath = "//button[text()='-']")
    public WebElement btnQuantityMinus;
    @FindBy(xpath = "//input[@id='purchaseDate']")
    public WebElement datePickerPurchaseDate;
    @FindBy(id = "month")
    public WebElement monthDropdown;
    @FindBy(id = "remarks")
    public WebElement remarksField;
    @FindBy(css = "button.submit-button")
    public WebElement submitButton;

    //elements for update new gmail
    @FindBy(tagName = "svg")
    public WebElement profileIcon;

    @FindBy(css = "li.MuiButtonBase-root.MuiMenuItem-root")
    public List<WebElement> profileMenuItem;

    @FindBy(xpath = "//button[text()='Edit']")
    public WebElement btnEdit;

    @FindBy(id = ":r6:")
    public WebElement updateEmailInput;

    @FindBy(xpath = "//button[text()='Update']")
    public WebElement btnUpdate;

    //elements for logout
    @FindBy(css = "[data-testid=AccountCircleIcon]")
    public WebElement btnProfileIcon;
    @FindBy(css = "[role=menuitem]")
    public List<WebElement> btnProfileMenuItems;



    // Constructor to initialize elements and set the Google access token
    public ResetPassword(WebDriver driver, String googleAccessToken) {
        this.driver = driver;
        this.googleAccessToken = googleAccessToken;
        PageFactory.initElements(driver, this);
    }

    public void clickResetButton() {
        resetButton.click();
    }

    // Methods to interact with Reset Password form
    public void enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void sendResetBtn() {
        sendResetBtn.click();
    }

    public String getErrorMessage() {
        return errorMessage.isDisplayed() ? errorMessage.getText() : null;
    }

    public String getResetSuccessMessage() {
        return resetSuccessMessage.isDisplayed() ? resetSuccessMessage.getText() : null;
    }

    public void clearEmailField() {
        try{
            Thread.sleep(2000);
            emailInput.sendKeys(Keys.CONTROL, "a");
            emailInput.sendKeys(Keys.BACK_SPACE);
        }catch (Exception ex){

        }
    }

    // Gmail API method to get the latest email ID
    public String getEmailList() {
        RestAssured.baseURI = "https://gmail.googleapis.com";
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + googleAccessToken)
                .when().get("/gmail/v1/users/me/messages");
        JsonPath jsonPath = res.jsonPath();
        return jsonPath.get("messages[0].id"); // Get the latest email ID
    }

    // Method to read the latest email and extract the reset link
    public String readLatestMail() {
        String messageId = getEmailList();
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + googleAccessToken)
                .when().get("/gmail/v1/users/me/messages/" + messageId);

        JsonPath jsonPath = res.jsonPath();
        String snippet = jsonPath.get("snippet"); // Get the email snippet
        return extractResetLink(snippet);
    }

    // Method to extract the reset link from the email snippet
    private String extractResetLink(String snippet) {
        String linkPrefix = "https://dailyfinance.roadtocareer.net/reset-password?token=";
        int startIndex = snippet.indexOf(linkPrefix);
        if (startIndex != -1) {
            int endIndex = snippet.indexOf(" ", startIndex); // End of the link (or use '\n' if the link is on a new line)
            if (endIndex == -1) endIndex = snippet.length(); // If it's the last part of the snippet
            return snippet.substring(startIndex, endIndex).trim();
        }
        return null; // Return null if no link is found
    }


    // New methods for password reset
    public void enterNewPassword(String newPassword) {
        newPasswordInput.clear();
        newPasswordInput.sendKeys(newPassword);
    }

    public void enterConfirmPassword(String confirmPassword) {
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(confirmPassword);
    }

    public void clickConfirmResetPasswordBtn() {
        confirmResetPasswordBtn.click();
    }

    public void enterLoginEmail(String email) {
        loginEmailInput.clear();
        loginEmailInput.sendKeys(email);
    }

    public void enterLoginPassword(String password) {
        loginPasswordInput.clear();
        loginPasswordInput.sendKeys(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    //methods for add cost page
    // Method to enter item name
    public void enterItemName(String itemName) {
        itemNameField.sendKeys(itemName);
    }
    // Method to enter amount
    public void enterAmount(String amount) {
        amountField.sendKeys(amount);
    }

    // Method to adjust quantity using Plus or Minus buttons
    public void adjustQuantity(int desiredQuantity) throws InterruptedException {
        // Assuming the initial quantity is 1
        int initialQuantity = 1;
        if (desiredQuantity > initialQuantity) {
            int clicksNeeded = desiredQuantity - initialQuantity;
            for (int i = 0; i < clicksNeeded; i++) {
                btnQuantityPlus.click();  // Increment quantity
                Thread.sleep(300);  // Short pause to allow UI to update
            }
        } else if (desiredQuantity < initialQuantity) {
            int clicksNeeded = initialQuantity - desiredQuantity;
            for (int i = 0; i < clicksNeeded; i++) {
                btnQuantityMinus.click();  // Decrement quantity
                Thread.sleep(300);  // Short pause to allow UI to update
            }
        }
    }


    //    // Method to enter quantity (manual way)
//    public void enterQuantity(String quantity) {
//        quantityField.click();
//        btnQuantityPlus.click();
//    }

    // Method to enter the purchase date (clearing and setting new date)
    public void enterPurchaseDate(String purchaseDate) {
        datePickerPurchaseDate.clear();        // Clear existing date
        datePickerPurchaseDate.sendKeys(purchaseDate); // Enter new date
    }

    // Method to select the month from the dropdown
    public void selectMonth(String month) {
        Select select = new Select(monthDropdown); // Use Select for dropdown
        select.selectByVisibleText(month);
    }

    // Method to enter remarks
    public void enterRemarks(String remarks) {
        remarksField.sendKeys(remarks);
    }
    // Method to click on the submit button
    public void clickSubmitButton() {
        submitButton.click();
    }

    //Methods for update new gmail
    //edit button click method
    public void clickEditButton() {
        // Wait for the Edit button and click it
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(btnEdit)).click();
    }
    //method for update email input
    public void updateEmailInput(String updateEmail){
        updateEmailInput.sendKeys(Keys.CONTROL, "a");
        updateEmailInput.sendKeys(Keys.BACK_SPACE);
        updateEmailInput.sendKeys(updateEmail);
    }

    //method for logout
    public void doLogout(){
        btnProfileIcon.click();
        btnProfileMenuItems.get(1).click();
    }
}

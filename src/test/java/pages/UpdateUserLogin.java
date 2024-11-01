package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;

public class UpdateUserLogin {
    public WebDriver driver;

    // Locators for login fields
    @FindBy(id = "email")
    public WebElement emailField;

    @FindBy(id = "password")
    public WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    public WebElement loginButton;

    //elements for logout
    @FindBy(css = "[data-testid=AccountCircleIcon]")
    public WebElement btnProfileIcon;
    @FindBy(css = "[role=menuitem]")
    public List<WebElement> btnProfileMenuItems;


    // Constructor
    public UpdateUserLogin(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Method to log in with an email and password
    public void login(String email, String password) {
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        loginButton.click();
    }


    // Method to assert login success
    public void assertLoginSuccess() {
        String headerActual = driver.findElement(By.className("MuiTypography-h6")).getText();
        String headerExpected = "Dashboard";
        Assert.assertTrue(headerActual.contains(headerExpected), "Login was not successful; 'Admin Dashboard' header not found.");
    }


    // Method to assert login failure
    public void assertLoginFailure() {
        String errorMessageActual = driver.findElement(By.tagName("p")).getText();
        String errorMessageExpected = "Invalid";
        Assert.assertTrue(errorMessageActual.contains(errorMessageExpected), "Login should have failed, but no error message was found.");
    }

    //method to clear credentials
    public void clearCreds(){
        emailField.sendKeys(Keys.CONTROL,"a");
        emailField.sendKeys(Keys.BACK_SPACE);
        passwordField.sendKeys(Keys.CONTROL,"a");
        passwordField.sendKeys(Keys.BACK_SPACE);
    }

    //method for logout
    public void doLogout(){
        btnProfileIcon.click();
        btnProfileMenuItems.get(1).click();
    }





}

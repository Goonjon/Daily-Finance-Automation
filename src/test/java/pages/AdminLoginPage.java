package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AdminLoginPage {
    public WebDriver driver;

    // Use @FindBy to locate elements
    @FindBy(id = "email")
    public WebElement usernameField;

    @FindBy(id = "password")
    public WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    public WebElement loginButton;

    //Search elements
    @FindBy(className = "search-box")
    public WebElement searchInput;

    @FindBy(xpath = "//table/thead/tr/th[contains(text(), 'Email')]")  // Adjust with the actual table locator
    public WebElement userTableRows;

    public AdminLoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);  // Initialize elements with PageFactory
    }

    // Method to perform login using credentials from system properties
    public void doLogin(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }

    //method to search the updated email is on the dashboard

    // Method to search for user by email
    public void searchUserEmail(String email) {
        searchInput.clear();
        searchInput.sendKeys(email);  // Enter the email in the search box
    }

    // Method to verify if email is present in the user table on the dashboard
    public boolean isEmailPresentInDashboard(String email) {
        // Wait until the table rows are loaded
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(userTableRows));

        // Locate rows in the table containing the email column
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr/td[contains(text(),'" + email + "')]"));

        // Check if any row contains the exact email text
        return rows.stream().anyMatch(row -> row.getText().equalsIgnoreCase(email));
    }
}

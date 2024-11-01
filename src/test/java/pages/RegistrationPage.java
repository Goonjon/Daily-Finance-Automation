package pages;
import config.UserModel;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

import static io.restassured.RestAssured.given;

public class RegistrationPage {
    @FindBy(css = "a[href='/register']")
    public WebElement btnRegister;
    @FindBy(id = "firstName")
    public WebElement txtFirstname;
    @FindBy(id = "lastName")
    WebElement txtLastname;
    @FindBy(id="email")
    WebElement txtEmail;
    @FindBy(id="password")
    WebElement txtPassword;
    @FindBy(id="phoneNumber")
    WebElement txtPhoneNumber;
    @FindBy(id="address")
    WebElement txtAddress;
    @FindBy(css = "[type=radio]")
    List<WebElement> rbGender;
    @FindBy(css = "[type=checkbox]")
    WebElement chkAcceptTerms;
    @FindBy(id="register")
    public WebElement btnSubmitReg;
    @FindBy(tagName = "a")
    public WebElement loginLink;



    private String googleAccessToken;

    public RegistrationPage(WebDriver driver, String googleAccessToken) {
        PageFactory.initElements(driver, this);
        this.googleAccessToken = googleAccessToken;
    }

    public void doRegistration(UserModel userModel){
        txtFirstname.sendKeys(userModel.getFirstname());
        txtLastname.sendKeys(userModel.getLastname()!=null?userModel.getLastname():"");
        txtEmail.sendKeys(userModel.getEmail());
        txtPassword.sendKeys(userModel.getPassword());
        txtPhoneNumber.sendKeys(userModel.getPhonenumber());
        txtAddress.sendKeys(userModel.getAddress()!=null?userModel.getAddress():"");
        rbGender.get(1).click();
        chkAcceptTerms.click();
        btnSubmitReg.click();
    }

    public String getEmailList() {
        RestAssured.baseURI = "https://gmail.googleapis.com";
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + googleAccessToken)
                .when().get("/gmail/v1/users/me/messages");

        JsonPath jsonPath = res.jsonPath();
        return jsonPath.get("messages[0].id");
    }

    public String readLatestMail() {
        String messageId = getEmailList();
        RestAssured.baseURI = "https://gmail.googleapis.com";
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + googleAccessToken)
                .when().get("/gmail/v1/users/me/messages/" + messageId);

        JsonPath jsonPath = res.jsonPath();
        return jsonPath.get("snippet");
    }
}
package testrunner;

import config.Setup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pages.UpdateUserLogin;

import java.io.FileReader;
import java.io.IOException;

public class UpdateUserLoginTestRunner extends Setup {  // Extend BaseTestSetup to inherit setup configurations
    private UpdateUserLogin updateUserLogin;
    private String newEmail;
    private String oldEmail;
    private final String password = "123456";  // The password for both emails

    @BeforeClass
    public void setupData() throws IOException, ParseException {
        // Initialize page object
        updateUserLogin = new UpdateUserLogin(driver);

        // Parse JSON file to get emails
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/test/resources/users.json"));

        // Get the new and old emails
        JSONObject newUserObj = (JSONObject) jsonArray.get(jsonArray.size() - 1);       // Last item
        JSONObject oldUserObj = (JSONObject) jsonArray.get(jsonArray.size() - 2);       // Second to last item

        newEmail = (String) newUserObj.get("email");
        oldEmail = (String) oldUserObj.get("email");
    }

    @Test(priority = 1, description = "Verify login with the old email fails")
    public void loginWithOldEmail() {
        updateUserLogin.login(oldEmail, password);
        updateUserLogin.assertLoginFailure();
    }

    @Test(priority = 2, description = "Verify login with the updated email is successful")
    public void loginWithUpdatedEmail() {
        updateUserLogin.clearCreds();
        updateUserLogin.login(newEmail, password);
        updateUserLogin.assertLoginSuccess();
        //updateUserLogin.doLogout();
    }

    @Test(priority = 3, description = "Again logout from profile page")
    public void doLogout(){
        updateUserLogin.doLogout();
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}

# Daily Finance Automation on Selenium-TestNG-Rest Assured
### Project Summary: This project automates essential user workflows on the DailyFinance website, covering account registration, password reset, and email validation to ensure smooth account management. It verifies that users can log in with both original and updated credentials, accurately update profiles, and view added items in their list. For admins, it includes secure login and the ability to search for updated user information on the dashboard, enhancing quality assurance and validating critical user flows on the site.

### Technologies I have used: 
- Language: Java
- Build System: Gradle
- Automation Tools & Frameworks: Selenium (for UI automation), TestNG (test management), Rest Assured (API calls and email retrieval)
- Data Handling: JSON (to store and retrieve user credentials and test data)

### Project Flow:
- Register a new user with valid details, verify successful email confirmation, and save registration data in JSON.
- Attempt password reset with invalid and valid inputs; verify email reset link is received and new password is set successfully.
- Log in with the updated password and verify that the user can access their account.
- Add two items to the user’s item list, then verify and assert both items appear correctly on the list.
- Update the user’s email in the profile section, log out, and validate login functionality with the new email while ensuring the old email fails.
- Securely log in to the admin account from the terminal, search for the updated email on the admin dashboard, and confirm the updated user’s presence in the user list data.
- Generate reports using Allure.
- Follow POM pattern for project structure.

### How to run?
1. Open IntelliJ IDEA and select New Project.
2. Create a Java project and name it.
3. Open the project in IntelliJ: File > Open > Select and expand folder > Open as project.
4. To run the test suites, use the following commands:
5. Run the test suite with the command: ```gradle clean test```
7. To generate the Allure report, run: ```allure generate allure-results --clean```
                                       ```allure serve allure-results```

### Screenshots Of the Allure Report:
<img width="960" alt="Screenshot 2024-11-01 200708" src="https://github.com/user-attachments/assets/92215ed5-e6d6-48d8-bee5-96945c392150">
<img width="960" alt="Screenshot 2024-11-01 200759" src="https://github.com/user-attachments/assets/b4e4e36b-45d6-4c62-afb9-22e9deb7370f">

### Video Record of DailyFinance Automation
### https://drive.google.com/file/d/1cAMogwTSrh_aXKJJZy90JEC5AFSUFI5o/view?usp=drive_link

### Test Case of DailyFinance Automation
https://docs.google.com/spreadsheets/d/1AoaOoE7frMgGmSjIQZ2I0-2EnaURPJZKwS5o2DviF6Y/edit?usp=drive_link




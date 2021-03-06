package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.SeleniumHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AutomatedSession {
    public final WebDriver driver;

    private final String GOOGLE_CHROME_PATH = "/app/.apt/usr/bin/google_chrome";
    private final String CHROMEDRIVER_PATH = "/app/.chromedriver/bin/chromedriver.exe";

    public AutomatedSession() {

        // Use following for local servers / When not being run on Heroku - replace CHROMEDRIVER_PATH with chromeDriverPath
        // Comment out options.setBinary(GOOGLE_CHROME_PATH);
        /*
        String chromeDriverPath = System.getProperty("user.dir") + "\\chromedriver.exe";
         */

        System.setProperty("webdriver.chrome.driver", System.getenv("CHROMEDRIVER_PATH"));


        ChromeOptions options = new ChromeOptions();
        options.setBinary(System.getenv("GOOGLE_CHROME_BIN"));
        options.addArguments("--no-sandbox", "--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--disable-dev-shm-usage");

        this.driver = new ChromeDriver(options);
    }

    protected String getDocumentNumber() {
        Matcher m = Pattern.compile("(?<=/)\\d{5,6}(?=/)").matcher(driver.getCurrentUrl());
        if (m.find()) {
            return m.group();
        }
        throw new RuntimeException("Could not find document number!");
    }

    protected abstract void fillRemarks(String remarks);

    protected abstract void fillTransportationFields(Release release, String clerkInitials);

    protected void loginTc3(LoginCredentials credentials) throws TimeoutException {
        System.out.println("\nLogging into TC3...");
        WebDriverWait webDriverWait = new WebDriverWait(driver , 20);
        driver.get("https://identity.qsl.com/u/login/identifier?state=hKFo2SBIVDJIUXlSYWdBTFBCVUFDVWgzRVRhUnAyZUVNVjZ1SqFur3VuaXZlcnNhbC1sb2dpbqN0aWTZIEVIRnFYMEh5T3NjX3liUnhOSkV3VjQ1b0FFSVVIU2tMo2NpZNkgYzRibDAyWVZjN1NWRWE2aXQ4RlFSVDRNOENaeU9RMWo");
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("username"))).sendKeys(credentials.getUsername() + Keys.RETURN);

        // Check for password field visibility and take respective action
        WebElement element = SeleniumHelper.waitForOneOfThreeElements(driver, 20, By.id("i0116"), By.id("i0118"), By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div"));
        if (element.getAttribute("id").equals("i0116")) {
            driver.findElement(By.id("idSIButton9")).click();
        }
        element = SeleniumHelper.waitForEitherElement(driver, 20, By.id("i0118"), By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div"));
        if (element.getAttribute("id").equals("i0118")) {
            element.sendKeys(credentials.getPassword() + Keys.RETURN);

            // Check for "Stay Signed-In", "More Info Required", or TC3 home page and take respective action
            try {
                element = SeleniumHelper.waitForOneOfThreeElements(driver, 60, By.id("idBtn_Back"), By.id("idSubmit_ProofUp_Redirect"), By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div"));
                if (element.getAttribute("id").equals("idSubmit_ProofUp_Redirect")) navigateMoreInfoRequired();
                else checkForStaySignedIn();

            } catch (TimeoutException e) {
                System.out.println("A TimeoutException occurred while waiting for user to authenticate sign-in credentials!");
            }
        }
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div")));
    }

    // Logic to be take when user is prompted for additional information after authentication
    private void navigateMoreInfoRequired() {
        final WebDriverWait webDriverWait = new WebDriverWait(driver, 30);

        driver.findElement(By.id("idSubmit_ProofUp_Redirect")).click();
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("id__13"))).click();

        checkForStaySignedIn();
    }

    /*
     Checks for "Stay Signed In" page presence, if present will click the "No" Button
     Always the last page displayed (if it is displayed) before navigation to TC3, thus if it is not visible can assume that TC3 page is displayed
     */
    private void checkForStaySignedIn() {

        WebElement element = SeleniumHelper.waitForEitherElement(driver, 60, By.id("idBtn_Back"), By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div"));
        if (element.getAttribute("id").equals("idBtn_Back")) element.click();
    }

    protected void endSession() {
        System.out.println("\nClosing WebDriver session...");
        try {
            driver.quit();
        } catch (Exception ignore) {}
    }
}

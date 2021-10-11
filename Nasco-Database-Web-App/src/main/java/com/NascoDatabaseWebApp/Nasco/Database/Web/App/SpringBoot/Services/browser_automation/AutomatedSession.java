package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.SeleniumHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AutomatedSession {
    public final WebDriver driver;

    public AutomatedSession() {
        String chromeDriverPath = System.getProperty("user.dir") + "\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        this.driver = new ChromeDriver(options);
    }

    protected abstract void fillRemarks(Release release);

    protected abstract void fillFields(Release release);

    protected void loginTc3(LoginCredentials credentials) {
        WebDriverWait webDriverWait = new WebDriverWait(driver , 20);
        driver.get("https://identity.qsl.com/u/login/identifier?state=hKFo2SBIVDJIUXlSYWdBTFBCVUFDVWgzRVRhUnAyZUVNVjZ1SqFur3VuaXZlcnNhbC1sb2dpbqN0aWTZIEVIRnFYMEh5T3NjX3liUnhOSkV3VjQ1b0FFSVVIU2tMo2NpZNkgYzRibDAyWVZjN1NWRWE2aXQ4RlFSVDRNOENaeU9RMWo");
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("username"))).sendKeys(credentials.getUsername() + Keys.RETURN);

        // Check for password field visibility and take respective action
        WebElement element = SeleniumHelper.waitForEitherElement(driver, 20, By.id("i0118"), By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div"));
        if (element.getAttribute("id").equals("i0118")) {
            element.sendKeys(credentials.getPassword() + Keys.RETURN);

            // Check for "Stay Signed-In" confirmation dialog and take respective action
            element = SeleniumHelper.waitForEitherElement(driver, 20, By.id("idBtn_Back"), By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div"));
            if (element.getAttribute("id").equals("idBtn_Back")) {
                element.click();
            }
        }
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div")));
    }

    protected void endSession() {
        try {
            driver.quit();
        } catch (Exception ignore) {}
    }
}

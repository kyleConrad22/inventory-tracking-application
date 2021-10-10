package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public abstract class AutomatedSession {
    public final WebDriver driver;

    public AutomatedSession() {
        String chromeDriverPath = System.getProperty("user.dir") + "\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        this.driver = new ChromeDriver(options);
    }

    protected void loginTc3(LoginCredentials credentials) {
        driver.get("https://tos.qsl.com/");
    }

    protected void endSession() {
        try {
            driver.quit();
        } catch (Exception ignore) {}
    }
}

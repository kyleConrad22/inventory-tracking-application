package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.*;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.enums.CargoType;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.enums.Site;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.enums.TransportationType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AutomatedSession {
    protected final WebDriver driver;

    public AutomatedSession() {
        String chromeDriverPath = System.getProperty("user.dir") + "\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        this.driver = new ChromeDriver(options);
    }

    protected String getDocumentNumber() {
        Matcher m = Pattern.compile("(?<=/)\\d{5,6}(?=/)").matcher(driver.getCurrentUrl());
        if (m.find()) {
            return m.group();
        }
        throw new RuntimeException("Could not find document number!");
    }

    protected void fillRemarks(String remarks) {
        System.out.println("\nAttempting to fill remarks...");
        driver.findElement(By.id("specialInstructions")).sendKeys(remarks);

    }

    protected void createNewAction(Site site, TransportationType transportationType, CargoType cargoType) {
        System.out.println("\nCreating a new reception...");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        driver.get("http://tos.qsl.com/client-inventories/receptions-of-materials");

        clickNewActionButton();

        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("react-select-9-input"))).sendKeys(site.toString() + Keys.RETURN);
        String radio = "radio_transportationType_";
        switch (transportationType) {
            case VESSEL:
                driver.findElement(By.cssSelector("label[for='" + radio + 0 + "']")).click();
                break;

            case TRUCK:
                driver.findElement(By.cssSelector("label[for='" + radio + 1 + "']")).click();
                break;

            case RAILCAR:
                driver.findElement(By.cssSelector("label[for='" + radio + 2 + "']")).click();
                break;

            case BARGE:
                driver.findElement(By.cssSelector("label[for='" + radio + 3 + "']")).click();
                break;

            case OTHER:
                driver.findElement(By.cssSelector("label[for='" + radio + 4 + "']")).click();
                break;

            case FROM_CONTAINER:
                driver.findElement(By.cssSelector("label[for='" + radio + 5 + "']")).click();
                break;
        }

        radio = "radio_cargoType_";
        if (cargoType.equals(CargoType.BULK)) {
            driver.findElement(By.cssSelector("label[for='" + radio + 0 + "']")).click();
        } else {
            driver.findElement(By.cssSelector("label[for='" + radio + 1 + "']")).click();
        }

        driver.findElement(By.xpath("/html/body/div[3]/div/form/header/menu/button[2]")).click();
    }

    protected void clickNewActionButton() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        WebElement element = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"viewport\"]/article/section/section[1]/div[2]/div[2]/button")));
        try {
            element.click();
        } catch (WebDriverException e) {
            SeleniumHelper.executeClickOnBlockedElement(driver, element);
        }
    }

    protected void fillTransportationFields(TransportationFields transportationFields) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);

        System.out.println("\nFilling transportation fields...");
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("carrier"))).sendKeys(transportationFields.getCarrier() + Keys.RETURN);
        driver.findElement(By.id("driverName")).sendKeys(transportationFields.getDriverName());
        driver.findElement(By.id("carrierBill")).sendKeys(transportationFields.getCarrierBill());
        driver.findElement(By.id("transportationNumber")).sendKeys(transportationFields.getTransportationNumber());

    }

    protected static String getClerkInitials(String username) {
        StringBuilder clerkInitials = new StringBuilder();
        for (String name : username.substring(0,username.indexOf("@qsl.com")).split("\\.")) {
            clerkInitials.append(name.charAt(0));
        }
        return clerkInitials.toString().toUpperCase(Locale.ROOT);
    }

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

            // Check for "Stay Signed-In" confirmation dialog and take respective action
            element = SeleniumHelper.waitForEitherElement(driver, 20, By.id("idBtn_Back"), By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div"));
            if (element.getAttribute("id").equals("idBtn_Back")) {
                element.click();
            }
        }
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"viewport\"]/article/section/div/div[1]/div[2]/div")));
    }

    // Try to click header button; if header button is not found then click on "sticky" header button
    protected void clickCreateButton() {
        try {
            driver.findElement(By.xpath("//*[@id=\"viewport\"]/article/section/form/section[1]/div[2]/div[2]/button[2]")).click();
        } catch (Exception e) {
            driver.findElement(By.xpath("//*[@id=\"viewport\"]/article/section/form/section[2]/div/div[2]/button[2]")).click();
        }

        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        webDriverWait.until(ExpectedConditions.urlContains("general-information"));
    }

    protected void endSession() {
        System.out.println("\nClosing WebDriver session...");
        try {
            driver.quit();
        } catch (Exception ignore) {}
    }
}

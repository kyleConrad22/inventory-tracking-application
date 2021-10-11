package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.AutomatedSession;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.SeleniumHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Reception extends AutomatedSession {
    protected abstract void createReception(Release release, String clerkInitials);
    protected abstract void createImportManifest(Release release);

    public void createNewReception(String site, String transportationType, String cargoType) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        driver.get("http://tos.qsl.com/client-inventories/receptions-of-materials");
        WebElement element = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"viewport\"]/article/section/section[1]/div[2]/div[2]/button")));
        try {
            element.click();
        } catch (WebDriverException e) {
            SeleniumHelper.executeClickOnBlockedElement(driver, element);
        }
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("react-select-11-input"))).sendKeys(site + Keys.RETURN);
        String radio = "radio_transportationType_";
        switch (transportationType) {
            case "Vessel":
                driver.findElement(By.id(radio + 0)).click();
                break;

            case "Truck":
                driver.findElement(By.id(radio + 1)).click();
                break;

            case "Railcar":
                driver.findElement(By.id(radio + 2)).click();
                break;

            case "Barge":
                driver.findElement(By.id(radio + 3)).click();
                break;

            case "Other":
                driver.findElement(By.id(radio + 4)).click();
                break;

            case "From Container":
                driver.findElement(By.id(radio + 5)).click();
                break;
        }

        radio = "radio_cargoType_";
        if (cargoType.equals("Bulk")) {
            driver.findElement(By.id(radio + 0)).click();
        } else {
            driver.findElement(By.id(radio + 1)).click();
        }

        driver.findElement(By.xpath("/html/body/div[3]/div/form/header/menu/button[2]")).click();
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-select-9-input")));
    }

    public void setInventory(String inventory) {
        driver.findElement(By.id("react-select-27-input")).sendKeys(inventory + Keys.RETURN);
    }

    public void clickCreateButton() {
        driver.findElement(By.id("//*[@id=\"viewport\"]/article/section/form/section[1]/div[2]/div[2]/button[2]")).click();
    }

    public void navigateToIncomingItems() {
        String url = driver.getCurrentUrl();
        driver.get(url.substring(0, url.lastIndexOf("/")) + "/incoming-items");
    }

    public void importManifest() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);

        WebElement element = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"viewport\"]/article/section/section[3]/div[2]div[1]")));
        try {
            element.click();
        } catch (WebDriverException e) {
            SeleniumHelper.executeClickOnBlockedElement(driver, element);
        }

        driver.findElement(By.cssSelector("input[type='file']")).sendKeys(System.getProperty("user.dir") + "/import-manifest.xlsx");
    }
}

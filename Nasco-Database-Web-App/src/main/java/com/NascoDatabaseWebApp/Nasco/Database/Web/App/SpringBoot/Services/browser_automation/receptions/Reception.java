package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.AutomatedSession;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Reception extends AutomatedSession {
    protected abstract void createReception(Release release);
    protected abstract XSSFWorkbook createImportManifest(Release release);

    public void createNewReception(String site, String transportationType, String cargoType) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        driver.get("http://tos.qsl.com/client-inventories/receptions-of-materials");
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"viewport\"]/article/section/section[1]/div[2]/div[2]/button")))
                .click();
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
    }

}

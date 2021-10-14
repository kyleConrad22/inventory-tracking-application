package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.AutomatedSession;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Shipment extends AutomatedSession {

    public abstract void createShipment(Gatepass gatepass, LoginCredentials tc3Credentials, LoginCredentials tmCredentials);

    /* TODO */
    protected void loginTm(LoginCredentials tmCredentials) {

    }

    /* TODO */
    protected void submitToTm(Gatepass gatepass) {

    }

    /*TODO*/
    protected void clickCreateButton() {

    }

    protected void clickAddDestination() {
        driver.findElement(By.xpath("//*[@id='viewport']/article/section/form/fieldset[2]/article/header/button")).click();
    }

    protected abstract String getRemarks(Release release, String clerkInitials);

    /* TODO */
    protected abstract void navigateToLoadingRequestOrShippedItems();

    /* TODO */
    protected void saveShipment() {

    }

    protected void setReceiver(String receiver) {
        WebElement element = driver.findElement(By.id("shipmentDestinations[0].receiver"));
        element.sendKeys(receiver);

        try {
            driver.findElement(By.cssSelector("div[title='Use non listed \"" + receiver + "\"']")).click();
        } catch (NoSuchElementException e) {
            element.sendKeys(Keys.RETURN);
        }
    }

    protected void setReceiverAddress(String receiverAddress) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);

        driver.findElement(By.xpath("//*[@id=\"viewport\"]/article/section/form/fieldset[2]/article/section/section/div/menu/section/div[4]/i")).click();
        webDriverWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("formattedAddress")))).sendKeys(receiverAddress);
        driver.findElement(By.xpath("/html/body/div[5]/div/header/menu/button[2]")).click();
    }

    protected abstract void addItemsToShipment(Release release);

}

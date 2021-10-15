package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.AutomatedSession;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Shipment extends AutomatedSession {

    protected boolean addLoadingRequest;

    public abstract void createShipment(Gatepass gatepass, LoginCredentials tc3Credentials, LoginCredentials tmCredentials);

    /* TODO */
    protected void loginTm(LoginCredentials tmCredentials) {

    }

    /* TODO */
    protected void submitToTm(Gatepass gatepass) {

    }

    protected void clickAddDestination() {
        driver.findElement(By.xpath("//*[@id='viewport']/article/section/form/fieldset[2]/article/header/button")).click();
    }

    protected abstract String getRemarks(Release release, String clerkInitials);

    protected void navigateToLoadingRequestOrShippedItems() {
        if (addLoadingRequest) {
            navigateToLoadingRequest();
        } else {
            navigatedToShippedItems();
        }
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

    protected void navigatedToShippedItems() {
        System.out.println("\nNavigating to shipped items...");
        String url = driver.getCurrentUrl();
        driver.get(url.substring(0, url.lastIndexOf("/")) + "/shipped-items");
    }

    protected void navigateToLoadingRequest() {
        System.out.println("\nNavigating to loading request...");
        String url = driver.getCurrentUrl();
        driver.get(url.substring(0,url.lastIndexOf("/")) + "/loading-request");
    }

}

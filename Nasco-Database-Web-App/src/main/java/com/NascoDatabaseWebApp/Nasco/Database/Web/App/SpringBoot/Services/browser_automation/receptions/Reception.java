package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.AutomatedSession;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.SeleniumHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Reception extends AutomatedSession {
    protected abstract void createReception(Release release, String clerkInitials);

    protected void createImportManifest(Release release) {
        System.out.println("\nCreating import-manifest...");
        try (
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(System.getProperty("user.dir") + "/import-manifest.xlsx"));
                FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") + "/import-manifest.xlsx")
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            clearUsedRows(sheet);
            addReleaseItems(sheet, release);
            workbook.write(out);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Import-manifest file couldn't be found: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred while attempting to write to import-manifest: " + e.getMessage());
        }

    }

    private void clearUsedRows(Sheet sheet) {

        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            sheet.removeRow(sheet.getRow(i));
        }
    }

    protected abstract void addReleaseItems(Sheet sheet, Release release);

    public void createNewReception(String site, String transportationType, String cargoType) {
        System.out.println("\nCreating a new reception...");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        driver.get("http://tos.qsl.com/client-inventories/receptions-of-materials");
        WebElement element = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"viewport\"]/article/section/section[1]/div[2]/div[2]/button")));
        try {
            element.click();
        } catch (WebDriverException e) {
            SeleniumHelper.executeClickOnBlockedElement(driver, element);
        }
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("react-select-9-input"))).sendKeys(site + Keys.RETURN);
        String radio = "radio_transportationType_";
        switch (transportationType) {
            case "Vessel":
                driver.findElement(By.cssSelector("label[for='" + radio + 0 + "']")).click();
                break;

            case "Truck":
                driver.findElement(By.cssSelector("label[for='" + radio + 1 + "']")).click();
                break;

            case "Railcar":
                driver.findElement(By.cssSelector("label[for='" + radio + 2 + "']")).click();
                break;

            case "Barge":
                driver.findElement(By.cssSelector("label[for='" + radio + 3 + "']")).click();
                break;

            case "Other":
                driver.findElement(By.cssSelector("label[for='" + radio + 4 + "']")).click();
                break;

            case "From Container":
                driver.findElement(By.cssSelector("label[for='" + radio + 5 + "']")).click();
                break;
        }

        radio = "radio_cargoType_";
        if (cargoType.equals("Bulk")) {
            driver.findElement(By.cssSelector("label[for='" + radio + 0 + "']")).click();
        } else {
            driver.findElement(By.cssSelector("label[for='" + radio + 1 + "']")).click();
        }

        driver.findElement(By.xpath("/html/body/div[3]/div/form/header/menu/button[2]")).click();
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-select-5-input")));
    }

    public void setInventory(String inventory) {
        System.out.printf("\nSetting inventory as %s...\n", inventory);
        driver.findElement(By.id("react-select-13-input")).sendKeys(inventory + Keys.RETURN);
    }

    // Try to click header button; if header button is not found then click on "sticky" header button
    public void clickCreateButton() {
        try {
            driver.findElement(By.xpath("//*[@id=\"viewport\"]/article/section/form/section[1]/div[2]/div[2]/button[2]")).click();
        } catch (Exception e) {
            driver.findElement(By.xpath("//*[@id=\"viewport\"]/article/section/form/section[2]/div/div[2]/button[2]")).click();
        }

        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        webDriverWait.until(ExpectedConditions.urlContains("general-information"));
    }

    public void navigateToIncomingItems() {
        System.out.println("\nNavigating to incoming items...");
        String url = driver.getCurrentUrl();
        driver.get(url.substring(0, url.lastIndexOf("/")) + "/incoming-items");
    }

    public void importManifest() {
        System.out.println("\nImporting manifest...");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);

        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"viewport\"]/article/section/section[3]/div[2]/div[1]")));
        driver.findElement(By.cssSelector("input[type='file']")).sendKeys(System.getProperty("user.dir") + "/import-manifest.xlsx");
        System.out.println("\nWaiting for success...");
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".Toastify__toast--success")));
        System.out.println("\nImport successful");

    }
}

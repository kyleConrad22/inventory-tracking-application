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

    public void setInventory(String inventory) {
        System.out.printf("\nSetting inventory as %s...\n", inventory);
        driver.findElement(By.id("react-select-13-input")).sendKeys(inventory + Keys.RETURN);
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

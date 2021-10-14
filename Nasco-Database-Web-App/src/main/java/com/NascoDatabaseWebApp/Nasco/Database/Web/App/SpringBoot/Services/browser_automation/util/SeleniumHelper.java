package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util;

import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Locale;
import java.util.concurrent.*;

public final class SeleniumHelper {

    // Waits for either element to become clickable then returns the retrieved element
    public static WebElement waitForEitherElement(WebDriver driver, Integer timeoutSeconds, By locator1, By locator2) throws TimeoutException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        WebDriverWait webDriverWait = new WebDriverWait(driver, 1);

        try {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            webDriverWait.until(ExpectedConditions.elementToBeClickable(locator1));
                            break;
                        } catch (TimeoutException ignore) { }
                        try {
                            webDriverWait.until(ExpectedConditions.elementToBeClickable(locator2));
                            break;
                        } catch (TimeoutException ignore) { }
                    }
                }
            };

            Future<?> f= service.submit(r);

            f.get(timeoutSeconds, TimeUnit.SECONDS);

        } catch (final InterruptedException e) {
            // The thread was interrupted during sleep, wait, or join
        } catch (final java.util.concurrent.TimeoutException e) {
            throw new TimeoutException();
            // Task time taken surpassed specified timeoutSeconds
        } catch (final ExecutionException e) {
            // An exception from within the runnable task
        } finally {
            service.shutdown();
        }

        try {
            return driver.findElement(locator1);
        } catch (NoSuchElementException e) {
            return driver.findElement(locator2);
        }
    }

    // Waits for either element to become clickable then returns the retrieved element
    public static WebElement waitForOneOfThreeElements(WebDriver driver, Integer timeoutSeconds, By locator1, By locator2, By locator3) throws TimeoutException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        WebDriverWait webDriverWait = new WebDriverWait(driver, 1);

        try {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            webDriverWait.until(ExpectedConditions.elementToBeClickable(locator1));
                            break;
                        } catch (TimeoutException ignore) { }
                        try {
                            webDriverWait.until(ExpectedConditions.elementToBeClickable(locator2));
                            break;
                        } catch (TimeoutException ignore) { }
                        try {
                            webDriverWait.until(ExpectedConditions.elementToBeClickable(locator3));
                            break;
                        } catch (TimeoutException ignore) { }
                    }
                }
            };

            Future<?> f= service.submit(r);

            f.get(timeoutSeconds, TimeUnit.SECONDS);

        } catch (final InterruptedException e) {
            // The thread was interrupted during sleep, wait, or join
        } catch (final java.util.concurrent.TimeoutException e) {
            throw new TimeoutException();
            // Task time taken surpassed specified timeoutSeconds
        } catch (final ExecutionException e) {
            // An exception from within the runnable task
        } finally {
            service.shutdown();
        }

        try {
            return driver.findElement(locator1);
        } catch (NoSuchElementException ignore) { }
        try {
            return driver.findElement(locator2);
        } catch (NoSuchElementException ignore) {
            return driver.findElement(locator3);
        }

    }

    public static void executeClickOnBlockedElement(WebDriver driver, WebElement element) {
        JavascriptExecutor ex = (JavascriptExecutor) driver;
        ex.executeScript("arguments[0].click()", element);
    }

}

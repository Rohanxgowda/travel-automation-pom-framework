package com.travel.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor jsExecutor;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15), Duration.ofMillis(250));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void scrollIntoView(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
        // Use an explicit short wait instead of Thread.sleep
        try {
            shortWait();
        } catch (Exception e) {
            // ignore - best-effort wait
        }
    }

    protected void clickWithJavaScript(WebElement element) {
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    protected void click(By locator) {
        WebElement element = waitForElementClickable(locator);
        try {
            // Try multiple click strategies
            try {
                element.click();
            } catch (Exception e1) {
                // If normal click fails, try scrolling into view
                scrollIntoView(element);
                try {
                    element.click();
                } catch (Exception e2) {
                    // If still fails, try JavaScript click
                    try {
                        clickWithJavaScript(element);
                    } catch (Exception e3) {
                        // Final attempt: force visibility and click
                        jsExecutor.executeScript(
                            "arguments[0].style.opacity = '1';" +
                            "arguments[0].style.visibility = 'visible';" +
                            "arguments[0].style.display = 'block';" +
                            "arguments[0].click();",
                            element
                        );
                    }
                }
            }
            // Small explicit short wait after click instead of Thread.sleep
            shortWait();
        } catch (Exception e) {
            // If normal click fails, try scrolling into view and JavaScript click
            scrollIntoView(element);
            clickWithJavaScript(element);
        }
    }

    /**
     * Short helper wait to replace Thread.sleep; waits up to 2 seconds for document.readyState to be 'complete'.
     */
    protected void shortWait() {
        try {
            WebDriverWait s = new WebDriverWait(driver, Duration.ofSeconds(2));
            s.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").toString().equals("complete"));
        } catch (Exception ignored) {
            // fallback: small timed wait using Thread.sleep as last resort
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected void sendKeys(By locator, String text) {
        waitForElementVisible(locator).sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForElementVisible(locator).getText();
    }

    protected void switchToNewTab() {
        String originalWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if(!originalWindow.equals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
}
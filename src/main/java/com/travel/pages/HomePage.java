package com.travel.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

import java.time.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class HomePage extends BasePage {
    // Locators
    // Primary navigation and widget locators
    private final By flightSearchWidget = By.cssSelector("[class*='fsw_inputBox'], [class*='fsw-inputBox']");
    private final By flightsTab = By.cssSelector("[class*='navigation'] [class*='flight'], [data-cy*='flight']");
    private final By flightMenu = By.cssSelector("[class*='menu_Flights'], [class*='navFlight']");
    
    // Search form locators with multiple possible selectors
    private final By fromCity = By.cssSelector("[data-cy='fromCity'], [for='fromCity'], [class*='from-city']");
    private final By toCity = By.cssSelector("[data-cy='toCity'], [for='toCity'], [class*='to-city']");
    private final By fromCityInput = By.cssSelector("[data-cy='fromCity'] input, input[placeholder*='From'], [class*='from-city'] input");
    private final By toCityInput = By.cssSelector("[data-cy='toCity'] input, input[placeholder*='To'], [class*='to-city'] input");
    
    // Suggestion list locators with fallbacks
    private final By searchSuggestion = By.cssSelector(".react-autosuggest__suggestions-list li, [class*='suggestion-list'] li");
    private final By autoSuggestList = By.cssSelector(".react-autosuggest__suggestions-container--open, [class*='suggestion-container']");
    private final By dateSelector = By.xpath("//div[contains(@class,'fsw_inputBox')]//label[@for='departure']");
    private final By nextMonthButton = By.xpath("//span[@aria-label='Next Month']");
    private final By calendarDays = By.xpath("//div[@class='DayPicker-Day' and not(contains(@class, 'disabled'))]");
    private final By searchButton = By.cssSelector("[data-cy='searchButton'], [class*='search-button'], button[type='submit'], a[class*='search']");
    private final By modalClose = By.cssSelector("span[class*='modalClose']");
    private final By loginFrame = By.id("webklipper-publisher-widget-container-notification-frame");
    private final By searchWidgetContainer = By.cssSelector("[class*='fsw'], [class*='flightSearchWidget']");
    private final By closeLoginPrompt = By.xpath("//a[@class='close']");
    private final By flightResults = By.cssSelector("div[class*='listingCard']");
    private final By flightPrice = By.cssSelector("div[class*='priceSection'] p");
    private final By flightDetails = By.xpath("//div[contains(@class, 'listingCard')]//p[contains(@class, 'airline')]");
    private final By loadingIndicator = By.cssSelector("div[class*='loading']");
    
    // For filters
    private final By sortDropdown = By.cssSelector("span[class*='sortby']");
    private final By priceSort = By.xpath("//span[text()='Price']");
    private final By nonStopFilter = By.xpath("//span[text()='Non Stop']");
    private final By airlinesFilter = By.xpath("//span[text()='Airlines']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToFlights() {
        try {
            // Initial page load wait
            waitForPageLoad();
            
            // Handle any initial popups
            handlePopups();
            
            // Try to click flights tab if it exists
            try {
                if (driver.findElement(flightsTab).isDisplayed()) {
                    clickWithJavaScript(driver.findElement(flightsTab));
                    Thread.sleep(2000);
                }
            } catch (Exception ignored) {
                // Flights tab might not be needed/present
            }
            
            // Verify the flight search form is accessible
            boolean widgetFound = false;
            int attempts = 0;
            while (!widgetFound && attempts < 3) {
                try {
                    // Try different ways to find the flight search widget
                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(flightSearchWidget),
                        ExpectedConditions.visibilityOfElementLocated(fromCity),
                        ExpectedConditions.visibilityOfElementLocated(fromCityInput)
                    ));
                    widgetFound = true;
                } catch (Exception e) {
                    attempts++;
                    if (attempts == 3) {
                        throw new RuntimeException("Could not access flight search functionality after multiple attempts");
                    }
                    // Clear any popups and retry
                    removeOverlays();
                    handlePopups();
                    Thread.sleep(2000);
                }
            }
            
            // Final overlay removal
            removeOverlays();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Navigation to flights was interrupted", e);
        }
    }

    private void handlePopups() {
        try {
            // Handle notification frame
            driver.switchTo().frame(driver.findElement(loginFrame));
            if (waitForElementClickable(closeLoginPrompt).isDisplayed()) {
                click(closeLoginPrompt);
            }
            driver.switchTo().defaultContent();
        } catch (Exception ignored) {
            // Frame might not be present
        }

        try {
            // Handle modal if present
            if (waitForElementVisible(modalClose).isDisplayed()) {
                click(modalClose);
            }
        } catch (Exception ignored) {
            // Modal might not be present
        }
        
        // Remove any additional overlays
        removeOverlays();
    }
    
    private void removeOverlays() {
        try {
            jsExecutor.executeScript(
                "var removeElements = function(selector) {" +
                "   document.querySelectorAll(selector).forEach(function(element) {" +
                "       element.remove();" +
                "   });" +
                "};" +
                "removeElements('.chNavIcon');" +
                "removeElements('[class*=\"banner\"]');" +
                "removeElements('[class*=\"overlay\"]');" +
                "removeElements('div[data-cy=\"NotificationModal\"]');" +
                "removeElements('div[data-cy=\"webklipper\"]');" +
                "removeElements('iframe[id*=\"notification\"]');" +
                "document.querySelectorAll('div[style*=\"z-index\"]').forEach(function(element) {" +
                "   if (parseInt(window.getComputedStyle(element).zIndex) > 999) {" +
                "       element.remove();" +
                "   }" +
                "});"
            );
            Thread.sleep(1000);
        } catch (Exception ignored) {
            // Script execution might fail
        }
    }

    private void waitForPageLoad() {
        try {
            // Initial wait for page load
            Thread.sleep(5000);
            
            // Wait for document ready state with retry
            int retries = 0;
            while (retries < 3) {
                try {
                    wait.until(webDriver -> {
                        String readyState = ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").toString();
                        String jQuery = ((JavascriptExecutor) webDriver)
                            .executeScript("if (window.jQuery) return jQuery.active; else return -1").toString();
                        return readyState.equals("complete") && (jQuery.equals("0") || jQuery.equals("-1"));
                    });
                    break;
                } catch (Exception e) {
                    retries++;
                    if (retries == 3) throw e;
                    Thread.sleep(2000);
                }
            }

            // Try multiple ways to verify page is ready
            try {
                // Check if flights tab/section is present
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(flightsTab),
                    ExpectedConditions.presenceOfElementLocated(flightMenu),
                    ExpectedConditions.presenceOfElementLocated(flightSearchWidget)
                ));
            } catch (Exception e) {
                System.out.println("Warning: Could not find standard flight elements, will continue...");
            }

            // Ensure we're at the top of the page for better interaction
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
            Thread.sleep(1000);

            // Remove potential blockers
            removeOverlays();
            
            // Switch to default content and remove any remaining overlays
            try {
                driver.switchTo().defaultContent();
                removeOverlays();
            } catch (Exception ignored) {}
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void enterSourceLocation(String source) {
        try {
            // Ensure page is fully loaded and handle initial setup
            waitForPageLoad();
            handlePopups();
            
            // Extra wait to ensure all animations are complete
            Thread.sleep(2000);
            
            // Try to find and interact with the search widget first
            WebElement searchWidget = wait.until(ExpectedConditions.presenceOfElementLocated(flightSearchWidget));
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchWidget);
            Thread.sleep(1000);
            
            // Ensure the from city field is visible and clickable
            WebElement fromCityElement = wait.until(ExpectedConditions.presenceOfElementLocated(fromCity));
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", fromCityElement);
            
            // Force remove any overlays before clicking
            removeOverlays();
            Thread.sleep(1000);
            
            // Try multiple click strategies
            try {
                fromCityElement.click();
            } catch (Exception e1) {
                try {
                    clickWithJavaScript(fromCityElement);
                } catch (Exception e2) {
                    // If both clicks fail, try to force show the input
                    jsExecutor.executeScript(
                        "arguments[0].style.display='block'; arguments[0].style.visibility='visible';",
                        wait.until(ExpectedConditions.presenceOfElementLocated(fromCityInput))
                    );
                }
            }
            Thread.sleep(2000);
            
            // Find and interact with the input field
            WebElement input = waitForElementVisible(fromCityInput);
            scrollIntoView(input);
            clickWithJavaScript(input);
            input.clear();
            input.sendKeys(source);
            Thread.sleep(2000);
            
            // Wait for suggestion list with increased timeout
            wait.until(ExpectedConditions.presenceOfElementLocated(autoSuggestList));
            wait.until(ExpectedConditions.visibilityOfElementLocated(autoSuggestList));
            
            // Find and click the first matching suggestion
            By specificCity = By.xpath(String.format("//li[contains(@class, 'react-autosuggest__suggestion')]//p[contains(text(), '%s')]", source));
            WebElement cityElement = wait.until(ExpectedConditions.elementToBeClickable(specificCity));
            clickWithJavaScript(cityElement);
            
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void enterDestinationLocation(String destination) {
        try {
            // Wait for source location interaction to complete
            Thread.sleep(2000);
            
            // Wait for To field to be ready
            WebElement toCityElement = waitForElementVisible(toCity);
            
            // Scroll the element into center view for better interaction
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", toCityElement);
            Thread.sleep(1000);
            
            // Try to click using different methods until successful
            try {
                waitForElementClickable(toCity);
                toCityElement.click();
            } catch (Exception e1) {
                try {
                    clickWithJavaScript(toCityElement);
                } catch (Exception e2) {
                    // If both clicks fail, try to remove overlays
                    removeOverlays();
                    clickWithJavaScript(toCityElement);
                }
            }
            Thread.sleep(1500);
            
            // Handle the input field with similar careful approach
            WebElement input = waitForElementVisible(toCityInput);
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", input);
            Thread.sleep(1000);
            
            try {
                waitForElementClickable(toCityInput);
                input.click();
            } catch (Exception e) {
                clickWithJavaScript(input);
            }
            
            input.clear();
            input.sendKeys(destination);
            Thread.sleep(2000);
            
            // Wait for suggestion list
            wait.until(ExpectedConditions.presenceOfElementLocated(autoSuggestList));
            wait.until(ExpectedConditions.visibilityOfElementLocated(autoSuggestList));
            
            // Find and click the first matching suggestion
            By specificCity = By.xpath(String.format("//li[contains(@class, 'react-autosuggest__suggestion')]//p[contains(text(), '%s')]", destination));
            WebElement cityElement = wait.until(ExpectedConditions.elementToBeClickable(specificCity));
            clickWithJavaScript(cityElement);
            
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void selectNextMonthDate() {
        try {
            // Click on the date selector to open calendar
            WebElement dateElement = waitForElementVisible(dateSelector);
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", dateElement);
            Thread.sleep(1000);
            clickWithJavaScript(dateElement);
            Thread.sleep(1500);

            // Click next month button
            WebElement nextMonth = waitForElementVisible(nextMonthButton);
            clickWithJavaScript(nextMonth);
            Thread.sleep(1500);

            // Get next month's date range (7-14)
            LocalDate nextMonthDate = LocalDate.now().plusMonths(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
            
            // Find all available dates
            List<WebElement> availableDates = driver.findElements(calendarDays);
            WebElement targetDate = null;
            
            // Try to find a date between 7th and 14th of next month
            for (WebElement date : availableDates) {
                String ariaLabel = date.getAttribute("aria-label");
                if (ariaLabel != null && !date.getAttribute("class").contains("disabled")) {
                    try {
                        LocalDate dateObj = LocalDate.parse(ariaLabel, formatter);
                        if (dateObj.getMonth() == nextMonthDate.getMonth() && 
                            dateObj.getDayOfMonth() >= 7 && dateObj.getDayOfMonth() <= 14) {
                            targetDate = date;
                            break;
                        }
                    } catch (Exception ignored) {
                        // Parse error - continue to next date
                    }
                }
            }
            
            // If no date found between 7-14, take first available date in next month
            if (targetDate == null && !availableDates.isEmpty()) {
                targetDate = availableDates.get(0);
            }
            
            if (targetDate != null) {
                jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", targetDate);
                Thread.sleep(1000);
                clickWithJavaScript(targetDate);
                Thread.sleep(1500);
            } else {
                throw new RuntimeException("No available dates found in next month");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Date selection was interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Error selecting date: " + e.getMessage(), e);
        }
    }

    public void clickSearch() {
        try {
            // Ensure search widget is visible
            WebElement widget = wait.until(ExpectedConditions.visibilityOfElementLocated(searchWidgetContainer));
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", widget);
            Thread.sleep(1000);

            // Remove any overlays before clicking
            removeOverlays();
            
            // Try to find the search button with increased timeout
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement searchBtn = longWait.until(ExpectedConditions.presenceOfElementLocated(searchButton));
            
            // Ensure the button is in view and clickable
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchBtn);
            Thread.sleep(1000);

            // Try multiple click strategies
            try {
                // Try regular click first
                longWait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();
            } catch (Exception e) {
                try {
                    // Try JavaScript click if regular click fails
                    jsExecutor.executeScript("arguments[0].click();", searchBtn);
                } catch (Exception e2) {
                    // Final attempt: force visibility and click
                    jsExecutor.executeScript(
                        "arguments[0].style.opacity = '1';" +
                        "arguments[0].style.visibility = 'visible';" +
                        "arguments[0].style.display = 'block';" +
                        "arguments[0].click();", searchBtn
                    );
                }
            }

            // Wait for click to take effect
            Thread.sleep(2000);
            
            // Verify search initiated
            try {
                // Look for loading indicator or results
                By loadingIndicator = By.cssSelector("[class*='loader'], [class*='loading'], [class*='progress']");
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(loadingIndicator),
                    ExpectedConditions.presenceOfElementLocated(flightResults)
                ));
            } catch (Exception ignored) {
                // Even if we can't verify loading state, continue
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Search operation was interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to perform search: " + e.getMessage(), e);
        }
    }

    public void waitForFlightResults() {
        // Wait for loading indicator to disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingIndicator));
        // Wait for flight results to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(flightResults));
    }

    public void sortByPrice() {
        // Open sort dropdown and select "Price"
        click(sortDropdown);
        // Wait for sort options to be visible
        waitForElementVisible(priceSort);
        // Click on price sort option
        click(priceSort);
        // Wait for re-sorting to complete
        waitForFlightResults();
    }

    public void filterByStops(int stops) {
        if (stops == 0) {
            click(nonStopFilter);
            waitForFlightResults();
        }
    }

    public void filterByAirlines(String airline) {
        click(airlinesFilter);
        By airlineOption = By.xpath(String.format("//p[text()='%s']", airline));
        click(airlineOption);
        waitForFlightResults();
    }

    public void printFlightDetails() {
        try {
            List<WebElement> flights = driver.findElements(flightResults);
            System.out.println("\n=== Flight Search Results ===");
            
            if (flights.isEmpty()) {
                System.out.println("No flights found!");
                return;
            }

            // Store flight details for comparison
            class FlightInfo {
                String airline;
                String departure;
                String duration;
                int price;
                
                @Override
                public String toString() {
                    return String.format("Airline: %s | Departure: %s | Duration: %s | Price: Rs.%d",
                        airline, departure, duration, price);
                }
            }
            
            List<FlightInfo> flightInfoList = new ArrayList<>();
            
            // Collect details for up to 5 flights
            for (int i = 0; i < Math.min(5, flights.size()); i++) {
                WebElement flight = flights.get(i);
                try {
                    FlightInfo info = new FlightInfo();
                    
                    // Get all flight details
                    WebElement details = flight.findElement(flightDetails);
                    WebElement price = flight.findElement(flightPrice);
                    
                    info.airline = details.getText().split("\n")[0];
                    info.departure = details.getText().split("\n")[1];
                    info.duration = details.getText().split("\n")[2];
                    info.price = Integer.parseInt(price.getText().replaceAll("[^0-9]", ""));
                    
                    flightInfoList.add(info);
                } catch (Exception e) {
                    System.out.println("Error parsing flight " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            // Sort by price
            flightInfoList.sort(Comparator.comparingInt(f -> f.price));
            
            // Print cheapest and second cheapest flights
            System.out.println("\n[Best] Cheapest Flight Option:");
            System.out.println(flightInfoList.get(0));
            
            if (flightInfoList.size() > 1) {
                System.out.println("\n[Alt] Second Cheapest Flight Option:");
                System.out.println(flightInfoList.get(1));
            }
            
            // Print price difference if we have both flights
            if (flightInfoList.size() > 1) {
                int priceDiff = flightInfoList.get(1).price - flightInfoList.get(0).price;
                System.out.println(String.format("\nPrice Difference: Rs.%d", priceDiff));
            }
            
            System.out.println("\n=== End of Search Results ===");
            
        } catch (Exception e) {
            System.err.println("Error printing flight details: " + e.getMessage());
        }
    }

    public void selectCheapestFlight() {
        List<WebElement> flights = driver.findElements(flightResults);
        if (!flights.isEmpty()) {
            // Click on the first (cheapest) flight after sorting
            clickWithJavaScript(flights.get(0));
            // Wait for selection to be processed
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Method to open Google in new tab and perform price comparison
    public void compareFlightPricesWithGoogle(String source, String destination) {
        try {
            // Store the original window handle
            String originalWindow = driver.getWindowHandle();
            String flightPriceText = "";

            // Get flight price if available
            List<WebElement> prices = driver.findElements(flightPrice);
            if (!prices.isEmpty()) {
                flightPriceText = prices.get(0).getText().replaceAll("[^0-9]", "");
            }

            // Open new tab using JavaScript
            jsExecutor.executeScript("window.open()");
            
            // Switch to the new tab (it will be the last window handle)
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // Navigate to Google and search for flights
            driver.get("https://www.google.com");
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
            searchBox.sendKeys("flights from " + source + " to " + destination);
            searchBox.sendKeys(Keys.ENTER);
            
            // Wait for some time to see the results
            Thread.sleep(3000);

            // Switch back to original tab
            driver.switchTo().window(originalWindow);
            
            // Log the comparison
            System.out.println("Price comparison completed. You can manually verify the prices.");
            if (!flightPriceText.isEmpty()) {
                System.out.println("MakeMyTrip Price: ₹" + flightPriceText);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Method to verify flight availability on multiple websites
    public void verifyFlightAcrossWebsites(String source, String destination) {
        try {
            // Store the original window handle
            String originalWindow = driver.getWindowHandle();

            // Create a list of websites to check
            String[] websites = {
                "https://www.google.com/travel/flights",
                "https://www.cleartrip.com",
                "https://www.goibibo.com"
            };

            // Open each website in a new tab
            for (String website : websites) {
                // Open new tab
                jsExecutor.executeScript("window.open()");
                
                // Switch to new tab
                for (String windowHandle : driver.getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow) && 
                        !driver.switchTo().window(windowHandle).getCurrentUrl().equals("about:blank")) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                }

                // Navigate to the website
                driver.get(website);
                Thread.sleep(2000);
                
                System.out.println("Opened: " + website);
            }

            // Switch back to original MakeMyTrip tab
            driver.switchTo().window(originalWindow);
            
            System.out.println("Flight search opened across multiple websites for comparison");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Method to close all additional tabs except the main one
    public void closeAdditionalTabs() {
        String originalWindow = driver.getWindowHandle();
        
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
        }
        
        // Switch back to the original tab
        driver.switchTo().window(originalWindow);
    }
}
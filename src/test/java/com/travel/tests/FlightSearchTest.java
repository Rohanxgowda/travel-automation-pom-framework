package com.travel.tests;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.travel.pages.HomePage;
import com.travel.utils.WebDriverFactory;

public class FlightSearchTest {
    private WebDriver driver;
    private HomePage homePage;
    private static final int MAX_RETRIES = 2;

    @BeforeMethod
    public void setUp() {
        try {
            driver = WebDriverFactory.createDriver("chrome");
            driver.get("https://www.makemytrip.com");
            homePage = new HomePage(driver);
        } catch (Exception e) {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception ignored) {}
            }
            throw new RuntimeException("Failed to set up test: " + e.getMessage(), e);
        }
    }

    @Test(retryAnalyzer = TestRetry.class)
    public void testFlightSearch() {
        try {
            // Step 1: Navigate to Flights section and wait for page load
            homePage.navigateToFlights();

            String source = "Delhi";
            String destination = "Mumbai";

            // Step 2: Enter flight details using full city names
            homePage.enterSourceLocation(source);
            homePage.enterDestinationLocation(destination);
            homePage.selectNextMonthDate();

            // Step 3: Search and analyze results
            homePage.clickSearch();
            
            // Step 4: Wait for and analyze results
            homePage.waitForFlightResults();
            homePage.sortByPrice();
            
            // Step 5: Get flight details
            homePage.printFlightDetails();
            
            // Step 6: Apply filters
            homePage.filterByStops(0);
            
            // Step 7: Compare prices across websites
            homePage.compareFlightPricesWithGoogle(source, destination);
            
            // Step 8: Open flight search in multiple websites for comparison
            homePage.verifyFlightAcrossWebsites(source, destination);
            
            // Give some time to manually verify the results
            Thread.sleep(3000);
            
            // Step 9: Close additional tabs
            homePage.closeAdditionalTabs();
            
            // Step 10: Final selection on MakeMyTrip
            homePage.selectCheapestFlight();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
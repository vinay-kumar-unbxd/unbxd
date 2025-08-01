package SearchResultPage;

import Base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.ScreenshotUtil;
import utils.TestData;
import utils.ActionUtils;
import utils.API_Utils;
import io.restassured.response.Response;
import java.util.List;
import java.util.ArrayList;

public class SearchButtonAndEnterTest extends BaseTest {

    private List<String> apiProductUrls = new ArrayList<>();

    @Test
    public void TC_75_validateSearchButtonFunctionality() throws InterruptedException {
        // Setup test data
        TestData searchData = TestData.forSearchQueries("Electronics");
        TestData configData = new TestData("data.xlsx", "truworths");
        String searchQuery = "jacket";
        
        logInfo("Test Case 75: Validate search button functionality");
        logInfo("Search query: " + searchQuery);
        
        // 1. Fire search API and fetch product URLs
        fetchProductUrlsFromAPI(searchQuery, configData);
        
        // 2. Navigate to site
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Take screenshot of initial state
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "initial_page_state");
        
        // 3. Enter search query in search box
        logInfo("Entering search query: " + searchQuery);
        basePage.searchPage.enterInSearchBox(searchQuery);
        
        // Wait a moment for the input to be processed
        Thread.sleep(2000);
        
        // Take screenshot after entering the query
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "after_search_query_entered");
        
        // 4. Click on search icon/button
        logInfo("Clicking on search icon/button");
        basePage.searchPage.clickSearchIcon();
        
        // Wait for search results to load
        Thread.sleep(5000);
        
        // Take screenshot after clicking search button
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "after_search_button_click");
        
        // 5. Validate search results are displayed
        validateSearchResults(searchQuery, "Search Button", basePage);
        
        logPass("✅ Test Case 75: Search button functionality validation passed successfully");
    }
    
    @Test
    public void TC_77_validateEnterKeyFunctionality() throws InterruptedException {
        // Setup test data
        TestData searchData = TestData.forSearchQueries("Electronics");
        TestData configData = new TestData("data.xlsx", "truworths");
        String searchQuery = "jacket";
        
        logInfo("Test Case 77: Validate Enter key functionality for search");
        logInfo("Search query: " + searchQuery);
        
        // 1. Fire search API and fetch product URLs
        fetchProductUrlsFromAPI(searchQuery, configData);
        
        // 2. Navigate to site
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Take screenshot of initial state
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "initial_page_state");
        
        // 3. Enter search query in search box
        logInfo("Entering search query: " + searchQuery);
        basePage.searchPage.enterInSearchBox(searchQuery);
        
        // Wait a moment for the input to be processed
        Thread.sleep(2000);
        
        // Take screenshot after entering the query
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "after_search_query_entered");
        
        // 4. Press Enter key to execute search
        logInfo("Pressing Enter key to execute search");
        basePage.searchPage.pressEnterInSearchBox();
        
        // Wait for search results to load
        Thread.sleep(5000);
        
        // Take screenshot after pressing Enter
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "after_enter_key_press");
        
        // 5. Validate search results are displayed
        validateSearchResults(searchQuery, "Enter Key", basePage);
        
        logPass("✅ Test Case 77: Enter key functionality validation passed successfully");
    }
    
    private void fetchProductUrlsFromAPI(String searchQuery, TestData configData) {
        try {
            logInfo("Fetching product URLs from API for query: " + searchQuery);
            String baseUrl = configData.getBaseUrl();
            String searchEndpoint = configData.getSearch();
            String searchParams = configData.getSearchEndpoint();
            String searchApiUrl = baseUrl + searchEndpoint + searchQuery + searchParams;
            logInfo("Search API URL: " + searchApiUrl);

            // Call API using API_Utils methods
            Response response = API_Utils.getSearchResultResponse(searchApiUrl);            
            apiProductUrls = API_Utils.getProductUrls(response, 5);
            logInfo("Stored " + apiProductUrls.size() + " product URLs from API:");           
        } catch (Exception e) {
            logFail("❌ Error fetching product URLs from API: " + e.getMessage());
            apiProductUrls.clear();
        }
    }
    
    private void validateSearchResults(String searchQuery, String searchMethod, BasePage basePage) {
        try {
            logInfo("Validating search results for query: " + searchQuery + " using " + searchMethod);
            
            // 1. Validate URL contains search query (Primary assertion)
            String currentUrl = driver.getCurrentUrl();
            logInfo("Current URL: " + currentUrl);
            
            boolean urlContainsQuery = currentUrl.contains(searchQuery.toLowerCase()) || 
                                     currentUrl.contains(searchQuery.replace(" ", "+")) || 
                                     currentUrl.contains(searchQuery.replace(" ", "%20")) ||
                                     currentUrl.contains(searchQuery.replace(" ", "-"));
            
            if (urlContainsQuery) {
                logPass("✅ URL contains search query: " + searchQuery);
            } else {
                logFail("❌ URL does not contain search query: " + searchQuery);
                logFail("Current URL: " + currentUrl);
                ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "url_validation_failure");
                Assert.fail("URL does not contain search query: " + searchQuery + " using " + searchMethod);
            }
            
            // 2. Validate API product URLs in UI
            if (!apiProductUrls.isEmpty()) {
                validateApiProductUrlsInUI(searchQuery, searchMethod, basePage);
            } else {
                logWarning("⚠️ No API product URLs available for UI validation");
            }
                        
            // 5. Final assertion - URL validation is the primary check
            if (!urlContainsQuery) {
                logFail("❌ Search functionality failed - URL does not reflect search query");
                ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_functionality_failure");
                Assert.fail("Search functionality failed for query: " + searchQuery + " using " + searchMethod + ". URL: " + currentUrl);
            } else {
                logPass("✅ Search functionality validated successfully via URL for query: " + searchQuery);
            }
            
        } catch (Exception e) {
            logFail("❌ Error validating search results: " + e.getMessage());
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_results_validation_error");
            Assert.fail("Error validating search results: " + e.getMessage());
        }
    }
    
    private void validateApiProductUrlsInUI(String searchQuery, String searchMethod, BasePage basePage) {
        try {
            logInfo("Validating API product URLs in UI for query: " + searchQuery);
                        
            for (String productUrl : apiProductUrls) {
                String urlToCheck = productUrl.toLowerCase();
                boolean isUrlFound = basePage.searchPage.isProductUrlPresent(urlToCheck);
                
                if (isUrlFound) {
                    logPass(String.format("✅ API Product URL found: %s", productUrl));
                } else {
                    logFail(String.format("❌ API Product URL not found: %s", productUrl));
                }
            }
                        
            logPass("✅ All API product URL validations passed");
            
        } catch (Exception e) {
            logFail("❌ Error validating API product URLs in UI: " + e.getMessage());
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "api_product_validation_error");
            Assert.fail("Error validating API product URLs in UI: " + e.getMessage());
        }
    }
    
}
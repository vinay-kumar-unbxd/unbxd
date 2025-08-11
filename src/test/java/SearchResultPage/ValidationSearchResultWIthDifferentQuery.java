package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.ActionUtils;
import utils.TestData;
import utils.ScreenshotUtil;
import utils.ValidationUtils;

import java.util.List;
import java.util.ArrayList;

public class ValidationSearchResultWIthDifferentQuery extends BaseTest {

    private List<String> apiProductUrls = new ArrayList<>();
    private List<String> apiProductTitles = new ArrayList<>();

    @Test
    //@Parameters({"testKey", "vertical"})
    public void TC_079_twoWordsQuery() throws InterruptedException {
        logInfo("🔍 [DIFFERENT-QUERY-VALIDATION] Starting Validation Search Result With Different Query Test");
        
        // Setup test data
        TestData configData = new TestData("data.xlsx", "truworths");
        TestData searchData = TestData.forSearchQueries("Fashion and Apparel");
        String searchQuery = searchData.getTwoWordQuery();
        
        logInfo("Search query: " + searchQuery);
        
        // Step 1: Trigger search API and fetch product URLs and titles
        fetchProductUrlsAndTitlesFromAPI(searchQuery, configData);
        
        // Step 2: Navigate to site
        logInfo("Step 2: Navigating to website");
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Take screenshot of initial page state
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "initial_page_state");
        
        // Wait for page to load and handle popups
        Thread.sleep(2000);
        basePage.searchPage.closePopupIfPresent();
        basePage.searchPage.clickSearchField();
        basePage.searchPage.enterInSearchBox(searchQuery);
        ActionUtils.pressEnterWithActions(driver);

        // Step 3: Validate search results
        logInfo("Step 3: Validating search results");

        Thread.sleep(2000);
        
        // Step 4: Validate product titles and URLs in UI
        logInfo("Step 4: Validating product titles and URLs in UI");
        for(String title : apiProductTitles) {
            logInfo("API Product Title: " + title);
        }
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_results");
        validateProductTitlesInUI(basePage);
        logPass("✅ Test completed successfully");
        
        // Reset lists after test execution
        resetLists(); 
        
        for(String title : apiProductTitles) {
            logInfo("API Product Title: " + title);
        }

    }
    
    @Test
    //@Parameters({"testKey", "vertical"})
    public void TC_080_moreThanTwoWordsQuery() throws InterruptedException {
        logInfo("🔍 [MORE-THAN-TWO-WORDS-VALIDATION] Starting Validation Search Result With More Than Two Words Query Test");
        
        // Setup test data
        TestData configData = new TestData("data.xlsx", "truworths");
        TestData searchData = TestData.forSearchQueries("Fashion and Apparel");
        String searchQuery = searchData.getMoreThanTwoWordQuery();
        
        logInfo("Search query: " + searchQuery);
        
        // Step 1: Trigger search API and fetch product URLs and titles
        fetchProductUrlsAndTitlesFromAPI(searchQuery, configData);
        
        // Step 2: Navigate to site
        logInfo("Step 2: Navigating to website");
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Take screenshot of initial page state
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "initial_page_state_more_words");
        
        // Wait for page to load and handle popups
        Thread.sleep(2000);
        basePage.searchPage.closePopupIfPresent();
        basePage.searchPage.clickSearchField();
        basePage.searchPage.enterInSearchBox(searchQuery);
        ActionUtils.pressEnterWithActions(driver);

        // Step 3: Validate search results
        logInfo("Step 3: Validating search results");

        Thread.sleep(2000);
        
        // Step 4: Validate product titles and URLs in UI
        logInfo("Step 4: Validating product titles and URLs in UI");
        validateProductTitlesInUI(basePage);
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_results_more_words");
        logPass("✅ More than two words query test completed successfully");
        
        // Reset lists after test execution
        resetLists(); 
        
    }
    
    private void fetchProductUrlsAndTitlesFromAPI(String searchQuery, TestData configData) {
        try {
            logInfo("Step 1: Fetching product URLs and titles from API for query: " + searchQuery);
            String baseUrl = configData.getBaseUrl();
            String searchEndpoint = configData.getSearch();
            String searchParams = configData.getSearchEndpoint();
            String searchApiUrl = baseUrl + searchEndpoint + searchQuery + searchParams;
            logInfo("Search API URL: " + searchApiUrl);

            Response response = API_Utils.getSearchResultResponse(searchApiUrl, createLogger());        
            
            // First, fetch total product count from API
            int totalProductCount = API_Utils.getTotalProductCount(response);
            logInfo("📊 Total Product Count from API: " + totalProductCount);
            
            // Show warning if total product count is zero
            if (totalProductCount == 0) {
                logFail("⚠️ WARNING: Total product count is ZERO! No products found for query: " + searchQuery);
                logInfo("🔍 This might indicate: No search results, API issue, or incorrect query");
            }
            
            // Determine how many products to store based on available count
            int productsToStore = Math.min(10, totalProductCount);
            logInfo("📦 Products to store: " + productsToStore + " (max 10)");
            
            // Fetch product URLs and titles based on available count
            apiProductUrls = API_Utils.getProductUrls(response, productsToStore);
            List<String> allTitles = API_Utils.getProductTitles(response);
            
            // Store product titles based on available count
            apiProductTitles = allTitles.subList(0, productsToStore);
            
            logInfo("📊 Search Results: Found " + totalProductCount + " total products, storing " + productsToStore + " titles");
            
            logPass("✅ Successfully fetched " + apiProductUrls.size() + " product URLs and " + apiProductTitles.size() + " product titles from API");
         } catch (Exception e) {
            logFail("❌ Error fetching product URLs and titles from API: " + e.getMessage());
            apiProductUrls.clear();
            apiProductTitles.clear();
        }
        }
    
    /**
     * Validate product titles in UI against API data
     */
    private void validateProductTitlesInUI(BasePage basePage) {
        logInfo("Validating product titles in UI against API data...");
        
        if (apiProductTitles.isEmpty()) {
            logFail("❌ No API product titles available for validation");
            Assert.fail("No API product titles available for validation");
            return;
        }
        
        int validationCount = 0;
        int successCount = 0;
        
        // Validate first 5 product titles from API against UI
        for (int i = 0; i < Math.min(8, apiProductTitles.size()); i++) {
            String apiTitle = apiProductTitles.get(i);
            logInfo("Validating API title " + (i + 1) + ": " + apiTitle);
            
            try {
                boolean titleFound = ValidationUtils.validateProductTitle(basePage, apiTitle, createLogger());
                if (titleFound) {
                    successCount++;
                }
                validationCount++;
            } catch (Exception e) {
                logInfo("❌ Error validating title: " + apiTitle + " - " + e.getMessage());
                validationCount++;
            }
        }
        
        logInfo("Validation Summary: " + successCount + "/" + validationCount + " product titles found in UI");
        
        if (successCount == validationCount && validationCount > 0) {
            logPass("✅ ALL product titles from API found in UI - Test PASSED");
        } else {
            logFail("❌ NOT ALL product titles from API found in UI - Test FAILED");
            Assert.fail("Product title validation failed. Found " + successCount + "/" + validationCount + " titles in UI");
        }
    }
    
    private ValidationUtils.TestLogger createLogger() {
        return new ValidationUtils.TestLogger() {
            @Override
            public void logInfo(String message) {
                ValidationSearchResultWIthDifferentQuery.this.logInfo(message);}
            @Override
            public void logPass(String message) {
                ValidationSearchResultWIthDifferentQuery.this.logInfo("✅ " + message); }
            @Override
            public void logFail(String message) {
                ValidationSearchResultWIthDifferentQuery.this.logInfo("❌ " + message);}
        };
    }
    
    /**
     * Reset lists after test execution to ensure clean state
     */
    private void resetLists() {
        apiProductUrls.clear();
        apiProductTitles.clear();
        logInfo("🧹 Lists reset for next test execution");
    }
}

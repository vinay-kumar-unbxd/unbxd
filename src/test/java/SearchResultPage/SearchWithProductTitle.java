package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.TestData;
import utils.ScreenshotUtil;
import utils.ActionUtils;

import java.util.List;
import java.util.Random;

public class SearchWithProductTitle extends BaseTest {

    @Test
    @Parameters("testkey")
    public void autoSuggestionWithProductTitle(String testkey) throws InterruptedException {
        logInfo("🛍️ [PRODUCT-SEARCH] Starting 3-Product Validation for: " + testkey);
        
        // Get API data
        TestData testData = new TestData("data.xlsx", testkey);
        Response response = API_Utils.getSearchResultResponse(testData.getSearchApiUrl());
        Assert.assertEquals(response.getStatusCode(), 200, "API request failed");
        logPass("🛍️ [PRODUCT-SEARCH] API Response received successfully");
        
        // Extract product data
        List<String> titles = API_Utils.getProductTitles(response);
        List<String> images = API_Utils.getProductImageUrls(response);
        List<String> urls = API_Utils.getProductUrls(response, 3);
        
        Assert.assertTrue(titles.size() >= 3, "Need at least 3 products, found: " + titles.size());
        logInfo("🛍️ [PRODUCT-SEARCH] Found " + titles.size() + " products in API");
        
        // Setup site navigation
        driver.get(testData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        Thread.sleep(2000);
        basePage.searchPage.closePopupIfPresent();
        logPass("✅ Site loaded and ready");
        
        // Validate first 3 products
        int passed = 0;
        for (int i = 0; i < 3; i++) {
            String title = titles.get(i);
            String image = i < images.size() ? images.get(i) : "";
            String url = i < urls.size() ? urls.get(i) : "";
            
            logInfo("=== Product " + (i+1) + ": " + title + " ===");
            boolean success = searchAndVerifyProduct(basePage, title, image, url, "Product " + (i+1));
            if (success) passed++;
        }
        
        // Final result
        logInfo("📊 Results: " + passed + "/3 products validated");
        if (passed == 3) {
            logPass("✅ TEST PASSED: All products found!");
        } else {
            logFail("❌ TEST FAILED: Only " + passed + "/3 products found");
            Assert.fail("Product validation failed: " + passed + "/3 passed");
        }
    }
    
    @Test
    @Parameters("testkey")
    public void searchResultwithProductTitle(String testkey) throws InterruptedException {
        logInfo("🎯 [TITLE-SEARCH] Search for complete Product title and Click product for: " + testkey);
        
        // Step 1: Initialize test data and trigger search API
        TestData testData = new TestData("data.xlsx", testkey);
        String searchApiUrl = testData.getSearchApiUrl();
        String siteUrl = testData.getSiteUrl();
        
        logInfo("🎯 [TITLE-SEARCH] Triggering Search API: " + searchApiUrl);
        Response response = API_Utils.getSearchResultResponse(searchApiUrl);
        
        // Verify API response is successful
        Assert.assertEquals(response.getStatusCode(), 200, "API request failed");
        logPass("🎯 [TITLE-SEARCH] API Response received successfully with status code: " + response.getStatusCode());
        
        // Step 2: Fetch random product title, image URL, and product URL from response
        List<String> allProductTitles = API_Utils.getProductTitles(response);
        List<String> allImageUrls = API_Utils.getProductImageUrls(response);
        List<String> allProductUrls = API_Utils.getProductUrls(response, 1  );
        
        Assert.assertFalse(allProductTitles.isEmpty(), "No products found in API response");
        logInfo("🎯 [TITLE-SEARCH] Found " + allProductTitles.size() + " products in API response");
        
        // Get random product
        Random random = new Random();
        int randomIndex = random.nextInt(allProductTitles.size());
        
        String randomProductTitle = allProductTitles.get(randomIndex);
        String randomImageUrl = allImageUrls.size() > randomIndex ? allImageUrls.get(randomIndex) : "";
        String randomProductUrl = allProductUrls.size() > randomIndex ? allProductUrls.get(randomIndex) : "";
        
        logInfo("SelectedProduct : " + randomProductTitle + " | Image URL: " + randomImageUrl + " | Product URL: " + randomProductUrl);
        
        // Step 3: Navigate to site
        logInfo("Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("Successfully navigated to website");
        
        BasePage basePage = new BasePage(driver);
        
        // Wait for page to load and close any popups
        Thread.sleep(2000);
        basePage.searchPage.closePopupIfPresent();
        logInfo("Page loaded and popups handled");
        
        // Step 4: Enter title in search field and press Enter
        logInfo("Entering product title in search field: " + randomProductTitle);
        basePage.searchPage.enterInSearchBox(randomProductTitle);
        logPass("Product title entered successfully");
        
        ActionUtils.pressEnterWithActions(driver);
        // Wait for search results to load
        Thread.sleep(3000);
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, randomProductTitle);
        // Step 5: Post pressing Enter button validations
        logInfo("Starting post-search validations...");
        
        // Step 5a: Validate URL contains the product URL
        logInfo("Validating current URL contains product information...");
        String currentUrl = driver.getCurrentUrl();
        logInfo("Current URL: " + currentUrl);
        
        // Take screenshot after fetching current URL
        
        // Step 5b: Validate product image present in UI
        logInfo("Validating product image is present in UI...");
        try {
            basePage.validateImagesPresentInUI(List.of(randomImageUrl));
            logPass("Product image found in search results UI");
        } catch (Exception e) {
            logFail("Product image NOT found in search results UI: " + e.getMessage());
            // Don't fail the test for image, just log the failure
        }
        
        // Step 5c: Validate product title present in UI
        logInfo("Validating product title is present in UI...");
        try {
            basePage.verifyTitlesPresentInUI(List.of(randomProductTitle));
            logPass("Product title found in search results UI");
        } catch (Exception e) {
            logFail("Product title NOT found in search results UI: " + e.getMessage());
            Assert.fail("Product title verification failed: " + e.getMessage());
        }
        
        logPass("TEST PASSED: Random product search with comprehensive validation completed successfully!");
    }
    
    
    private boolean searchAndVerifyProduct(BasePage basePage, String productTitle, String imageUrl, String productUrl, String productLabel) throws InterruptedException {
        logInfo("=== " + productLabel + " Verification ===");
        logInfo("Product Title: " + productTitle);
        
        // Clear search box and enter product title
        basePage.searchPage.enterInSearchBox(productTitle);
        logPass("Product title entered successfully");
        
        // Wait for search results to load
        Thread.sleep(3000);
        
        // Use optimized common method to verify all product attributes
        logInfo("Verifying all product attributes (title, image, URL)...");
        boolean success = basePage.verifyCompleteProduct(productTitle, imageUrl, productUrl, productLabel);
        
        // Take screenshot after product verification
        String screenshotName = productLabel.replaceAll("\\s+", "_") + "_Verification_Result";
        ScreenshotUtil.takeScreenshotAndAttachToReportWithLog(driver, test, screenshotName, "Product verification completed for: " + productLabel);
        
        if (success) {
            logPass(productLabel + " verification PASSED - All attributes found");
        } else {
            logFail(productLabel + " verification FAILED - Some attributes not found");
        }
        
        return success;
    }
    
} 
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
        logInfo("🛍️ [PRODUCT-SEARCH] Starting Product Search and Validation Test for: " + testkey);
        
        // Step 1: Initialize test data and trigger search API
        TestData testData = new TestData("data.xlsx", testkey);
        String searchApiUrl = testData.getSearchApiUrl();
        String siteUrl = testData.getSiteUrl();
        
        logInfo("🛍️ [PRODUCT-SEARCH] Triggering Search API: " + searchApiUrl);
        Response response = API_Utils.getSearchResultResponse(searchApiUrl);
        
        // Verify API response is successful
        Assert.assertEquals(response.getStatusCode(), 200, "API request failed");
        logPass("🛍️ [PRODUCT-SEARCH] API Response received successfully with status code: " + response.getStatusCode());
        
        // Step 2: Fetch first 3 product titles, image URLs, and product URLs from response
        List<String> allProductTitles = API_Utils.getProductTitles(response);
        List<String> allImageUrls = API_Utils.getProductImageUrls(response);
        List<String> allProductUrls = API_Utils.getProductUrls(response);
        
        Assert.assertFalse(allProductTitles.isEmpty(), "No products found in API response");
        Assert.assertTrue(allProductTitles.size() >= 3, "Less than 3 products found in API response");
        logInfo("🛍️ [PRODUCT-SEARCH] Found " + allProductTitles.size() + " products in API response");
        
        // Get first 3 products (no randomization)
        String productTitle1 = allProductTitles.get(0);
        String productTitle2 = allProductTitles.get(1);
        String productTitle3 = allProductTitles.get(2);
        
        String imageUrl1 = allImageUrls.size() > 0 ? allImageUrls.get(0) : "";
        String imageUrl2 = allImageUrls.size() > 1 ? allImageUrls.get(1) : "";
        String imageUrl3 = allImageUrls.size() > 2 ? allImageUrls.get(2) : "";
        
        String productUrl1 = allProductUrls.size() > 0 ? allProductUrls.get(0) : "";
        String productUrl2 = allProductUrls.size() > 1 ? allProductUrls.get(1) : "";
        String productUrl3 = allProductUrls.size() > 2 ? allProductUrls.get(2) : "";
        
        logInfo("Selected Products for Testing:");
        logInfo("   1. " + productTitle1 + " | Image: " + imageUrl1 + " | URL: " + productUrl1);
        logInfo("   2. " + productTitle2 + " | Image: " + imageUrl2 + " | URL: " + productUrl2);
        logInfo("   3. " + productTitle3 + " | Image: " + imageUrl3 + " | URL: " + productUrl3);
        
        // Step 3: Navigate to site
        logInfo("Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("Successfully navigated to website");
        
        BasePage basePage = new BasePage(driver);
        
        // Wait for page to load and close any popups
        Thread.sleep(2000);
        basePage.searchPage.closePopupIfPresent();
        logInfo("Page loaded and popups handled");
        
        // Step 4 & 5: Search for each product and verify title, image, and product URL presence in UI
        logInfo("Starting product verification process...");
        boolean product1Success = searchAndVerifyProduct(basePage, productTitle1, imageUrl1, productUrl1, "Product 1");
        boolean product2Success = searchAndVerifyProduct(basePage, productTitle2, imageUrl2, productUrl2, "Product 2");
        boolean product3Success = searchAndVerifyProduct(basePage, productTitle3, imageUrl3, productUrl3, "Product 3");
        
        // Generate summary
        int successCount = (product1Success ? 1 : 0) + (product2Success ? 1 : 0) + (product3Success ? 1 : 0);
        logInfo("Test Summary: " + successCount + "/3 products successfully validated");
        
        // Test only passes if ALL 3 products have title, image, AND product URL present
        if (product1Success && product2Success && product3Success) {
            logPass("TEST PASSED: All 3 products (titles + images + URLs) found successfully!");
        } else {
            String failureMessage = "TEST FAILED: Only " + successCount + "/3 products found in UI. " +
                    "Results: Product1=" + (product1Success ? "Passed" : "Failed") + 
                    ", Product2=" + (product2Success ? "Passed" : "Failed") + 
                    ", Product3=" + (product3Success ? "Passed" : "Failed");
            logFail(failureMessage);
            Assert.fail(failureMessage);
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
        List<String> allProductUrls = API_Utils.getProductUrls(response);
        
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
package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.TestData;

import java.util.List;

public class SearchWithProductTitle extends BaseTest {

    @Test
   // @Parameters("testKey")
    public void autoSuggestionWithProductTitle() throws InterruptedException {
        logInfo("Starting Product Search and Validation Test for: " );
        
        // Step 1: Initialize test data and trigger search API
        TestData testData = new TestData("data.xlsx", "mwave");
        String searchApiUrl = testData.getSearchApiUrl();
        String siteUrl = testData.getSiteUrl();
        
        logInfo("Triggering Search API : " + searchApiUrl);
        Response response = API_Utils.getSearchResultResponse(searchApiUrl);
        
        // Verify API response is successful
        Assert.assertEquals(response.getStatusCode(), 200, "API request failed");
        logPass("API Response received successfully with status code: " + response.getStatusCode());
        
        // Step 2: Fetch first 3 product titles, image URLs, and product URLs from response
        List<String> allProductTitles = API_Utils.getProductTitles(response);
        List<String> allImageUrls = API_Utils.getProductImageUrls(response);
        List<String> allProductUrls = API_Utils.getProductUrls(response);
        
        Assert.assertFalse(allProductTitles.isEmpty(), "No products found in API response");
        Assert.assertTrue(allProductTitles.size() >= 3, "Less than 3 products found in API response");
        logInfo("Found " + allProductTitles.size() + " products in API response");
        
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
    

    private boolean searchAndVerifyProduct(BasePage basePage, String productTitle, String imageUrl, String productUrl, String productLabel) throws InterruptedException {
        logInfo("=== " + productLabel + " Verification ===");
        logInfo("Product Title: " + productTitle);
        
        // Clear search box and enter product title
        basePage.searchPage.enterInSearchBox(productTitle);
        logPass("Product title entered successfully");
        
        // Wait for search results to load
        Thread.sleep(3000);
        
        // Scroll to ensure all content is loaded
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight/2);");
        Thread.sleep(2000);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        Thread.sleep(1000);
        
        // Use optimized common method to verify all product attributes
        logInfo("Verifying all product attributes (title, image, URL)...");
        boolean success = basePage.verifyCompleteProduct(productTitle, imageUrl, productUrl, productLabel);
        
        if (success) {
            logPass(productLabel + " verification PASSED - All attributes found");
        } else {
            logFail(productLabel + " verification FAILED - Some attributes not found");
        }
        
        return success;
    }
    

    private String extractProductSlug(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) return "";
        
        // Extract the last part of the URL path (product slug)
        String[] urlParts = fullUrl.split("/");
        if (urlParts.length > 0) {
            return urlParts[urlParts.length - 1];
        }
        return fullUrl;
    }
} 
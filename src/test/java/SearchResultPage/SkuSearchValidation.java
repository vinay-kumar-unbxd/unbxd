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
import utils.ValidationUtils;

import java.util.List;
import java.util.Random;

public class SkuSearchValidation extends BaseTest {

    // Create TestLogger implementation for ValidationUtils
    private ValidationUtils.TestLogger createLogger() {
        return new ValidationUtils.TestLogger() {
            @Override
            public void logInfo(String message) {
                SkuSearchValidation.this.logInfo(message);
            }
            
            @Override
            public void logPass(String message) {
                SkuSearchValidation.this.logPass(message);
            }
            
            @Override
            public void logFail(String message) {
                SkuSearchValidation.this.logFail(message);
            }
        };
    }

    @Test
   @Parameters("testkey")
    public void validateSkuSearch(String testkey) throws InterruptedException {
        logInfo("🔍 [SKU-VALIDATION] Starting SKU Search Validation Test for: " + testkey);
        
        // Step 1: Initialize test data and trigger search API
        TestData testData = new TestData("data.xlsx", testkey);
        String searchApiUrl = testData.getSearchApiUrl();
        String siteUrl = testData.getSiteUrl();
        
        logInfo("🔍 [SKU-VALIDATION] Triggering Search API: " + searchApiUrl);
        Response response = API_Utils.getAndValidateApiResponse(searchApiUrl, createLogger());
        
        // Step 2: Fetch SKU, title, and image URL from response
        List<String> skuList = API_Utils.getProductSkus(response);
        List<String> productTitles = API_Utils.getProductTitles(response);
        List<String> imageUrls = API_Utils.getProductImageUrls(response);
        
        Assert.assertFalse(skuList.isEmpty(), "No SKUs found in API response");
        logInfo("[SKU-VALIDATION] Found " + skuList.size() + " SKUs in API response");
        
        // Get random product SKU, title, and image
        Random random = new Random();
        int randomIndex = random.nextInt(skuList.size());
        
        String selectedSku = skuList.get(randomIndex);
        String selectedTitle = productTitles.size() > randomIndex ? productTitles.get(randomIndex) : "";
        String selectedImageUrl = imageUrls.size() > randomIndex ? imageUrls.get(randomIndex) : "";
        
        // Store in variables
        logInfo("Selected Product Details: SKU: " + selectedSku + " | Title: " + selectedTitle + " | Image URL: " + selectedImageUrl);
        
        // Step 3: Navigate to site
        logInfo("Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("Successfully navigated to website");
        
        BasePage basePage = new BasePage(driver);
        basePage.closePopupIfPresent();
        
        // Wait for page to load and close any popups
        Thread.sleep(2000);
        
        // Step 4: Enter SKU in search field
        logInfo("Entering SKU in search field: " + selectedSku);
        basePage.searchPage.enterInSearchBox(selectedSku);
        logPass("SKU entered successfully in search field");
        Thread.sleep(1000);

        // Take screenshot before validation
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "Autosuggest_Validation_" + selectedSku);


        
        // Step 4.5: Validate title, SKU, and image before pressing Enter
        logInfo("=== SKU AUTOSUGGESTION VALIDATION ===");
        
        // Autosuggest validation
       boolean titleValidation = ValidationUtils.validateProductTitle(basePage, selectedTitle, createLogger());
       boolean skuValidation = ValidationUtils.validateProductSku(driver, selectedSku, createLogger());
       boolean imageValidation = ValidationUtils.validateProductImage(basePage, selectedImageUrl, createLogger());

       if (titleValidation && skuValidation && imageValidation) {
        logPass("✅ All autosuggest validations passed");
       } else {
        logFail("❌ Some autosuggestvalidations failed");
       }
     
        ActionUtils.pressEnterWithActions(driver);
        Thread.sleep(3000);
        
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "SKU_Search_Results_" + selectedSku);
        logInfo("Screenshot captured after SKU search");
        
        // Search results validation
        logInfo("=== SKU SEARCH RESULTS VALIDATION ===");
        
        String currentUrl = driver.getCurrentUrl();
        logInfo("Current URL after SKU search: " + currentUrl);
        
        boolean titleValidationResults = ValidationUtils.validateProductTitle(basePage, selectedTitle, createLogger());
        boolean skuValidationResults = ValidationUtils.validateProductSku(driver, selectedSku, createLogger());
        boolean imageValidationResults = ValidationUtils.validateProductImage(basePage, selectedImageUrl, createLogger());
        
        if (titleValidationResults && skuValidationResults && imageValidationResults) {
            logPass("✅ All search results validations passed");
        } else {
            logFail("❌ Some search results validations failed");
        }
        
        // Check both autosuggest and search results validations
        boolean autosuggestPassed = titleValidation && skuValidation && imageValidation;
        boolean searchResultsPassed = titleValidationResults && skuValidationResults && imageValidationResults;
        
        logInfo("=== FINAL TEST RESULT ===");
        logInfo("🔍 Autosuggest Validation: " + (autosuggestPassed ? "PASSED" : "FAILED"));
        logInfo("🎯 Search Results Validation: " + (searchResultsPassed ? "PASSED" : "FAILED"));
        
        if (autosuggestPassed && searchResultsPassed) {
            logPass("✅ TEST PASSED: Both autosuggest and search results validations successful for SKU: " + selectedSku);
        } else {
            logFail("❌ TEST FAILED: Some validations failed for SKU: " + selectedSku);
            Assert.fail("Test failed - Autosuggest: " + (autosuggestPassed ? "PASSED" : "FAILED") + 
                       ", Search Results: " + (searchResultsPassed ? "PASSED" : "FAILED"));
        }
    }
} 
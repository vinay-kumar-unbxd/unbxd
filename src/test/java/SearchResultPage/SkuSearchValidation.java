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

    @Test
    //@Parameters("testkey")
    public void validateSkuSearch() throws InterruptedException {
        logInfo("🔍 [SKU-VALIDATION] Starting SKU Search Validation Test");
        
        // Initialize test data and get API response
        TestData testData = new TestData("data.xlsx", "truworths");
        Response response = API_Utils.getAndValidateApiResponse(testData.getSearchApiUrl(), createLogger());
        
        // Extract product data
        List<String> skus = API_Utils.getProductSkus(response);
        List<String> titles = API_Utils.getProductTitles(response);
        List<String> imageUrls = API_Utils.getProductImageUrls(response);
        
        Assert.assertFalse(skus.isEmpty(), "No SKUs found in API response");
        
        // Select random product
        Random random = new Random();
        int randomIndex = random.nextInt(skus.size());
        String selectedSku = skus.get(randomIndex);
        String selectedTitle = titles.size() > randomIndex ? titles.get(randomIndex) : "";
        String selectedImageUrl = imageUrls.size() > randomIndex ? imageUrls.get(randomIndex) : "";
        
        logInfo("Selected Product: SKU: " + selectedSku + " | Title: " + selectedTitle);
        
        // Navigate to site and setup
        driver.get(testData.getSiteUrl());
        logPass("Successfully navigated to website");
        
        BasePage basePage = new BasePage(driver);
        basePage.closePopupIfPresent();
        Thread.sleep(2000);
        
        // Enter SKU in search box
        basePage.searchPage.enterInSearchBox(selectedSku);
        logPass("SKU entered successfully in search field");
        Thread.sleep(1000);

        // Take screenshot and validate autosuggest
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "Autosuggest_Validation_" + selectedSku);
        
        logInfo("=== SKU AUTOSUGGESTION VALIDATION ===");
        boolean autosuggestTitleValid = ValidationUtils.validateProductTitle(basePage, selectedTitle, createLogger());
        boolean autosuggestSkuValid = ValidationUtils.validateProductSku(driver, selectedSku, createLogger());
        boolean autosuggestImageValid = ValidationUtils.validateProductImage(basePage, selectedImageUrl, createLogger());
        boolean autosuggestPassed = autosuggestTitleValid && autosuggestSkuValid && autosuggestImageValid;
        
        if (autosuggestPassed) {
            logPass("✅ All autosuggestion validations passed");
        } else {
            logFail("❌ Some autosuggestion validations failed");
        }
        
        // Press Enter and navigate to search results
        ActionUtils.pressEnterWithActions(driver);
        Thread.sleep(3000);
        
        // Take screenshot and validate search results
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "SKU_Search_Results_" + selectedSku);
        
        logInfo("=== SKU SEARCH RESULTS VALIDATION ===");
        boolean searchResultsTitleValid = ValidationUtils.validateProductTitle(basePage, selectedTitle, createLogger());
        boolean searchResultsSkuValid = ValidationUtils.validateProductSku(driver, selectedSku, createLogger());
        boolean searchResultsImageValid = ValidationUtils.validateProductImage(basePage, selectedImageUrl, createLogger());
        boolean searchResultsPassed = searchResultsTitleValid && searchResultsSkuValid && searchResultsImageValid;
        
        if (searchResultsPassed) {
            logPass("✅ All search results validations passed");
        } else {
            logFail("❌ Some search results validations failed");
        }
        
        // Final assessment
        logInfo("=== FINAL TEST RESULT ===");
        logInfo("🔍 Autosuggest Validation: " + (autosuggestPassed ? "PASSED" : "FAILED"));
        logInfo("🎯 Search Results Validation: " + (searchResultsPassed ? "PASSED" : "FAILED"));
        
        if (autosuggestPassed && searchResultsPassed) {
            logPass("✅ TEST PASSED: Both validations successful for SKU: " + selectedSku);
        } else {
            logFail("❌ TEST FAILED: Some validations failed for SKU: " + selectedSku);
            Assert.fail("Test failed - Autosuggest: " + (autosuggestPassed ? "PASSED" : "FAILED") + 
                       ", Search Results: " + (searchResultsPassed ? "PASSED" : "FAILED"));
        }
    }

    /**
     * Create TestLogger implementation for ValidationUtils
     */
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
} 
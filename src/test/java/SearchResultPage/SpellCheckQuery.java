package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.API_Utils;
import utils.ActionUtils;
import utils.ScreenshotUtil;
import utils.TestData;
import pages.BasePage;

import java.util.List;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class SpellCheckQuery extends BaseTest {
    
    private List<String> productUrls = new ArrayList<>();
    private String correctedQuery = null;

    @Test
    public void tc_127_validateSpellCheckQueryAPI() {
        SoftAssert softAssert = new SoftAssert();
        
        // Setup test data
        TestData searchData = TestData.forSearchQueries("Fashion & Apparel");
        TestData configData = new TestData("data.xlsx", "unique-vintage");
        
        String baseUrl = configData.getBaseUrl();
        String searchEndpoint = configData.getSearch();
        String searchParams = configData.getSearchEndpoint();
        String spellCheckQuery = searchData.getSpellCheckQuery();
        
        // API validation
        String searchApiUrl = baseUrl + searchEndpoint + spellCheckQuery + searchParams;
        logInfo("Search API URL: " + searchApiUrl);
        Response response = API_Utils.getSearchResultResponse(searchApiUrl);
        int productCount = API_Utils.getTotalProductCount(response);
        
        // Store first 10 product URLs
        productUrls = API_Utils.getProductUrls(response, 10);
        
        // Check for spell correction
        String suggestion = API_Utils.getDidYouMeanSuggestion(response);
        String fallbackQuery = API_Utils.getFallbackQuery(response);
        
        correctedQuery = null;
        String correctionType = null;
        
        if (suggestion != null) {
            correctedQuery = suggestion; correctionType = "didYouMean";
            softAssert.assertTrue(productCount == 0, "✅ No products found for query: " + spellCheckQuery);
            logPass("✅ No products found for query: " + spellCheckQuery);
        } else if (fallbackQuery != null) {
            correctedQuery = fallbackQuery; correctionType = "fallback";
            softAssert.assertTrue(productCount > 0, "✅ Products found for query: " + spellCheckQuery);
            logPass("✅ Products found for query: " + spellCheckQuery);
        } else {
            softAssert.fail("Spell correction not found for query: " + spellCheckQuery);
        }
        
        // Test corrected query
        if (correctedQuery != null) {
            String correctedApiUrl = baseUrl + searchEndpoint + correctedQuery + searchParams;
            Response correctedResponse = API_Utils.getSearchResultResponse(correctedApiUrl);
            int correctedCount = API_Utils.getTotalProductCount(correctedResponse);
            
            softAssert.assertTrue(correctedCount > 0, correctionType + " should return products");
            
            // Store first 10 product URLs from corrected query if original query had no results
            if (productUrls.isEmpty() && correctedCount > 0) {
                productUrls = API_Utils.getProductUrls(correctedResponse, 10);
            }
        }      
        
        // Final assertions
        try {
            softAssert.assertAll();
            logPass("✅ All spell check validations passed");
        } catch (AssertionError e) {
            logFail("❌ Spell check validation failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(dependsOnMethods = "tc_127_validateSpellCheckQueryAPI")
    public void tc_128_validateSpellCheckInUI() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        
        // Setup test data
        TestData searchData = TestData.forSearchQueries("Fashion & Apparel");
        TestData configData = new TestData("data.xlsx", "unique-vintage");
        String spellCheckQuery = searchData.getSpellCheckQuery();
        logInfo("Spell check query: " + spellCheckQuery + " and corrected query: " + correctedQuery);
        // 1. Navigate to site
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // 2. Enter spell check query in search field
        basePage.searchPage.enterInSearchBox(spellCheckQuery);
        ActionUtils.pressEnterWithActions(driver);
        Thread.sleep(8000); // Wait for search results to load
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "spell_check_query_entered");
        
        
        // 4. Check for corrected query link and click if found
        if (correctedQuery != null) {
            try {
                // First try exact link text
                WebElement link = driver.findElement(By.partialLinkText(correctedQuery));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
                Thread.sleep(1000); // Wait for smooth scrolling
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
                logInfo("✅ Clicked spell correction link with text: " + correctedQuery);
            } catch (Exception e) {
                logInfo("No spell correction link found with text: " + correctedQuery);
            }
            Thread.sleep(2000); // Wait for page to load after clicking correction
        } else {
            logInfo("No corrected query to check for");
        }
        
        // 5. Validate stored URLs in UI HTML
        if (productUrls.isEmpty()) {
            logInfo("No product URLs to validate");
            return;
        }
        
        // Get the page source and validate each URL
        int totalProducts = productUrls.size();
        int foundProducts = 0;
        
        for (String productUrl : productUrls) {
            String urlToCheck = productUrl.toLowerCase();
            boolean isUrlFound = basePage.searchPage.isProductUrlPresent(urlToCheck);
            
            if (isUrlFound) {
                foundProducts++;
                logPass(String.format("✅ Product URL found (%d/%d): %s", foundProducts, totalProducts, productUrl));
            } else {
                logFail(String.format("❌ Product URL not found (%d/%d): %s", foundProducts, totalProducts, productUrl));
                softAssert.fail("Product URL not found: " + productUrl);
            }
        }
        
        // Take screenshot after validation
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "ui_spell_check_validation");
        
        // Only pass if all products are found
        if (foundProducts == totalProducts && totalProducts == 10) {
            logPass(String.format("✅ All %d products found successfully!", totalProducts));
        } else {
            String failMessage = String.format("❌ Only %d out of %d products found. Test requires all 10 products to be found.", 
                                            foundProducts, totalProducts);
            logFail(failMessage);
            softAssert.fail(failMessage);
        }
        
        softAssert.assertAll();
        logPass("✅ All spell check UI validations passed");
    }
    
    public List<String> getStoredProductUrls() {
        return new ArrayList<>(productUrls);
    }
    
 
    
    
    
    
} 
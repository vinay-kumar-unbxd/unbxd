package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
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
    @Parameters({"vertical", "testKey"})
    public void tc_127_validateSpellCheckQueryAPI(String vertical, String testKey) {
        SoftAssert softAssert = new SoftAssert();
        
        // Setup test data using parameter from XML
        TestData searchData = TestData.forSearchQueries(vertical);
        TestData configData = new TestData("data.xlsx", testKey);
        
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
    @Parameters({"vertical", "testKey"})
    public void tc_128_validateSpellCheckInUI(String vertical, String testKey) throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        
        // Setup test data using parameter from XML
        TestData searchData = TestData.forSearchQueries(vertical);
        TestData configData = new TestData("data.xlsx", testKey);
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

    @Test(dependsOnMethods = "tc_128_validateSpellCheckInUI")
    @Parameters({"vertical", "testKey"})
    public void validateTwoWordSpellError(String vertical, String testKey) throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        
        // Setup test data using parameter from XML
        TestData searchData = TestData.forSearchQueries(vertical);
        TestData configData = new TestData("data.xlsx", testKey);
        String twoWordSpellError = searchData.getTwoWordSpellCheck(); // Get from XML data

        logInfo("Test Case 125: Validate two-word spell error functionality");
        logInfo("Two-word spell error query: " + twoWordSpellError);

        String baseUrl = configData.getBaseUrl();
        String searchEndpoint = configData.getSearch();
        String searchParams = configData.getSearchEndpoint();
        String spellCheckQuery = twoWordSpellError;
        
        // API validation
        String searchApiUrl = baseUrl + searchEndpoint + spellCheckQuery + searchParams;
        logInfo("Search API URL: " + searchApiUrl);
        Response response = API_Utils.getSearchResultResponse(searchApiUrl);
        int productCount = API_Utils.getTotalProductCount(response);
        
        // Store first 10 product URLs
        List<String> productUrls = API_Utils.getProductUrls(response, 10);
        
         // Check for spell correction
         String suggestion = API_Utils.getDidYouMeanSuggestion(response);
         String fallbackQuery = API_Utils.getFallbackQuery(response);
         
         String correctedQuery = null;
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
         
         // 1. Navigate to site
         driver.get(configData.getSiteUrl());
         BasePage basePage = new BasePage(driver);

         // 2. Enter spell check query in search field
         basePage.searchPage.enterInSearchBox(twoWordSpellError);
         ActionUtils.pressEnterWithActions(driver);
         Thread.sleep(8000);
         logInfo("Current URL after search: " + driver.getCurrentUrl());
         ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "spell_check_search_results");



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
         // Final assertions
         try {
             softAssert.assertAll();
             logPass("✅ All spell check validations passed");
         } catch (AssertionError e) {
             logFail("❌ Spell check validation failed: " + e.getMessage());
             throw e;
         }
    }
    
    @Test(dependsOnMethods = "validateTwoWordSpellError")
    @Parameters({"vertical", "testKey"})
    public void tc_129_validateSpellCheckMessage(String vertical, String testKey) throws InterruptedException {
        // Setup test data using parameter from XML
        TestData searchData = TestData.forSearchQueries(vertical);
        TestData configData = new TestData("data.xlsx", testKey);
        String spellCheckQuery = searchData.getSpellCheckQuery();
        
        logInfo("Spell check query: " + spellCheckQuery);
        
        // 1. Navigate to site
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // 2. Enter spell check query in search field
        logInfo("Entering spell check query: " + spellCheckQuery);
        basePage.searchPage.enterInSearchBox(spellCheckQuery);
        ActionUtils.pressEnterWithActions(driver);
        
        // Wait for search results to load
        Thread.sleep(5000);
        logInfo("Current URL after search: " + driver.getCurrentUrl());
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "spell_check_search_results");
        
        // 3. Validate spell check message
        validateSpellCheckMessage(spellCheckQuery);
        
        logPass("✅ All spell check message validations passed");
    }
    
    private void validateSpellCheckMessage(String spellCheckQuery) {
        boolean spellCheckMessageFound = checkForSpellCheckMessage();
        
        if (spellCheckMessageFound) {
            String messageText = getSpellCheckMessageText();
            logPass("✅ Spell check message found and validated successfully: " + messageText);
        } else {
            logFail("❌ Spell check message not found");
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "spell_check_message_not_found");
            Assert.fail("Spell check message not found for query: " + spellCheckQuery);
        }
    }
    
    private boolean checkForSpellCheckMessage() {
        // Check XPath selectors
        if (checkSpellCheckXPathSelectors()) return true;
        
        // Check CSS selectors
        if (checkSpellCheckCssSelectors()) return true;
        
        // Check page source for phrases
        return checkSpellCheckPageSourcePhrases();
    }
    
    private boolean checkSpellCheckXPathSelectors() {
        String[] spellCheckPhrases = {
            "no results for", "showing results for", "did you mean",
            "instead", "search results for", "suggestions for"
        };

        for (String phrase : spellCheckPhrases) {
            String xpath = "//*[contains(translate(normalize-space(text()), " +
                    "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + phrase.toLowerCase() + "')]";
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (isAnyElementDisplayed(elements)) {
                    logInfo("Found spell check message via case-insensitive XPath: " + xpath);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }
    
    private String[] spellCheckCssSelectors = {
        ".spell-check-message", ".did-you-mean",".search-suggestion", ".spell-correction",
        ".search-correction", ".suggestion-message",".correction-message",
        ".spell-check",   ".search-suggestions",".correction-suggestions"
    };
    
    private boolean checkSpellCheckCssSelectors() {
        for (String cssSelector : spellCheckCssSelectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
                if (isAnyElementDisplayed(elements)) {
                    logInfo("Found spell check container via CSS: " + cssSelector);
                    return true;
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        return false;
    }
    
    private boolean checkSpellCheckPageSourcePhrases() {
        String pageSource = driver.getPageSource().toLowerCase();
        String[] spellCheckPhrases = {
            "no results for", "showing results for", "did you mean", "instead",
            "were found", "try searching for", "suggestions for", "search results for",
            "spell check", "correction", "suggestion", "did you mean:",
            "no results for", "showing results for", "instead"
        };
        
        for (String phrase : spellCheckPhrases) {
            if (pageSource.contains(phrase)) {
                logInfo("Found spell check phrase in page source: " + phrase);
                return true;
            }
        }
        return false;
    }
    
    private String getSpellCheckMessageText() {
        String[] spellCheckPhrases = {
            "no results for", "showing results for", "did you mean",
            "instead", "search results for", "suggestions for"
        };

        for (String phrase : spellCheckPhrases) {
            String xpath = "//*[contains(translate(normalize-space(text()), " +
                    "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + phrase.toLowerCase() + "')]";
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            return text.trim();
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        // Fallback to CSS selectors
        return findVisibleTextFromSelectors(spellCheckCssSelectors, By::cssSelector).orElse("Spell check message not found");
    }
    
    private java.util.Optional<String> findVisibleTextFromSelectors(String[] selectors, java.util.function.Function<String, By> byFunction) {
        for (String selector : selectors) {
            try {
                List<WebElement> elements = driver.findElements(byFunction.apply(selector));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            return java.util.Optional.of(text.trim());
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return java.util.Optional.empty();
    }
    
    private boolean isAnyElementDisplayed(List<WebElement> elements) {
        return elements.stream()
                .anyMatch(WebElement::isDisplayed);
    }
    
    public List<String> getStoredProductUrls() {
        return new ArrayList<>(productUrls);
    }
    



    
} 
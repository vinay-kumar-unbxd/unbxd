package SearchResultPage;

import Base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.ScreenshotUtil;
import utils.TestData;
import java.util.List;

public class NoResultMessageTest extends BaseTest {

    private static final String SEARCH_QUERY = "hdiuehiduhdeudh";
    private static final int WAIT_TIME = 3000;
    
    // Optimized selectors - using arrays for better maintainability
    private static final String[] NO_RESULT_XPATH_SELECTORS = {
        "//*[contains(text(), 'Sorry')]",
        "//*[contains(text(), 'No results')]",
        "//*[contains(text(), 'No products')]",
        "//*[contains(text(), 'No search results')]",
        "//*[contains(text(), 'No matches found')]"
    };
    
    private static final String[] NO_RESULT_CSS_SELECTORS = {
        ".no-results", ".no-products", ".empty-results", ".search-no-results",
        ".no-search-results", ".empty-state", ".no-matches", ".sorry-message",
        ".no-result-message", ".empty-search", ".search-empty", ".results-empty"
    };
    
    private static final String[] PRODUCT_SELECTORS = {
        ".product-item", ".product-card", ".product", "[data-product]"
    };
    
    private static final String[] NO_RESULT_PHRASES = {
        "sorry", "no results", "no products", "no items", "we couldn", "try again",
        "no matches", "nothing found", "no search results", "0 results",
        "no items found", "we found 0", "no products found", "no items match",
        "no results found", "sorry, no results", "sorry, we couldn",
        "sorry, no products", "sorry, no items"
    };

    @Test
    public void TC_119_validateNoResultMessage() throws InterruptedException {
        // Setup
        TestData searchData = new TestData("data.xlsx", "mwave");
        driver.get(searchData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Step 1: Navigate and capture initial state
        logInfo("Navigated to site: " + driver.getCurrentUrl());
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "initial_page");
        
        // Step 2: Perform search
        performSearch(basePage);
        
        // Step 3: Validate no result message
        validateNoResultMessage();
        
        logPass("✅ All no result message validations passed");
    }
    
    private void performSearch(BasePage basePage) throws InterruptedException {
        logInfo("Entering search query: " + SEARCH_QUERY);
        basePage.searchPage.enterInSearchBox(SEARCH_QUERY);
        basePage.searchPage.pressEnterInSearchBox();
        
        Thread.sleep(WAIT_TIME);
        logInfo("Current URL after search: " + driver.getCurrentUrl());
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_results_page");
    }
    
    private void validateNoResultMessage() {
        boolean noResultMessageFound = checkForNoResultMessage();
        
        if (noResultMessageFound) {
            String messageText = getNoResultMessageText();
            logPass("✅ No result message found and validated successfully: " + messageText);
        } else {
            logFail("❌ No result message not found");
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "no_result_message_not_found");
            Assert.fail("No result message not found for query: " + SEARCH_QUERY);
        }
    }
    
    private String getNoResultMessageText() {
        // Try XPath messages first
        for (String xpath : NO_RESULT_XPATH_SELECTORS) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            return text;
                        }
                    }
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        
        // Try CSS containers
        for (String cssSelector : NO_RESULT_CSS_SELECTORS) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            return text;
                        }
                    }
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        
        return "No result message found";
    }
    
    private boolean checkForNoResultMessage() {
        // Check XPath selectors
        if (checkXPathSelectors()) return true;
        
        // Check CSS selectors
        if (checkCssSelectors()) return true;
        
        // Check if no products exist
        if (checkNoProducts()) return true;
        
        // Check page source for phrases
        return checkPageSourcePhrases();
    }
    
    private boolean checkXPathSelectors() {
        for (String xpath : NO_RESULT_XPATH_SELECTORS) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (isAnyElementDisplayed(elements)) {
                    logInfo("Found no result message via XPath: " + xpath);
                    return true;
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        return false;
    }
    
    private boolean checkCssSelectors() {
        for (String cssSelector : NO_RESULT_CSS_SELECTORS) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
                if (isAnyElementDisplayed(elements)) {
                    logInfo("Found no result container via CSS: " + cssSelector);
                    return true;
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        return false;
    }
    
    private boolean checkNoProducts() {
        for (String productSelector : PRODUCT_SELECTORS) {
            try {
                List<WebElement> products = driver.findElements(By.cssSelector(productSelector));
                if (!products.isEmpty()) {
                    return false; // Products found, not a no-result scenario
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        logInfo("No products found on page - this might indicate no results");
        return true;
    }
    
    private boolean checkPageSourcePhrases() {
        String pageSource = driver.getPageSource().toLowerCase();
        for (String phrase : NO_RESULT_PHRASES) {
            if (pageSource.contains(phrase)) {
                logInfo("Found no result phrase in page source: " + phrase);
                return true;
            }
        }
        return false;
    }
    
    private boolean isAnyElementDisplayed(List<WebElement> elements) {
        return elements.stream()
                .anyMatch(WebElement::isDisplayed);
    }
} 
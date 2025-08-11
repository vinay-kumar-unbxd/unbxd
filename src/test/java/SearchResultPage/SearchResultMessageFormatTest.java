package SearchResultPage;

import Base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.ScreenshotUtil;
import utils.TestData;

public class SearchResultMessageFormatTest extends BaseTest {

    @Test
   // @Parameters({"testkey", "vertical"})
    public void tc_070_validateSearchResultMessageFormat() {
        logInfo("🔍 [MESSAGE-FORMAT-VALIDATION] Starting Search Result Message Format Validation Test");
        
        // Setup test data
        TestData configData = new TestData("data.xlsx", "Unique-vintage");
        TestData searchData = TestData.forSearchQueries("Fashion and Apparel");
        
        // Get different types of search queries for comprehensive testing
        String singleWordQuery = searchData.getSingleWordQuery();
        
        logInfo("Test Configuration:");
        logInfo("- Single Word Query: " + singleWordQuery);
        
        // Step 1: Navigate to site
        logInfo("Step 1: Navigating to website");
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Take screenshot of initial page state
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "initial_page_state");
        
        // Step 2: Validate message format for single word query
        if (singleWordQuery != null && !singleWordQuery.trim().isEmpty()) {
            validateMessageFormatForQuery(basePage, singleWordQuery, "Single Word Query");
        }
        
        
        logPass("✅ All search result message format validations completed successfully");
    }

    private void validateMessageFormatForQuery(BasePage basePage, String searchQuery, String queryType) {
        try {
            logInfo("🔍 Validating message format for " + queryType + ": '" + searchQuery + "'");
            
            // Perform search
            performSearch(basePage, searchQuery);
            
            // Get search result message from UI
            String uiMessage = basePage.searchPage.getSearchResultMessage();
            
            if (uiMessage != null && !uiMessage.trim().isEmpty()) {
                logInfo("📝 UI Message: " + uiMessage);
                logPass("✅ Search result message is present for " + queryType + ": " + uiMessage);
                
                // Validate message contains expected elements
                validateMessageElements(uiMessage, searchQuery, queryType);
                
            } else {
                logFail("❌ No search result message found for " + queryType);
                ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "no_message_found_" + queryType.toLowerCase().replace(" ", "_"));
                Assert.fail("No search result message found for " + queryType + ": " + searchQuery);
            }
            
        } catch (Exception e) {
            logFail("❌ Error validating message format for " + queryType + ": " + e.getMessage());
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "error_" + queryType.toLowerCase().replace(" ", "_"));
            Assert.fail("Error validating message format for " + queryType + ": " + e.getMessage());
        }
    }

    private void performSearch(BasePage basePage, String searchQuery) {
        try {
            logInfo("🔍 Performing search for: " + searchQuery);
            
            // Close any popups
            basePage.searchPage.closePopupIfPresent();
            
            // Enter search query
            basePage.searchPage.clickSearchField();
            basePage.searchPage.enterInSearchBox(searchQuery);
            basePage.searchPage.pressEnterInSearchBox();
            
            // Wait for search results to load
            Thread.sleep(3000);
            
            logInfo("Current URL after search: " + driver.getCurrentUrl());
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_results_" + searchQuery.replace(" ", "_"));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Search operation interrupted", e);
        }
    }



    private void validateMessageElements(String message, String searchQuery, String queryType) {
        logInfo("🔍 Validating message elements for " + queryType);
        
        String lowerMessage = message.toLowerCase();
        String lowerQuery = searchQuery.toLowerCase();
        
        // Validate that message contains the search query/title
        if (lowerMessage.contains(lowerQuery)) {
            logPass("✅ Message contains search query/title: " + searchQuery);
        } else {
            logFail("❌ Message does not contain search query/title: " + searchQuery);
        }
        
        // Validate that message contains either "search" or "showing"
        boolean hasSearch = lowerMessage.contains("search");
        boolean hasShowing = lowerMessage.contains("showing");
        
        if (hasSearch || hasShowing) {
            if (hasSearch) {
                logPass("✅ Message contains 'search' keyword");
            }
            if (hasShowing) {
                logPass("✅ Message contains 'showing' keyword");
            }
        } else {
            logFail("❌ Message does not contain either 'search' or 'showing' keyword");
        }
        
        // Validate message length (should be reasonable)
        if (message.length() >= 5 && message.length() <= 200) {
            logPass("✅ Message length is appropriate: " + message.length() + " characters");
        } else {
            logFail("❌ Message length is inappropriate: " + message.length() + " characters");
        }
    }
} 
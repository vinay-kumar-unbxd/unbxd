package SearchResultPage;

import Base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BasePage;
import org.testng.annotations.Parameters;
import utils.ScreenshotUtil;
import utils.TestData;

public class SearchTermInSearchBoxTest extends BaseTest {

    @Test 
    @Parameters({"vertical", "testkey"})
    public void TC_62_validateSearchTermInSearchBox(String vertical, String testkey) throws InterruptedException {
        // Setup test data
        TestData searchData = TestData.forSearchQueries("Electronics");
        TestData configData = new TestData("data.xlsx", "mwave");
        String searchQuery = searchData.getSingleWordQuery();
        
        logInfo("Test Case 62: Validate search term in search box");
        logInfo("Search query: " + searchQuery);
        
        // 1. Navigate to site
        driver.get(configData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Take screenshot of initial state
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "initial_page_state");
        
        // 2. Enter search query in search box
        logInfo("Entering search query: " + searchQuery);
        basePage.searchPage.enterInSearchBox(searchQuery);
        
        // Wait a moment for the input to be processed
        Thread.sleep(2000);
        
        // Take screenshot after entering the query
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "after_search_query_entered");
        
        // 3. Validate that the entered query is displayed in search box
        validateSearchTermInSearchBox(searchQuery, basePage);
        
        logPass("✅ Test Case 62: Search term validation passed successfully");
    }
    
    private void validateSearchTermInSearchBox(String expectedQuery, BasePage basePage) {
        try {
            // Use the common method from SearchPage to get search box value
            String actualValue = basePage.searchPage.getSearchBoxValue();
            
            logInfo("Expected search term: " + expectedQuery);
            logInfo("Actual search term in search box: " + actualValue);
            
            // Validate that the entered query is displayed in search box
            if (basePage.searchPage.validateSearchBoxValue(expectedQuery)) {
                logPass("✅ Search term correctly displayed in search box: " + actualValue);
            } else {
                logFail("❌ Search term not correctly displayed in search box");
                logFail("Expected: " + expectedQuery + ", Actual: " + actualValue);
                ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_term_validation_failure");
                Assert.fail("Search term not correctly displayed in search box. Expected: " + expectedQuery + ", Actual: " + actualValue);
            }
            
        } catch (Exception e) {
            logFail("❌ Error validating search term in search box: " + e.getMessage());
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_term_validation_error");
            Assert.fail("Error validating search term in search box: " + e.getMessage());
        }
    }
    
}
package SearchResultPage;

import Base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.BasePage;
import utils.ActionUtils;
import utils.ScreenshotUtil;
import utils.TestData;

public class EmptySearchFieldEnterClickValidation extends BaseTest {

    @Test
    public void validateEmptySearchFieldEnterAndClick() {
        TestData testData = new TestData("data.xlsx", "truworths");
        BasePage basePage = new BasePage(driver);

        // Navigate to site and store initial URL
        driver.get(testData.getSiteUrl());
        basePage.searchPage.closePopupIfPresent();
        String initialUrl = driver.getCurrentUrl();
        // Validate click doesn't change URL
        basePage.searchPage.clickSearchField();
        ActionUtils.pressEnterWithActions(driver);
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_field_url_validation");
        validateUrlStability(initialUrl, "search field");


        // Validate search icon click doesn't change URL
        driver.get(testData.getSiteUrl());
        basePage.searchPage.clickSearchIcon();
        validateUrlStability(initialUrl, "search icon");
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_field_url_validation");
        logPass("✅ URL validation completed: Both search field and icon clicks maintained URL stability");
    }

    private void validateUrlStability(String expectedUrl, String action) {
        SoftAssert softAssert = new SoftAssert();
        
        try {
            String currentUrl = driver.getCurrentUrl();
            logInfo("Expected URL: " + expectedUrl);
            logInfo("Current URL: " + currentUrl);    
            // Main URL stability validation
            if (expectedUrl.equals(currentUrl)) {
                logPass("✅ URL stable after " + action + " click");
                softAssert.assertTrue(true, "URL stability validation passed for " + action);
            } else {
                logFail("❌ URL changed after " + action + " click: " + expectedUrl + " → " + currentUrl);
                // Capture screenshot on failure
                ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, action + "_validation_failure");
                // Fail the test when URLs don't match
                softAssert.fail("URL mismatch detected for " + action + ". Expected: " + expectedUrl + ", Actual: " + currentUrl);
            }
            
        } catch (Exception e) {
            // Capture screenshot on any unexpected exception
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, action + "_unexpected_error");
            logFail("❌ Unexpected error during " + action + " validation: " + e.getMessage());
            softAssert.fail("Unexpected error during URL validation for " + action + ": " + e.getMessage());
        }
        
        // Assert all soft assertions - this will fail the test if any soft assertion failed
        try {
            softAssert.assertAll();
            logPass("✅ All URL validations passed for " + action);
        } catch (AssertionError e) {
            // Capture screenshot on soft assertion failure
            ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, action + "_soft_assertion_failure");
            logFail("❌ Soft assertion failures detected for " + action + ": " + e.getMessage());
            throw e;
        }
    }
} 
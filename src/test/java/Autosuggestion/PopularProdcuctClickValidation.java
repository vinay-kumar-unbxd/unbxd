package Autosuggestion;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestData;
import pages.BasePage;
import utils.API_Utils;
import utils.ValidationUtils;
import utils.ScreenshotUtil;

public class PopularProdcuctClickValidation extends BaseTest {

    @Test
    public void validatePopularProductClick() throws InterruptedException {
        logInfo("🔍 Starting Popular Product Click Validation");

        // Get test data and API response
        TestData testData = new TestData("data.xlsx", "mwave");
        Response response = API_Utils.getAutosuggestResponse(testData.getAutosuggestApiUrl(), createLogger());

        String popularProductTitle = API_Utils.getPopularProductTitle(response, 0);
        String popularProductUrl = API_Utils.getPopularProductUrl(response, 0);
        String expectedProductPath = popularProductUrl.replace(testData.getSiteUrl(), "").replaceFirst("^/", "");
        
        logInfo("Product: " + popularProductTitle);
        logInfo("Expected URL: " + popularProductUrl);
        
        // Navigate and setup
        driver.get(testData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        basePage.closePopupIfPresent();
        basePage.searchPage.enterInSearchBox(testData.getQuery());

        // Validate and click on product
        ValidationUtils.validateProductTitle(basePage, popularProductTitle, createLogger());
        basePage.clickOnTitle(popularProductTitle);
        logInfo("✅ Clicked on: " + popularProductTitle);

        Thread.sleep(3000);
        
        // Post-click validations
        String currentUrl = driver.getCurrentUrl();
        boolean urlMatches = currentUrl.equals(popularProductUrl) || currentUrl.contains(expectedProductPath);
        
        ScreenshotUtil.takeScreenshotAndAttachToReportWithLog(driver, test, 
            "Product_Page_Result", "📸 Product page after click");
        
        ValidationUtils.validateProductTitle(basePage, popularProductTitle, createLogger());
        
        // Final result
        logInfo("📊 Validation Result:");
        logInfo("   Current URL: " + currentUrl);
        logInfo("   URL Match: " + (urlMatches ? "✅ PASS" : "❌ FAIL"));
        
        if (urlMatches) {
            logPass("✅ Popular product click validation PASSED");
        } else {
            logFail("❌ Popular product click validation FAILED");
        }
    }

    private utils.ValidationUtils.TestLogger createLogger() {
        return new utils.ValidationUtils.TestLogger() {
            @Override
            public void logInfo(String message) {
                PopularProdcuctClickValidation.this.logInfo(message);
            }

            @Override
            public void logPass(String message) {
                PopularProdcuctClickValidation.this.logPass(message);
            }

            @Override
            public void logFail(String message) {
                PopularProdcuctClickValidation.this.logFail(message);
            }
        };
    }
}

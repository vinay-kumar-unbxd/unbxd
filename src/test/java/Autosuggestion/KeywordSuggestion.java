package Autosuggestion;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.TestData;

import java.util.List;

public class KeywordSuggestion extends BaseTest {

    @Test
    @Parameters("testKey")
    public void validateKeywordSuggestion(String testKey) throws InterruptedException {
        TestData testData = new TestData("data.xlsx", testKey);
        String siteUrl = testData.getSiteUrl();
        String query = testData.getQuery();
        String apiUrl = testData.getAutosuggestApiUrl();

        // Get API suggestions
        Response response = API_Utils.getAutosuggestResponse(apiUrl, createLogger());
        List<String> keywordSuggest = API_Utils.getSuggestionsTitlesList(response, "KEYWORD_SUGGESTION", "autosuggest");
        
        // Log suggestions for reporting
        keywordSuggest.forEach(Reporter::log);

        // Validate in UI
        BasePage basePage = new BasePage(driver);
        driver.get(siteUrl);
        Thread.sleep(1000);
        
        basePage.searchPage.enterInSearchBox(query);
        try {
            basePage.verifyTitlesPresentInUI(keywordSuggest);
            logPass("✅ All keyword suggestions validated in UI");
        } catch (Exception e) {
            logFail("❌ Validation failed: " + e.getMessage());
            throw e;
        }
    }

    private utils.ValidationUtils.TestLogger createLogger() {
        return new utils.ValidationUtils.TestLogger() {
            @Override
            public void logInfo(String message) {
                KeywordSuggestion.this.logInfo(message);
            }

            @Override
            public void logPass(String message) {
                KeywordSuggestion.this.logPass(message);
            }

            @Override
            public void logFail(String message) {
                KeywordSuggestion.this.logFail(message);
            }
        };
    }
}

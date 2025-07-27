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

public class TopSearchQueries extends BaseTest {

    @Test
    @Parameters("testKey")
    public void topSearchQueries() throws InterruptedException {
        logInfo("🚀 Starting Top Search Queries Validation");
        
        TestData testData = new TestData("data.xlsx", "mwave");
        
        String siteUrl = testData.getSiteUrl();
        String query = testData.getQuery();
        String apiUrl = testData.getAutosuggestApiUrl();

        logInfo("🔗 API URL: " + apiUrl);
        logInfo("🌐 Site URL: " + siteUrl);
        logInfo("🔍 Search Query: " + query);

        BasePage basePage = new BasePage(driver);

        logInfo("📡 Calling Autosuggest API for Top Search Queries...");
        Response response = API_Utils.getAutosuggestResponse(apiUrl, createLogger());
        logPass("✅ API Response received successfully with status: " + response.getStatusCode());
        
        List<String> keywordSuggest = API_Utils.getSuggestionsTitlesList(response, "TOP_SEARCH_QUERIES", "autosuggest");
        logInfo("🔥 Found " + keywordSuggest.size() + " top search queries from API");

        logInfo("🌐 Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("✅ Successfully navigated to website");
        
        Thread.sleep(1000);
        logInfo("🔍 Entering search query: " + query);
        basePage.searchPage.enterInSearchBox(query);
        logPass("✅ Search query entered successfully");

        logInfo("📝 Top Search Queries from API:");
        for (int i = 0; i < keywordSuggest.size(); i++) {
            String title = keywordSuggest.get(i);
            logInfo("   " + (i+1) + ". " + title);
            Reporter.log(title);
        }

        logInfo("🔍 Validating top search queries in UI...");
        try {
            basePage.verifyTitlesPresentInUI(keywordSuggest);
            logPass("✅ All top search queries successfully validated in UI");
        } catch (Exception e) {
            logFail("❌ Top search queries validation failed: " + e.getMessage());
            throw e;
        }

    }

    private utils.ValidationUtils.TestLogger createLogger() {
        return new utils.ValidationUtils.TestLogger() {
            @Override
            public void logInfo(String message) {
                TopSearchQueries.this.logInfo(message);
            }

            @Override
            public void logPass(String message) {
                TopSearchQueries.this.logPass(message);
            }

            @Override
            public void logFail(String message) {
                TopSearchQueries.this.logFail(message);
            }
        };
    }
}

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
    public void validateKeywordSuggestion (String testKey) throws InterruptedException {
        logInfo("🚀 Starting Keyword Suggestion Validation for: " + testKey);
        
        TestData testData = new TestData("data.xlsx", testKey);
        
        String siteUrl = testData.getSiteUrl();
        String query = testData.getQuery();
        String apiUrl = testData.getAutosuggestApiUrl();

        logInfo("🔗 API URL: " + apiUrl);
        logInfo("🌐 Site URL: " + siteUrl);
        logInfo("🔍 Search Query: " + query);

        BasePage basePage = new BasePage(driver);

        logInfo("📡 Calling Autosuggest API...");
        Response response = API_Utils.getAutosuggestResponse(apiUrl, logger);
        logPass("✅ API Response received successfully with status: " + response.getStatusCode());
        
        List<String> keywordSuggest = API_Utils.getSuggestionsTitlesList(response, "KEYWORD_SUGGESTION", "autosuggest");
        logInfo("📋 Found " + keywordSuggest.size() + " keyword suggestions from API");

        logInfo("🌐 Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("✅ Successfully navigated to website");
        
        Thread.sleep(1000);
        logInfo("🔍 Entering search query: " + query);
        basePage.searchPage.enterInSearchBox(query);
        logPass("✅ Search query entered successfully");

        logInfo("📝 Keyword Suggestion Titles from API:");
        for (int i = 0; i < keywordSuggest.size(); i++) {
            String title = keywordSuggest.get(i);
            logInfo("   " + (i+1) + ". " + title);
            Reporter.log(title);
        }

        logInfo("🔍 Validating keyword suggestions in UI...");
        try {
            basePage.verifyTitlesPresentInUI(keywordSuggest);
            logPass("✅ All keyword suggestions successfully validated in UI");
        } catch (Exception e) {
            logFail("❌ Keyword suggestion validation failed: " + e.getMessage());
            throw e;
        }








    }

}

package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.TestData;
import utils.ScreenshotUtil;
import utils.ValidationUtils;

import java.util.List;
import java.util.ArrayList;

public class ValidationSearchResultWIthDifferentQuery extends BaseTest {

    private List<String> apiProductUrls = new ArrayList<>();

    @Test
    @Parameters({"testkey", "vertical"})
    public void twoWordsQuery(String testkey, String vertical) throws InterruptedException {
        logInfo("🔍 [DIFFERENT-QUERY-VALIDATION] Starting Validation Search Result With Different Query Test");
        
        // Setup test data
        TestData configData = new TestData("data.xlsx", testkey);
        TestData searchData = TestData.forSearchQueries(vertical);
        String searchQuery = searchData.getTwoWordQuery();
        
        logInfo("Search query: " + searchQuery);
        
        // Step 1: Trigger search API and fetch product URLs
        fetchProductUrlsFromAPI(searchQuery, configData);
        
        // TODO: Add remaining test steps here
        logInfo("Step 1 completed: Search API triggered successfully");
        
        logPass("✅ Test completed successfully");
    }
    
    private void fetchProductUrlsFromAPI(String searchQuery, TestData configData) {
        try {
            logInfo("Step 1: Fetching product URLs from API for query: " + searchQuery);
            String baseUrl = configData.getBaseUrl();
            String searchEndpoint = configData.getSearch();
            String searchParams = configData.getSearchEndpoint();
            String searchApiUrl = baseUrl + searchEndpoint + searchQuery + searchParams;
            logInfo("Search API URL: " + searchApiUrl);

            // Call API using API_Utils methods
            Response response = API_Utils.getSearchResultResponse(searchApiUrl);            
            apiProductUrls = API_Utils.getProductUrls(response, 5);
            logPass("✅ Successfully fetched " + apiProductUrls.size() + " product URLs from API");
            
            // Log the product URLs for debugging
            for (int i = 0; i < apiProductUrls.size(); i++) {
                logInfo("Product URL " + (i + 1) + ": " + apiProductUrls.get(i));
            }
            
        } catch (Exception e) {
            logFail("❌ Error fetching product URLs from API: " + e.getMessage());
            apiProductUrls.clear();
        }
    }
}

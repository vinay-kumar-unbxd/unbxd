package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.ScreenshotUtil;
import utils.API_Utils;
import utils.TestData;

public class SearchResultPageValidation extends BaseTest {

    @Test
   @Parameters({"testkey", "vertical"})
    public void totalProductCount(String testkey, String vertical)
    {
        logInfo("[COUNT-VALIDATION] Starting Product Count Validation Test for: " + testkey);
        TestData testData = new TestData("data.xlsx", testkey);
        
        String siteUrl = testData.getSiteUrl();
        String query = testData.getQuery();
        String searchUrl = testData.getValue("searchUrl");
        String siteUrlSearch = siteUrl + searchUrl + query;
        String apiUrl = testData.getSearchApiUrl();

        BasePage basePage = new BasePage(driver);

        Response response = API_Utils.getSearchResultResponse(apiUrl);

          int apiTotalProductCount = API_Utils.getTotalProductCount(response);
          logInfo("[COUNT-VALIDATION] API count: "+apiTotalProductCount);
          driver.get(siteUrlSearch);
          basePage.searchPage.closePopupIfPresent();
          logInfo("[COUNT-VALIDATION] Navigating to the search results page: " + siteUrlSearch);
          ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_results_page");
          int uiTotalProductCount = basePage.searchPage.getProductCountFromUI();

          logInfo("[COUNT-VALIDATION] UI count: " + uiTotalProductCount);
          
          // Comparison and assertion
          if (apiTotalProductCount == uiTotalProductCount) {
              logPass("✅ PRODUCT COUNT MATCH: API (" + apiTotalProductCount + ") = UI (" + uiTotalProductCount + ")");
          } else {
              logFail("❌ PRODUCT COUNT MISMATCH: API (" + apiTotalProductCount + ") ≠ UI (" + uiTotalProductCount + ")");
          }
          
          Assert.assertEquals(uiTotalProductCount, apiTotalProductCount, "❌ Mismatch in UI and API product count! API: " + apiTotalProductCount + ", UI: " + uiTotalProductCount);
    }

}

package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.TestData;

public class SearchResultPageValidation extends BaseTest {

    @Test
    public void totalProductCount()
    {
        TestData testData = new TestData("data.xlsx", "unique-vintage");
        
        String siteUrl = testData.getSiteUrl();
        String query = testData.getQuery();
        String apiUrl = testData.getSearchApiUrl();
        String searchUrl = testData.getValue("searchUrl");
        String siteUrlSearch = siteUrl + searchUrl + query;

        BasePage basePage = new BasePage(driver);

        Response response = API_Utils.getSearchResultResponse(apiUrl);

          int apiTotalProductCount = API_Utils.getTotalProductCount(response);
          System.out.println("API count: "+apiTotalProductCount);
          driver.get(siteUrlSearch);
          int uiTotalProductCount = basePage.searchPage.getProductCountFromUI();

          System.out.println("UI count: "+uiTotalProductCount);
          Assert.assertEquals(uiTotalProductCount, apiTotalProductCount, "❌ Mismatch in UI and API product count!");
    }

}

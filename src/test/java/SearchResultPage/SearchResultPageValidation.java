package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.DataUtils;
import utils.ExcelReader;

public class SearchResultPageValidation extends BaseTest {

    @Test
    public void totalProductCount()
    {
        String [] data = ExcelReader.readRowByKey("data.xlsx", "unique-vintage");
        String siteUrl = data[1];
        String baseUrl = data[2];
        String search = data[6];
        String endPointSRP = data[7];
        String query = data[5];

        String searchUrl = data[8];

        String apiUrl = baseUrl+search+query+endPointSRP;
        String siteUrlSearch = siteUrl+searchUrl+query;

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

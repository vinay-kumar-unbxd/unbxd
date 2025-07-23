package Autosuggestion;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.ExcelReader;

import java.util.List;

public class TopSearchQueries extends BaseTest {

    @Test
    @Parameters("testKey")
    public void topSearchQueries(String testKey) throws InterruptedException {
        String[] data = ExcelReader.readRowByKey("data.xlsx", "mwave");
        String siteUrl = data[1];
        String baseUrl = data[2];
        String apiKey = data[3];
        String siteKey = data[4];
        String type = data[5];
        String endPointAS = data[6];
        String query = data[7];
        String apiUrl = baseUrl + apiKey + siteKey + type + query + endPointAS;

        BasePage basePage = new BasePage(driver);

        Response response = API_Utils.getAutosuggestResponse(apiUrl);
        List<String> keywordSuggest = API_Utils.getSuggestionsTitle(response, "TOP_SEARCH_QUERIES", "autosuggest");

        driver.get(siteUrl);
        //       Thread.sleep(3000);
//        basePage.closePopupIfPresent();
        Thread.sleep(1000);
        basePage.searchPage.enterInSearchBox(query);

        System.out.println("===Keyword suggestion Titles ===");
        for (String title : keywordSuggest) {
            System.out.println(title);
            Reporter.log(title);
        }

        System.out.println("===validated UI titles===");
        basePage.verifyTitlesPresentInUI(keywordSuggest);

    }
}

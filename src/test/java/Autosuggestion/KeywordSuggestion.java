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

public class KeywordSuggestion extends BaseTest {

    @Test
    @Parameters("testKey")
    public void validateKeywordSuggestion (String testKey) throws InterruptedException {
        String[] data = ExcelReader.readRowByKey("data.xlsx", testKey);
        String siteUrl = data[1];
        String baseUrl = data[2];
        String type = data[3];
        String endPointAS = data[4];
        String query = data[5];
        String apiUrl = baseUrl+type+query+endPointAS;

        BasePage basePage = new BasePage(driver);

        Response response = API_Utils.getAutosuggestResponse(apiUrl);
        List<String> keywordSuggest = API_Utils.getSuggestionsTitle(response, "KEYWORD_SUGGESTION", "autosuggest");

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

package Autosuggestion;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.IReporter;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.DataUtils;
import utils.ExcelReader;

import java.util.List;

import static utils.DataUtils.convertNumberListToStringList;

public class PopularSuggestion extends BaseTest {

    @Test
    @Parameters("testKey")
    public void validatePopularProducts( ) throws InterruptedException {

        String[] data = ExcelReader.readRowByKey("data.xlsx", "kpnfresh");

        String siteUrl = data[1];
        String baseUrl = data[2];
        String autosuggest = data[3];
        String endPointAS = data[4];

        String query = data[5];
        String apiUrl = baseUrl+autosuggest+query+endPointAS;
        BasePage basePage= new BasePage(driver);

        Response response = API_Utils.getAutosuggestResponse(apiUrl);
        List<String> popularProducts = API_Utils.getSuggestionsTitle(response, "POPULAR_PRODUCTS", "title");

        List<String> popularProductPrices = API_Utils.getSuggestionsPriceStrings(response, "POPULAR_PRODUCTS", "price");

        //fetch popular product image urls
        List<String> popularProductsImage = API_Utils.getFirstImageUrls(response, "POPULAR_PRODUCTS");


        driver.get(siteUrl);
  //      basePage.closePopupIfPresent();
        Thread.sleep(1000);
        basePage.searchPage.enterInSearchBox(query);

        System.out.println("=== POPULAR_PRODUCTS Titles ===");
        for (String title : popularProducts) {
            System.out.println(title);
            Reporter.log(title);
        }

        System.out.println("===validated UI titles===");
        basePage.verifyTitlesPresentInUI(popularProducts);


        System.out.println("=== POPULAR_PRODUCTS Image url ===");
        for (String title : popularProductsImage) {
            System.out.println(title);}

        System.out.println("===validated image urls===");
        basePage.validateImagesPresentInUI(popularProductsImage);

//        System.out.println("=== Selling Price ===");
//        for (String title : popularProductPrices) {
//            System.out.println(title);}
//
//        System.out.println("===validated UI Price===");
//        basePage.verifyTitlesPresentInUI(popularProductPrices);

    }
}

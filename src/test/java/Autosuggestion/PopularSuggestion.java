package Autosuggestion;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.IReporter;
import org.testng.Reporter;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.ExcelReader;

import java.util.List;

import static utils.DataUtils.convertNumberListToStringList;

public class PopularSuggestion extends BaseTest {

    @Test
    public void validatePopularProductSellingPrice() throws InterruptedException {

        String[] data = ExcelReader.readRowByKey("data.xlsx", "mwave");

        // data[1] = query, data[2] = siteUrl, data[3] = apiUrl
        String siteUrl = data[1];

        String baseUrl = data[2];
        String apiKey = data[3];
        String siteKey = data[4];
        String type = data[5];
        String endPointAS = data[6];

        String query = data[7];
        String apiUrl = baseUrl+apiKey+siteKey+type+query+endPointAS;


        BasePage basePage= new BasePage(driver);


        Response response = API_Utils.getAutosuggestResponse(apiUrl);
        // fetch popular product titles
        List<String> popularProducts = API_Utils.getSuggestionsTitle(response, "POPULAR_PRODUCTS", "title");

       // Response response = API_Utils.getAutosuggestResponse(APIUrl);

        // fetch popular product prices
//        List<Number> numericValues = API_Utils.getSuggestionsNumericValue(response, "POPULAR_PRODUCTS", "price");
//        List<String>  popularProductPrices = convertNumberListToStringList(numericValues);

        List<String> popularProductPrices = API_Utils.getSuggestionsPriceStrings(response, "POPULAR_PRODUCTS", "price");

        //fetch popular product image urls
        List<String> popularProductsImage = API_Utils.getFirstImageUrls(response, "POPULAR_PRODUCTS");


        driver.get(siteUrl);
//        Thread.sleep(3000);
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

        System.out.println("=== Selling Price ===");
        for (String title : popularProductPrices) {
            System.out.println(title);}

        System.out.println("===validated UI Price===");
        basePage.verifyTitlesPresentInUI(popularProductPrices);

    }
}

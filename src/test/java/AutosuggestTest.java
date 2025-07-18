import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import java.util.List;
import static utils.DataUtils.convertNumberListToStringList;

public class AutosuggestTest extends BaseTest {

    @Test
    public void validatePopularProductTitle() throws InterruptedException {
        BasePage basePage= new BasePage(driver);

        String query = "dress";
        String APIUrl = "https://search.unbxd.io/579c6c9e792e43e038e7f40ca11b6103/ss-unbxd-aapac-shoppersstop-dev50901709028198/search?q="+query+"&inFields.count=10&topQueries.count=4&keywordSuggestions.count=8&promotedSuggestion.count=2&popularProducts.count=6";

        Response response = API_Utils.getAutosuggestResponse(APIUrl);
        List<String> popularProducts = API_Utils.getSuggestionsTitle(response, "POPULAR_PRODUCTS", "title");

        System.out.println("=== POPULAR_PRODUCTS Titles ===");
        for (String title : popularProducts) {
            System.out.println(title);}

          driver.get("https://www.shoppersstop.com/");
          basePage.searchPage.enterInSearchBox("dress");
          Thread.sleep(2000);

          System.out.println("===UI titles===");
          basePage.verifyTitlesPresentInUI(popularProducts);
    }

    @Test
    public void validatePopularProductSellingPrice() throws InterruptedException {
        BasePage basePage= new BasePage(driver);

        String query = "dress";
        String APIUrl = "https://search.unbxd.io/579c6c9e792e43e038e7f40ca11b6103/ss-unbxd-aapac-shoppersstop-dev50901709028198/search?q="+query+"&inFields.count=10&topQueries.count=4&keywordSuggestions.count=8&promotedSuggestion.count=2&popularProducts.count=6";

//        Response response = API_Utils.getAutosuggestResponse(APIUrl);
//        List<Number> numericValues = API_Utils.getSuggestionsNumericValue(response, "POPULAR_PRODUCTS", "price");
//        List<String>  popularProductPrices = convertNumberListToStringList(numericValues);
//
//        System.out.println("=== POPULAR_PRODUCTS Selling Price ===");
//        for (String title : popularProductPrices) {
//            System.out.println(title);}
//
//        driver.get("https://www.shoppersstop.com/");
//        basePage.searchPage.enterInSearchBox("dress");
//        Thread.sleep(2000);
//
//        System.out.println("===UI===");
//        basePage.verifyTitlesPresentInUI(popularProductPrices);
    }

    @Test
    public void validatePopularProductImage() throws InterruptedException {
        BasePage basePage= new BasePage(driver);

        String query = "dress";
        String APIUrl = "https://search.unbxd.io/579c6c9e792e43e038e7f40ca11b6103/ss-unbxd-aapac-shoppersstop-dev50901709028198/search?q="+query+"&inFields.count=10&topQueries.count=4&keywordSuggestions.count=8&promotedSuggestion.count=2&popularProducts.count=6";

        Response response = API_Utils.getAutosuggestResponse(APIUrl);
        List<String> popularProductsImage = API_Utils.getFirstImageUrls(response, "POPULAR_PRODUCTS");

        System.out.println("=== POPULAR_PRODUCTS Titles ===");
        for (String title : popularProductsImage) {
            System.out.println(title);}

        driver.get("https://www.shoppersstop.com/");
        basePage.searchPage.enterInSearchBox("dress");
        Thread.sleep(2000);

        System.out.println("===UI titles===");
        basePage.validateImagesPresentInUI(popularProductsImage);
    }
}
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

public class PopularSuggestion extends BaseTest {

    @Test
    @Parameters("testKey")
    public void validatePopularProducts( ) throws InterruptedException {
        logInfo("🚀 Starting Popular Products Validation");

        TestData testData = new TestData("data.xlsx", "footlocker");

        String siteUrl = testData.getSiteUrl();
        String apiUrl = testData.getAutosuggestApiUrl();
        String query = testData.getQuery();
        
        logInfo("🔗 API URL: " + apiUrl);
        logInfo("🌐 Site URL: " + siteUrl);
        logInfo("🔍 Search Query: " + query);
        
        BasePage basePage= new BasePage(driver);

        logInfo("📡 Calling Autosuggest API for Popular Products...");
        Response response = API_Utils.getAutosuggestResponse(apiUrl);
        logPass("✅ API Response received successfully with status: " + response.getStatusCode());
        
        List<String> popularProducts = API_Utils.getSuggestionsTitle(response, "POPULAR_PRODUCTS", "title");
        logInfo("🎯 Found " + popularProducts.size() + " popular products from API");

        //fetch popular product image urls
        List<String> popularProductsImage = API_Utils.getFirstImageUrls(response, "POPULAR_PRODUCTS");
        logInfo("🖼️ Found " + popularProductsImage.size() + " product images from API");

        logInfo("🌐 Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("✅ Successfully navigated to website");
        
        Thread.sleep(1000);
        logInfo("🔍 Entering search query: " + query);
        basePage.searchPage.enterInSearchBox(query);
        logPass("✅ Search query entered successfully");

        logInfo("📝 Popular Product Titles from API:");
        for (int i = 0; i < popularProducts.size(); i++) {
            String title = popularProducts.get(i);
            logInfo("   " + (i+1) + ". " + title);
            Reporter.log(title);
        }

        logInfo("🔍 Validating popular product titles in UI...");
        try {
            basePage.verifyTitlesPresentInUI(popularProducts);
            logPass("✅ All popular product titles successfully validated in UI");
        } catch (Exception e) {
            logFail("❌ Popular product title validation failed: " + e.getMessage());
            throw e;
        }

        logInfo("🖼️ Popular Product Image URLs from API:");
        for (int i = 0; i < popularProductsImage.size(); i++) {
            String imageUrl = popularProductsImage.get(i);
            logInfo("   " + (i+1) + ". " + imageUrl);
        }

        logInfo("🔍 Validating popular product images in UI...");
        try {
            basePage.validateImagesPresentInUI(popularProductsImage);
            logPass("✅ All popular product images successfully validated in UI");
        } catch (Exception e) {
            logFail("❌ Popular product image validation failed: " + e.getMessage());
            throw e;
        }

//        System.out.println("=== Selling Price ===");
//        for (String title : popularProductPrices) {
//            System.out.println(title);}
//
//        System.out.println("===validated UI Price===");
//        basePage.verifyTitlesPresentInUI(popularProductPrices);

    }
}

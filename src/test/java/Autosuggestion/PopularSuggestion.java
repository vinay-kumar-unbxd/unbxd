package Autosuggestion;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.TestData;
import utils.ScreenshotUtil;
import java.util.List;

public class PopularSuggestion extends BaseTest {

    @Test
  //  @Parameters("testKey")
    public void validatePopularProducts( ) throws InterruptedException {
        logInfo("🚀 Starting Popular Products Validation");

        TestData testData = new TestData("data.xlsx", "footlocker");

        String siteUrl = testData.getSiteUrl();
        String apiUrl = testData.getAutosuggestApiUrl();
        String query = testData.getQuery();
        
        logInfo("API URL: " + apiUrl + " | Site URL: " + siteUrl + " | Search Query: " + query);
        BasePage basePage= new BasePage(driver);

        Response response = API_Utils.getAutosuggestResponse(apiUrl, createLogger());
        logPass("✅ API Response received successfully with status: " + response.getStatusCode());
        
        List<String> popularProducts = API_Utils.getSuggestionsTitlesList(response, "POPULAR_PRODUCTS", "title");
        logInfo("Found " + popularProducts.size() + " popular products from API");

        List<String> popularProductsImage = API_Utils.getSuggestionsFirstImageUrlsList(response, "POPULAR_PRODUCTS");
        logInfo("Found " + popularProductsImage.size() + " product images from API");

        logInfo("Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("✅ Successfully navigated to website");
        
        Thread.sleep(1000);
        basePage.searchPage.enterInSearchBox(query);
        logPass("✅ Search query entered successfully");

        logInfo("📝 Popular Product Titles from API:");
        for (int i = 0; i < popularProducts.size(); i++) {
            String title = popularProducts.get(i);
            logInfo("   " + (i+1) + ". " + title);
            Reporter.log(title);
        }
         
        logInfo("🔍 Validating popular product titles in UI...");
        int foundTitles = 0;
        int notFoundTitles = 0;
        
        for (int i = 0; i < popularProducts.size(); i++) {
            String title = popularProducts.get(i);
            try {
                basePage.verifyTitlesPresentInUI(List.of(title));
                logPass("✅ FOUND: " + title);
                foundTitles++;
            } catch (Exception e) {
                logFail("❌ NOT FOUND: " + title);
                notFoundTitles++;
            }
        }

        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "Popular Products List");

        logInfo("📊 Title Validation Summary:");
        logInfo("   Total Titles: " + popularProducts.size() + " | Found: " + foundTitles + " | Not Found: " + notFoundTitles);
        
        if (foundTitles > 0) {
            logPass("✅ Title validation completed - " + foundTitles + " found, " + notFoundTitles + " not found");
        } else {
            logFail("❌ No titles found in UI");
        }

        logInfo("🖼️ Popular Product Image URLs from API:");
        for (int i = 0; i < popularProductsImage.size(); i++) {
            String imageUrl = popularProductsImage.get(i);
            logInfo("   " + (i+1) + ". " + imageUrl);
        }

        logInfo("🔍 Validating popular product images in UI...");
        int foundImages = 0;
        int notFoundImages = 0;
        
        for (int i = 0; i < popularProductsImage.size(); i++) {
            String imageUrl = popularProductsImage.get(i);
            try {
                basePage.validateImagesPresentInUI(List.of(imageUrl));
                logPass("✅ FOUND: " + imageUrl);
                foundImages++;
            } catch (Exception e) {
                logFail("❌ NOT FOUND: " + imageUrl);
                notFoundImages++;
            }
        }
        
        logInfo("📊 Image Validation Summary:");
        logInfo("   Total Images: " + popularProductsImage.size());
        logInfo("   Found: " + foundImages);
        logInfo("   Not Found: " + notFoundImages);
        
        if (foundImages > 0) {
            logPass("✅ Image validation completed - " + foundImages + " found, " + notFoundImages + " not found");
        } else {
            logFail("❌ No images found in UI");
        }

//        System.out.println("=== Selling Price ===");
//        for (String title : popularProductPrices) {
//            System.out.println(title);}
//
//        System.out.println("===validated UI Price===");
//        basePage.verifyTitlesPresentInUI(popularProductPrices);

    }

    private utils.ValidationUtils.TestLogger createLogger() {
        return new utils.ValidationUtils.TestLogger() {
            @Override
            public void logInfo(String message) {
                PopularSuggestion.this.logInfo(message);
            }

            @Override
            public void logPass(String message) {
                PopularSuggestion.this.logPass(message);
            }

            @Override
            public void logFail(String message) {
                PopularSuggestion.this.logFail(message);
            }
        };
    }
}

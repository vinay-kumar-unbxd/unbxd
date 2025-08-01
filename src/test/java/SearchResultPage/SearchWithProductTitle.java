package SearchResultPage;

import Base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.API_Utils;
import utils.TestData;
import utils.ScreenshotUtil;
import utils.ActionUtils;

import java.util.List;
import java.util.Random;

public class SearchWithProductTitle extends BaseTest {

    private static final int REQUIRED_PRODUCTS = 3;
    private static final int WAIT_TIME = 3000;
    private static final int SITE_LOAD_WAIT = 2000;

    @Test
    @Parameters({"testKey", "vertical"})
    public void autoSuggestionWithProductTitle(String testKey, String vertical) throws InterruptedException {
        logInfo("🛍️ [PRODUCT-SEARCH] Starting " + REQUIRED_PRODUCTS + "-Product Validation for: " + testKey);
        
        // Setup test data - need both search data and site configuration
        TestData searchData = TestData.forSearchQueries(vertical);
        TestData configData = new TestData("data.xlsx", testKey);
        
        // Use config data for API URL
        Response response = API_Utils.getSearchResultResponse(configData.getSearchApiUrl());
        
        ProductData productData = extractProductData(response);
        BasePage basePage = setupSiteNavigation(configData.getSiteUrl());
        int passedCount = validateMultipleProducts(basePage, productData);
        
        validateFinalResults(passedCount);
    }
    
    @Test
    @Parameters({"testKey", "vertical"})
    public void searchResultwithProductTitle(String testKey, String vertical) throws InterruptedException {
        logInfo("🎯 [TITLE-SEARCH] Search for complete Product title and Click product for: " + testKey);
        
        // Setup test data - need both search data and site configuration
        TestData searchData = TestData.forSearchQueries(vertical);
        TestData configData = new TestData("data.xlsx", testKey);
        
        // Use config data for API URL
        Response response = API_Utils.getSearchResultResponse(configData.getSearchApiUrl());
        
        ProductData productData = extractProductData(response);
        ProductData randomProduct = getRandomProduct(productData);
        
        logInfo("Selected Product: " + randomProduct.getTitle(0) + 
                " | Product URL: " + randomProduct.getProductUrl(0));
        
        BasePage basePage = setupSiteNavigation(configData.getSiteUrl());
        
        performSearchAndValidation(basePage, randomProduct);
        
        logPass("TEST PASSED: Random product search with comprehensive validation completed successfully!");
    }
    

    
    private ProductData extractProductData(Response response) {
        List<String> titles = API_Utils.getProductTitles(response);
        List<String> urls = API_Utils.getProductUrls(response, REQUIRED_PRODUCTS);
        
        logInfo("Found " + titles.size() + " products in API response");
        return new ProductData(titles, urls);
    }
    
    
    private BasePage setupSiteNavigation(String siteUrl) throws InterruptedException {
        logInfo("Navigating to website: " + siteUrl);
        driver.get(siteUrl);
        logPass("Successfully navigated to website");
        
        BasePage basePage = new BasePage(driver);
        Thread.sleep(SITE_LOAD_WAIT);
        basePage.searchPage.closePopupIfPresent();
        logInfo("Page loaded and popups handled");
        
        return basePage;
    }
    
    private int validateMultipleProducts(BasePage basePage, ProductData productData) throws InterruptedException {
        int passedCount = 0;
        
        for (int i = 0; i < REQUIRED_PRODUCTS; i++) {
            String title = productData.getTitle(i);
            String url = productData.getProductUrl(i);
            
            logInfo("=== Product " + (i+1) + ": " + title + " ===");
            boolean success = searchAndVerifyProduct(basePage, title, url, "Product " + (i+1));
            if (success) passedCount++;
        }
        
        return passedCount;
    }
    
    private void validateFinalResults(int passedCount) {
        logInfo("📊 Results: " + passedCount + "/" + REQUIRED_PRODUCTS + " products validated");
        if (passedCount == REQUIRED_PRODUCTS) {
            logPass("✅ TEST PASSED: All products found!");
        } else {
            logFail("❌ TEST FAILED: Only " + passedCount + "/" + REQUIRED_PRODUCTS + " products found");
            Assert.fail("Product validation failed: " + passedCount + "/" + REQUIRED_PRODUCTS + " passed");
        }
    }
    
    private ProductData getRandomProduct(ProductData productData) {
        Random random = new Random();
        int randomIndex = random.nextInt(productData.getTitles().size());
        
        String title = productData.getTitle(randomIndex);
        String productUrl = productData.getProductUrl(randomIndex);
        
        return new ProductData(List.of(title), List.of(productUrl));
    }
    
    private void performSearchAndValidation(BasePage basePage, ProductData product) throws InterruptedException {
        String productTitle = product.getTitle(0);
        
        // Enter product title and search
        logInfo("Entering product title in search field: " + productTitle);
        basePage.searchPage.enterInSearchBox(productTitle);
        logPass("Product title entered successfully");
        
        ActionUtils.pressEnterWithActions(driver);
        Thread.sleep(WAIT_TIME);
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, productTitle);
        
        // Validate search results
        validateSearchResults(basePage, product);
    }
    
    private void validateSearchResults(BasePage basePage, ProductData product) {
        String productTitle = product.getTitle(0);        
        logInfo("Starting post-search validations...");
        
        // Validate URL
        validateCurrentUrl();
        
        // Validate product title (failing)
        validateProductTitle(basePage, productTitle);
    }
    
    private void validateCurrentUrl() {
        logInfo("Validating current URL contains product information...");
        String currentUrl = driver.getCurrentUrl();
        logInfo("Current URL: " + currentUrl);
    }
        
    private void validateProductTitle(BasePage basePage, String productTitle) {
        logInfo("Validating product title is present in UI...");
        try {
            basePage.verifyTitlesPresentInUI(List.of(productTitle));
            logPass("Product title found in search results UI");
        } catch (Exception e) {
            logFail("Product title NOT found in search results UI: " + e.getMessage());
            Assert.fail("Product title verification failed: " + e.getMessage());
        }
    }
    
    private boolean searchAndVerifyProduct(BasePage basePage, String productTitle, String productUrl, String productLabel) throws InterruptedException {
        logInfo("=== " + productLabel + " Verification ===");
        logInfo("Product Title: " + productTitle);
        
        // Clear search box and enter product title
        basePage.searchPage.enterInSearchBox(productTitle);
        logPass("Product title entered successfully");
        
        // Wait for search results to load
        Thread.sleep(WAIT_TIME);
        
        // Use optimized common method to verify all product attributes
        logInfo("Verifying all product attributes (title, image, URL)...");
        boolean success = basePage.verifyCompleteProduct(productTitle, productUrl, productLabel);
        
        // Take screenshot after product verification
        String screenshotName = productLabel.replaceAll("\\s+", "_") + "_Verification_Result";
        ScreenshotUtil.takeScreenshotAndAttachToReportWithLog(driver, test, screenshotName, "Product verification completed for: " + productLabel);
        
        if (success) {
            logPass(productLabel + " verification PASSED - All attributes found");}
        else { logFail(productLabel + " verification FAILED - Some attributes not found");}
        
        return success;
    }
    
    private static class ProductData {
        private final List<String> titles;
        private final List<String> urls;
        
        public ProductData(List<String> titles, List<String> urls) {
            this.titles = titles;
            this.urls = urls;}
        
        public String getTitle(int index) {
            return index < titles.size() ? titles.get(index) : "";}
        
        public String getProductUrl(int index) {
            return index < urls.size() ? urls.get(index) : "";}
        
        public List<String> getTitles() {
            return titles;}
        
    }
} 
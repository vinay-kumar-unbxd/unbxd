package utils;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import java.util.List;


public class ValidationUtils {
    
    public interface TestLogger {
        void logInfo(String message);
        void logPass(String message);
        void logFail(String message);
    }

    public static class ProductData {
        public String sku;
        public String title;
        public String imageUrl;
        public String price;

        public ProductData(String sku, String title, String imageUrl, String price) {
            this.sku = sku;
            this.title = title;
            this.imageUrl = imageUrl;
            this.price = price;
        }
    }

    // ✅ Image Validation
    public static boolean validateProductImage(BasePage basePage, String imageUrl, TestLogger logger) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            logger.logInfo("No image URL provided, skipping image validation.");
            return true;
        }

        try {
            basePage.validateImagesPresentInUI(List.of(imageUrl));
            logger.logPass("✅ Product image found in search results UI");
            return true;
        } catch (Exception e) {
            logger.logFail("❌ Product image NOT found in search results UI: " + e.getMessage());
            return false;
        }
    }

    // ✅ Title Validation
    public static boolean validateProductTitle(BasePage basePage, String title, TestLogger logger) {
        if (title == null || title.isEmpty()) {
            logger.logInfo("No title provided, skipping title validation.");
            return true;
        }
        try {
            basePage.verifyTitlesPresentInUI(List.of(title));
            logger.logPass("✅ Product title found in search results UI: " + title);
            return true;
        } catch (Exception e) {
            logger.logFail("❌ Product title NOT found in search results UI: " + title);
            return false;
        }
    }

    // ✅ SKU Validation
    public static boolean validateProductSku(WebDriver driver, String sku, TestLogger logger) {
        try {
            String pageSource = driver.getPageSource();
            if (pageSource.contains(sku)) {
                logger.logPass("✅ SKU " + sku + " found in search results page");
                return true;
            } else {
                logger.logFail("❌ SKU " + sku + " NOT found in search results page");
                return false;
            }
        } catch (Exception e) {
            logger.logFail("❌ Error while validating SKU in search results: " + e.getMessage());
            return false;
        }
    }
} 
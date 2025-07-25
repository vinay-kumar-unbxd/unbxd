package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

public class BasePage {

    // Constants for better maintainability
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    private static final int POPUP_SLEEP_DURATION = 1000;
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    public SearchPage searchPage;
    
    /**
     * Simple logging method for BasePage
     */
    protected void logInfo(String message) {
        System.out.println("[BasePage] " + message);
    }
    
    @FindBy(css = "img.product-image, img[src*='cdn'], img[src*='media'], img[alt*='Product'], img")
    public List<WebElement> allImages;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
        searchPage = new SearchPage(driver);
    }


    public void verifyTitlesPresentInUI(List<String> titles) {
        for (String expectedTitle : titles) {
            if (expectedTitle == null || expectedTitle.trim().isEmpty()) {
                continue; // Skip null or empty titles
            }
            
            String normalizedTitle = normalizeText(expectedTitle);
            String xpath = String.format(
                    "//*[contains(translate(normalize-space(.), " +
                            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"%s\")]",
                    normalizedTitle
            );
            
            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            if (elements.isEmpty()) {
                throw new RuntimeException("Title not found in UI: " + expectedTitle);
            } else {
                logInfo("Title found in UI: " + expectedTitle);
            }
        }
    }


    public void validateImagesPresentInUI(List<String> expectedImageUrls) {
        for (String expectedUrl : expectedImageUrls) {
            if (expectedUrl == null || expectedUrl.trim().isEmpty()) continue;

            boolean found = false;
            for (WebElement img : allImages) {
                String actualSrc = img.getAttribute("src");
                if (actualSrc != null && actualSrc.contains(expectedUrl)) {
                    logInfo("✅ Image found: " + expectedUrl);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new RuntimeException("❌ Image not found in UI: " + expectedUrl);
            }
        }
    }


    public boolean verifyProductTitle(String productTitle, String productLabel) {
        if (productTitle == null || productTitle.trim().isEmpty()) {
            logInfo("Warning " + productLabel + " has no title to verify");
            return true; // Consider it success if no title provided
        }
        
        try {
            verifyTitlesPresentInUI(List.of(productTitle));
            logInfo("Passed " + productLabel + " TITLE found in search results");
            return true;
        } catch (Exception e) {
            logInfo("Failed " + productLabel + " TITLE not found in search results: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyProductImage(String imageUrl, String productLabel) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            logInfo("Warning " + productLabel + " has no image URL to verify");
            return true; // Consider it success if no image URL provided
        }
        
        try {
            validateImagesPresentInUI(List.of(imageUrl));
            logInfo("Passed " + productLabel + " IMAGE found in search results");
            return true;
        } catch (Exception e) {
            logInfo("Failed " + productLabel + " IMAGE not found in search results: " + e.getMessage());
            return false;
        }
    }


    public boolean verifyProductUrl(String productUrl, String productLabel) {
        if (productUrl == null || productUrl.trim().isEmpty()) {
            logInfo("Warning " + productLabel + " has no product URL to verify");
            return true; // Consider it success if no product URL provided
        }
        
        try {
            if (searchPage.isProductUrlPresent(productUrl)) {
                logInfo("Passed " + productLabel + " URL found in search results");
                return true;
            } else {
                logInfo("Failed " + productLabel + " URL not found in search results");
                return false;
            }
        } catch (Exception e) {
            logInfo("Failed " + productLabel + " URL verification failed: " + e.getMessage());
            return false;
        }
    }


    public boolean verifyCompleteProduct(String productTitle, String imageUrl, String productUrl, String productLabel) {
        boolean titleFound = verifyProductTitle(productTitle, productLabel);
        boolean imageFound = verifyProductImage(imageUrl, productLabel);
        boolean urlFound = verifyProductUrl(productUrl, productLabel);
        
        boolean success = titleFound && imageFound && urlFound;
        String resultMsg = String.format("%s verification %s | Title=%s | Image=%s | URL=%s",
                productLabel,
                success ? "PASSED" : "FAILED",
                titleFound ? "Passed" : "Failed",
                imageFound ? "Passed" : "Failed",
                urlFound ? "Passed" : "Failed");
        logInfo(resultMsg);
        return success;
    }

    @FindAll({
            @FindBy(xpath = "//button[contains(@class,'close')]"),
            @FindBy(xpath = "//div[contains(@class,'popup')]//button[contains(@class,'close')]"),
            @FindBy(xpath = "(//button[@data-role='closeBtn'])[1]"),
            @FindBy(xpath = "//div[contains(@class, 'overlay')]//button[contains(@class,'close')]"),
            @FindBy(css = "[aria-label*='close'], [aria-label*='Close']"),
            @FindBy(css = ".modal-close, .popup-close, .overlay-close")
    })
    private List<WebElement> popupCloseButtons;

    /**
     * Closes the first visible popup element if any are present
     */
    public void closePopupIfPresent() {
        try {
            for (WebElement button : popupCloseButtons) {
                if (isElementClickable(button)) {
                    button.click();
                    Thread.sleep(POPUP_SLEEP_DURATION);
                    return; // Exit after first successful close
                }
            }
        } catch (Exception e) {
            // Popup handling is non-critical, continue with test execution
            logInfo("⚠️ Popup handling failed, continuing with test: " + e.getMessage());
        }
    }

    public WebElement findFirstVisibleElement(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                if (isElementVisible(element)) {
                    return element;
                }
            }
        }
        throw new NoSuchElementException("No visible/enabled element found from provided locators");
    }

    public List<WebElement> findVisibleElements(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            if (!elements.isEmpty()) {
                List<WebElement> visibleElements = new ArrayList<>();
                for (WebElement element : elements) {
                    if (isElementVisible(element)) {
                        visibleElements.add(element);
                    }
                }
                if (!visibleElements.isEmpty()) {
                    return visibleElements;
                }
            }
        }
        throw new NoSuchElementException("No visible elements found from provided locators");
    }

    public List<String> getDisplayedPrices() {
        String[] priceSelectors = {
            ".product-price", ".price", "[data-price]", ".cost", ".amount"
        };
        
        List<String> prices = new ArrayList<>();
        for (String selector : priceSelectors) {
            List<WebElement> priceElements = driver.findElements(By.cssSelector(selector));
            for (WebElement element : priceElements) {
                if (isElementVisible(element)) {
                    String priceText = element.getText().trim();
                    if (!priceText.isEmpty()) {
                        prices.add(priceText);
                    }
                }
            }
            if (!prices.isEmpty()) {
                break; // Return prices from first successful selector
            }
        }
        return prices;
    }


    private String normalizeText(String text) {
        return text
                .replaceAll("[^\\p{ASCII}]", "") // Remove non-ASCII characters
                .replaceAll("\\s+", " ")         // Normalize whitespace
                .trim()
                .toLowerCase();
    }

    private boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    private boolean isElementClickable(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    public void clickOnTitle(String expectedTitle) {
        if (expectedTitle == null || expectedTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Expected title is null or empty");
        }
    
        String normalizedTitle = expectedTitle.trim().toLowerCase();
    
        String xpath = String.format(
            "//*[contains(translate(normalize-space(.), " +
            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"%s\")]",
            normalizedTitle
        );
    
        List<WebElement> elements = driver.findElements(By.xpath(xpath));
    
        for (int i = elements.size() - 1; i >= 0; i--) {
            WebElement element = elements.get(i);
            if (element.isDisplayed()) {
                logInfo("Clicking on last matching title: " + expectedTitle);
                element.click();
                return;
            }
        }
    
        throw new RuntimeException("Title not found or not clickable: " + expectedTitle);
    }
    
    
}

package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BasePage {
    // Constants
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    private static final int POPUP_SLEEP_DURATION = 1000;
    private static final String XPATH_TEMPLATE = "//*[contains(translate(normalize-space(.), " +
            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"%s\")]";
    private static final String[] PRICE_SELECTORS = {
        ".product-price", ".price", "[data-price]", ".cost", ".amount"
    };
    
    // Core components
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    public final SearchPage searchPage;
    public final paginationPage paginationPage;
    
    // Page elements
    @FindBy(css = "img.product-image, img[src*='cdn'], img[src*='media'], img[alt*='Product'], img")
    public List<WebElement> allImages;

    @FindAll({
        @FindBy(xpath = "//button[contains(@class,'close')]"),
        @FindBy(xpath = "//div[contains(@class,'popup')]//button[contains(@class,'close')]"),
        @FindBy(xpath = "(//button[@data-role='closeBtn'])[1]"),
        @FindBy(xpath = "//div[contains(@class, 'overlay')]//button[contains(@class,'close')]"),
        @FindBy(css = "[aria-label*='close'], [aria-label*='Close']"),
        @FindBy(css = ".modal-close, .popup-close, .overlay-close")
    })
    private List<WebElement> popupCloseButtons;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
        searchPage = new SearchPage(driver);
        paginationPage = new paginationPage(driver);
    }

    // ================= LOGGING =================
    protected void logInfo(String message) {
        System.out.println("[BasePage] " + message);
    }

    // ================= VALIDATION METHODS =================
    public void verifyTitlesPresentInUI(List<String> titles) {
        titles.stream()
            .filter(title -> title != null && !title.trim().isEmpty())
            .forEach(this::verifyTitleInUI);
    }

    private void verifyTitleInUI(String expectedTitle) {
        String normalizedTitle = normalizeText(expectedTitle);
        String xpath = String.format(XPATH_TEMPLATE, normalizedTitle);
        
        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        if (elements.isEmpty()) {
            throw new RuntimeException("Title not found in UI: " + expectedTitle);
        }
        logInfo("Title found in UI: " + expectedTitle);
    }



    public void validateImagesPresentInUI(List<String> expectedImageUrls) {
        if (expectedImageUrls == null || expectedImageUrls.isEmpty()) {
            logInfo("⚠️ Expected image URL list is empty or null.");
            return;
        }
    
        // Step 1: Extract UI image sources, strip query params
        List<String> uiImages = new ArrayList<>();
        for (WebElement img : allImages) {
            String src = img.getAttribute("src");
            if (src != null && !src.trim().isEmpty()) {
                String baseUrl = src.contains("?") ? src.substring(0, src.indexOf("?")) : src;
                uiImages.add(baseUrl);
            }
        }
    
        // Step 2: Try exact match first
        for (String expectedUrl : expectedImageUrls) {
            if (expectedUrl == null || expectedUrl.trim().isEmpty()) continue;
    
            String cleanExpectedUrl = expectedUrl.contains("?")
                    ? expectedUrl.substring(0, expectedUrl.indexOf("?"))
                    : expectedUrl;
    
            // Try exact match
            for (String uiUrl : uiImages) {
                if (uiUrl.equalsIgnoreCase(cleanExpectedUrl)) {
                    logInfo("✅ Exact image match found in UI: " + cleanExpectedUrl);
                    return;
                }
            }
    
            // Step 3: Fallback — Match by removing numeric suffix (_1, _2, _3)
            String expectedBase = cleanExpectedUrl.replaceAll("_[0-9]+(?=\\.[a-zA-Z]{3,4}$)", "_");
    
            for (String uiUrl : uiImages) {
                String uiBase = uiUrl.replaceAll("_[0-9]+(?=\\.[a-zA-Z]{3,4}$)", "_");
    
                if (uiBase.equalsIgnoreCase(expectedBase)) {
                    logInfo("🔁 Base pattern image match found in UI: " + uiUrl);
                    return;
                }
            }
        }
    
        // Step 4: If no match found at all
        logInfo("❌ None of the expected images matched in the UI.");
        throw new AssertionError("No matching image found in UI.");
    }
    
    

    // ================= PRODUCT VERIFICATION =================
    public boolean verifyProductTitle(String productTitle, String productLabel) {
        return verifyProductAttribute(productTitle, productLabel, "TITLE", 
            () -> verifyTitlesPresentInUI(List.of(productTitle)));
    }

    public boolean verifyProductImage(String imageUrl, String productLabel) {
        return verifyProductAttribute(imageUrl, productLabel, "IMAGE", 
            () -> validateImagesPresentInUI(List.of(imageUrl)));
    }

    public boolean verifyProductUrl(String productUrl, String productLabel) {
        return verifyProductAttribute(productUrl, productLabel, "URL", 
            () -> {
                if (!searchPage.isProductUrlPresent(productUrl)) {
                    throw new RuntimeException("URL not found in search results");
                }
            });
    }

    private boolean verifyProductAttribute(String value, String productLabel, String attributeType, Runnable verificationAction) {
        if (value == null || value.trim().isEmpty()) {
            logInfo("Warning " + productLabel + " has no " + attributeType.toLowerCase() + " to verify");
            return true;
        }
        
        try {
            verificationAction.run();
            logInfo("Passed " + productLabel + " " + attributeType + " found in search results");
            return true;
        } catch (Exception e) {
            logInfo("Failed " + productLabel + " " + attributeType + " not found in search results: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyCompleteProduct(String productTitle, String productUrl, String productLabel) {
        boolean titleFound = verifyProductTitle(productTitle, productLabel);
        boolean urlFound = verifyProductUrl(productUrl, productLabel);
        
        boolean success = titleFound && urlFound;
        String resultMsg = String.format("%s verification %s | Title=%s | URL=%s",
                productLabel,
                success ? "PASSED" : "FAILED",
                titleFound ? "Passed" : "Failed");
        logInfo(resultMsg);
        return success;
    }

    // ================= UI INTERACTION =================
    public void closePopupIfPresent() {
        try {
            popupCloseButtons.stream()
                .filter(this::isElementClickable)
                .findFirst()
                .ifPresent(button -> {
                    try {
                        button.click();
                        Thread.sleep(POPUP_SLEEP_DURATION);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
        } catch (Exception e) {
            logInfo("⚠️ Popup handling failed, continuing with test: " + e.getMessage());
        }
    }

    public void clickOnTitle(String expectedTitle) {
        if (expectedTitle == null || expectedTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Expected title is null or empty");
        }

        String normalizedTitle = normalizeText(expectedTitle);
        String xpath = String.format(XPATH_TEMPLATE, normalizedTitle);
        
        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        
        // Find last displayed element and click using JavaScript
        elements.stream()
            .filter(WebElement::isDisplayed)
            .reduce((first, second) -> second) // Get last element
            .ifPresentOrElse(
                element -> {
                    logInfo("Clicking on last matching title: " + expectedTitle);
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].scrollIntoView(true);", element);
                    js.executeScript("arguments[0].click();", element);
                },
                () -> {
                    throw new RuntimeException("Title not found or not clickable: " + expectedTitle);
                }
            );
    }

    // ================= ELEMENT FINDER METHODS =================
    public WebElement findFirstVisibleElement(By... locators) {
        for (By locator : locators) {
            WebElement element = driver.findElements(locator).stream()
                .filter(this::isElementVisible)
                .findFirst()
                .orElse(null);
            if (element != null) return element;
        }
        throw new NoSuchElementException("No visible/enabled element found from provided locators");
    }

    public List<WebElement> findVisibleElements(By... locators) {
        for (By locator : locators) {
            List<WebElement> visibleElements = driver.findElements(locator).stream()
                .filter(this::isElementVisible)
                .collect(Collectors.toList());
            if (!visibleElements.isEmpty()) return visibleElements;
        }
        throw new NoSuchElementException("No visible elements found from provided locators");
    }

    public List<String> getDisplayedPrices() {
        for (String selector : PRICE_SELECTORS) {
            List<String> prices = driver.findElements(By.cssSelector(selector)).stream()
                .filter(this::isElementVisible)
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
            if (!prices.isEmpty()) return prices;
        }
        return new ArrayList<>();
    }

    // ================= UTILITY METHODS =================
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
}

package pages;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchPage{
    WebDriver driver;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this); // Init @FindBy elements
    }

    @FindAll({
        @FindBy(css = "input[type='search'], input[name='w'], input[name='q'], input[placeholder*='Search']"),
        @FindBy(xpath = "//input[@id='sli_search_1'] | //input[@name='w'] | //input[@type='search'] | //input[@name='q'] | //input[contains(@placeholder, 'Search')] | //input[contains(@placeholder, 'Keyword')]")
    })
    private WebElement searchBox;

    @FindAll({
            @FindBy(css = "button[data-click='close']"),
            @FindBy(xpath = "//button[contains(@class,'close')]"),
            @FindBy(xpath = "//div[contains(@class,'popup')]//button[contains(@class,'close')]"),
            @FindBy(xpath = "//div[contains(@class,'modal')]//button[contains(text(),'×')]"),
            @FindBy(xpath = "//div[contains(@class,'newsletter')]//button"),
            @FindBy(xpath = "(//button[@data-role='closeBtn'])[1]"),
            @FindBy(xpath = "//button[@aria-label='Close']"),
            @FindBy(xpath = "//div[contains(@class,'popup-close') or contains(@class,'close-popup')]"),
            @FindBy(xpath = "//div[contains(@class, 'overlay')]//button[contains(@class,'close')]")
    })
    private WebElement closeIcon;


    @FindAll({
        @FindBy(css = "span[id*='ProductCount'], span:contains('items'), *:contains('Showing'), .product-count"),
        @FindBy(xpath = "//span[contains(@id,'ProductCount')]"),
        @FindBy(xpath = "//span[contains(text(),'items')]"),
        @FindBy(xpath = "//*[contains(text(),'Showing')]"),
    })
    private List<WebElement> productCountElements;

    @FindAll({
        @FindBy(css = ".product-item a, .product-card a, .product a"),
        @FindBy(css = "a[href*='/product'], a[href*='/products'], a[href*='catalog/product']"),
        @FindBy(css = "#insider-worker"),
        @FindBy(xpath = "//div[contains(@class,'product')]//a"),
        @FindBy(xpath = "//article[contains(@class,'product')]//a"),
        @FindBy(xpath = "//li[contains(@class,'product')]//a"),
        @FindBy(xpath = "//a[contains(@href,'product')]")
    })
    private List<WebElement> productLinks;

    public void enterInSearchBox(String data)
    {   
        // Try to close any overlays/popups first
        closePopupIfPresent();
        
        // Wait a moment for popups to disappear
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        // Use JavaScript click if regular click fails
        try {
            searchBox.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBox);
        }
        
        searchBox.clear();
        searchBox.sendKeys(data); 
    }




    public void closePopupIfPresent() {
        try {
            // Try to close various types of overlays and popups
            String[] popupSelectors = {
                "button.close, .popup .close, .modal .close, [aria-label='Close'], .popup-close, .close-popup",
                "[data-testid='close'], [data-cy='close'], .overlay-close",
                ".bounce-element button, .bounce-element .close",
                "[class*='popup'] button, [class*='modal'] button, [class*='overlay'] button",
                "img[src*='bounce'], div[id*='bounce']"  // Try to handle bounce exchange overlays
            };
            
            for (String selector : popupSelectors) {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                for (WebElement element : elements) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        try {
                            element.click();
                            Thread.sleep(500);  // Wait for overlay to disappear
                            return;
                        } catch (Exception clickEx) {
                            // Try JS click
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                            Thread.sleep(500);
                            return;
                        }
                    }
                }
            }
            
            // If no close button found, try to click outside the overlay
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("document.body.click();");
            
        } catch (Exception e) {
            // Popup handling failed, continue with test
        }
    }

    public int getProductCountFromUI() {
        for (WebElement element : productCountElements) {
            try {
                String text = element.getText(); // Example: "Showing 1-20 of 1743 products"
                if (text != null && !text.trim().isEmpty()) {
                    Matcher matcher = Pattern.compile("\\d{1,6}").matcher(text);
                    int lastNumber = -1;
                    while (matcher.find()) {
                        lastNumber = Integer.parseInt(matcher.group());
                    }
                    if (lastNumber != -1) return lastNumber;
                }
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("❌ Could not extract product count from any matching UI element.");
    }









//    public boolean isSearchBoxPresent() {
//        return isDisplayed(searchBox);
//    }
//
//    // Perform a search
//    public void search(String query) {
//        if (isSearchBoxPresent()) {
//            sendKeys(searchBox, query);
//         // searchBox.sendKeys(Keys.ENTER); // or use submit()
//        } else {
//            throw new RuntimeException("Search box not found on UI");
//        }
//    }


    private List<WebElement> searchInputFields;

    public WebElement getSearchInputField() {
        for (WebElement element : searchInputFields) {
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
        }
        throw new NoSuchElementException("Search input field not found.");
    }

    // --- Autosuggest Items ---
    @FindAll({
            @FindBy(xpath = "//*[contains(@class, 'autosuggest')]//li"),
            @FindBy(xpath = "//*[contains(@class, 'suggestion')]//li"),
            @FindBy(xpath = "//*[contains(@class, 'unbxd')]//li"),
            @FindBy(xpath = "//*[contains(@class, 'ui-autocomplete')]//li"),
            @FindBy(xpath = "//div[contains(@class, 'unbxd-as-popular-product-name')]//span[@class='product-search-title']")
    })
    private List<WebElement> autosuggestElements;

    public List<WebElement> getAutosuggestElements() {
        if (autosuggestElements == null || autosuggestElements.isEmpty()) {
            throw new NoSuchElementException("Autosuggest list not found.");
        }
        return autosuggestElements;
    }

    /**
     * Get all product links from search results
     * @return List of product link WebElements
     */
    public List<WebElement> getProductLinks() {
        return productLinks;
    }

    /**
     * Check if a specific product URL or slug is present in search results
     * @param productUrl Full product URL or slug to search for
     * @return true if product URL/slug is found, false otherwise
     */
    public boolean isProductUrlPresent(String productUrl) {
        if (productUrl == null || productUrl.isEmpty()) {
            return false;
        }

        // Extract slug from full URL for flexible matching
        String productSlug = extractProductSlug(productUrl);

        for (WebElement link : productLinks) {
            String href = link.getAttribute("href");
            if (href != null && (href.contains(productUrl) || href.contains(productSlug))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get count of product links in search results
     * @return Number of product links found
     */
    public int getProductLinksCount() {
        return productLinks.size();
    }

    /**
     * Helper method to extract product slug from full URL
     * @param fullUrl Full product URL
     * @return Product slug or path portion
     */
    private String extractProductSlug(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) return "";
        
        // Extract the last part of the URL path (product slug)
        String[] urlParts = fullUrl.split("/");
        if (urlParts.length > 0) {
            return urlParts[urlParts.length - 1];
        }
        return fullUrl;
    }
}

package pages;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchPage {
    private final WebDriver driver;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d{1,6}");

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindAll({
        @FindBy(css = "input[type='search']"),
        @FindBy(css = "input[name='w']"),
        @FindBy(css = "input[name='q']"),
        @FindBy(css = "input[placeholder*='Search']"),
        @FindBy(css = "input[placeholder*='looking for']"),
        @FindBy(css = "input[placeholder*='SEARCH']"),
        @FindBy(xpath = "//input[@id='sli_search_1'] | //input[@type='search']")
    })
    private WebElement searchBox;

    @FindAll({
        @FindBy(css = ".search-button, .search-icon, button.search, [class*='search-submit']"),
        @FindBy(xpath = "//button[@*='search-button'] | //button[contains(@class,'search')] | //button[@type='submit'] | //*[contains(@class,'search-icon')] | //a[contains(@href,'SearchFormSubmit')]")
    })
    private WebElement searchIcon;

    @FindAll({
        @FindBy(css = "span[id*='ProductCount'], .product-count, .product-result-count"),
        @FindBy(xpath = "//span[contains(text(),'products') or contains(text(),'Results') or contains(text(),'items') or contains(text(),'Showing')]")
    })
    private List<WebElement> productCountElements;

    @FindAll({
        @FindBy(css = ".product-item a, .product-card a, .product a, a[href*='/product'], a[href*='/products']"),
        @FindBy(xpath = "//div[contains(@class,'product')]//a | //li[contains(@class,'product')]//a")
    })
    private List<WebElement> productLinks;

    @FindAll({
        @FindBy(css = ".pagination, .pager, nav[aria-label*='pagination'], [class*='pagination'], ul:has(li.cursor-pointer)"),
        @FindBy(xpath = "//div[contains(@class, 'pagination')] | //div[contains(@class, 'Pagination')]//a | //nav[contains(@class, 'pagination')] | //*[contains(@class, 'UNX-pagination')] | //ul[.//li[contains(@class, 'cursor-pointer')]]")
    })
    private WebElement paginationContainer;

    @FindAll({
        @FindBy(css = ".pagination a, .pager a, .page-item a, [data-page-action='paginate'], .UNX-page-button, li.cursor-pointer a, .whitespace-nowrap"),
        @FindBy(xpath = "//div[contains(@class, 'pagination')]//a | //div[contains(@class, 'Pagination')]//a | //nav[contains(@class, 'pagination')]//a | //*[@data-page-action='paginate'] | //li[contains(@class, 'cursor-pointer')]//a | //a[contains(@class, 'whitespace-nowrap')]")
    })
    private List<WebElement> paginationLinks;

    

    public void enterInSearchBox(String data) {
        try { 
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        clickElement(searchBox);
        searchBox.clear();
        searchBox.sendKeys(data);
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public void closePopupIfPresent() {
        String[] selectors = {
            "button.close, .popup .close, .modal .close, [aria-label='Close'], .popup-close",
            "[data-testid='close'], [data-cy='close'], .overlay-close",
            "[class*='popup'] button, [class*='modal'] button, [class*='overlay'] button"
        };
        
        try {
            for (String selector : selectors) {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                for (WebElement element : elements) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        clickElement(element);
                        Thread.sleep(500);
                        return;
                    }
                }
            }
            ((JavascriptExecutor) driver).executeScript("document.body.click();");
        } catch (Exception e) {
            // Continue if popup handling fails
        }
    }

    public int getProductCountFromUI() {
        for (WebElement element : productCountElements) {
            try {
                String text = element.getText();
                if (text != null && !text.trim().isEmpty()) {
                    Matcher matcher = NUMBER_PATTERN.matcher(text);
                    int lastNumber = -1;
                    while (matcher.find()) {
                        lastNumber = Integer.parseInt(matcher.group());
                    }
                    if (lastNumber != -1) return lastNumber;
                }
            } catch (Exception ignored) {
                // Continue to next element
            }
        }
        throw new RuntimeException("❌ Could not extract product count from any matching UI element.");
    }

    public boolean isProductUrlPresent(String productUrl) {
        if (productUrl == null || productUrl.isEmpty()) return false;

        String productSlug = extractProductSlug(productUrl);
        return productLinks.stream()
                .map(link -> link.getAttribute("href"))
                .filter(href -> href != null)
                .anyMatch(href -> href.contains(productUrl) || href.contains(productSlug));
    }

    private String extractProductSlug(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) return "";
        
        String[] urlParts = fullUrl.split("/");
        return urlParts.length > 0 ? urlParts[urlParts.length - 1] : fullUrl;
    }

    public void clickSearchField() {
        try {
            clickElement(searchBox);
        } catch (Exception e) {
            throw new RuntimeException("❌ Could not click search field: " + e.getMessage());
        }
    }

    public void clickSearchIcon() {
        try {
            clickElement(searchIcon);
        } catch (Exception e) {
            throw new RuntimeException("❌ Could not click search icon: " + e.getMessage());
        }
    }

    public boolean isPaginationPresent() {
        try {
            scrollPaginationIntoView();
            return paginationContainer.isDisplayed() || !paginationLinks.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void scrollPaginationIntoView() {
        try {
            if (paginationContainer != null) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", paginationContainer);
                Thread.sleep(1000); // Wait for scroll to complete
            } else if (!paginationLinks.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", paginationLinks.get(0));
                Thread.sleep(1000); // Wait for scroll to complete
            }
        } catch (Exception e) {
            // Continue if scroll fails
        }
    }

    public void pressEnterInSearchBox() {
        try {
            searchBox.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[0].value + '\\n';", searchBox);
        }
    }
    
    // Common method to get search box attribute value
    public String getSearchBoxAttribute(String attributeName) {
        try {
            return searchBox.getAttribute(attributeName);
        } catch (Exception e) {
            // logInfo("Error getting search box attribute '" + attributeName + "': " + e.getMessage()); // Assuming logInfo is defined elsewhere
            return null;
        }
    }
    
    // Common method to get search box value
    public String getSearchBoxValue() {
        return getSearchBoxAttribute("value");
    }
    
    // Common method to get search box placeholder
    public String getSearchBoxPlaceholder() {
        return getSearchBoxAttribute("placeholder");
    }
    
    // Common method to get search box type
    public String getSearchBoxType() {
        return getSearchBoxAttribute("type");
    }
    
    // Common method to validate search box value
    public boolean validateSearchBoxValue(String expectedValue) {
        String actualValue = getSearchBoxValue();
        return actualValue != null && actualValue.equals(expectedValue);
    }
    
    // Common method to get search box text with logging
    public String getSearchBoxValueWithLog(String testName) {
        String value = getSearchBoxValue();
        // logInfo("[" + testName + "] Search box value: " + value); // Assuming logInfo is defined elsewhere
        return value;
    }
    
    // Method to get search result message from UI
    public String getSearchResultMessage() {
        // CSS selectors for search result message elements
        String[] messageSelectors = {
            ".search-results-count",
            ".product-count",
            ".results-count",
            ".search-summary",
            ".search-results-summary",
            "[data-testid='search-results-count']",
            "[data-cy='search-results-count']",
            ".search-results-message",
            ".results-message",
            ".search-summary-text",
            ".product-results-count",
            ".search-results-info",
            ".results-info",
            ".search-results-header",
            ".search-results-title",
            ".search-results-description",
            ".search-results-text"
        };
        
        // Try CSS selectors first
        for (String selector : messageSelectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            return text.trim();
                        }
                    }
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        
        // XPath selectors for search result message elements
        String[] messageXPathSelectors = {
            "//*[contains(text(), 'results') and contains(text(), 'found')]",
            "//*[contains(text(), 'products') and contains(text(), 'found')]",
            "//*[contains(text(), 'items') and contains(text(), 'found')]",
            "//*[contains(text(), 'Showing') and contains(text(), 'results')]",
            "//*[contains(text(), 'Showing') and contains(text(), 'products')]",
            "//*[contains(text(), 'Showing') and contains(text(), 'items')]",
            "//*[contains(text(), 'of') and contains(text(), 'results')]",
            "//*[contains(text(), 'of') and contains(text(), 'products')]",
            "//*[contains(text(), 'of') and contains(text(), 'items')]",
            "//*[contains(text(), 'search'result)]",
            "//*[contains(text(), 'Search results')]",
            "//*[contains(text(), 'results for')]"
        };
        
        // Try XPath selectors
        for (String xpath : messageXPathSelectors) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            return text.trim();
                        }
                    }
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        
        // Try to find any element containing result-related text
        String[] resultKeywords = {"results", "products", "items", "found", "showing"};
        for (String keyword : resultKeywords) {
            try {
                String xpath = "//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + keyword + "')]";
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty() && containsResultInfo(text)) {
                            return text.trim();
                        }
                    }
                }
            } catch (Exception e) {
                // Continue to next keyword
            }
        }
        
        return null;
    }
    
    private boolean containsResultInfo(String text) {
        String lowerText = text.toLowerCase();
        return lowerText.contains("result") || lowerText.contains("product") || 
               lowerText.contains("item") || lowerText.contains("found") || 
               lowerText.contains("showing") || lowerText.contains("of");
    }
}

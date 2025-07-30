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
        @FindBy(css = "input[type='search'], input[name='w'], input[name='q'], input[placeholder*='Search'], input[placeholder*='looking for']"),
        @FindBy(xpath = "//input[@id='sli_search_1'] | //input[@type='search']")
    })
    private WebElement searchBox;

    @FindAll({
        @FindBy(css = "button[type='submit'], .search-button, .search-icon, button.search, [class*='search-submit']"),
        @FindBy(xpath = "//button[contains(@class,'search')] | //button[@type='submit'] | //*[contains(@class,'search-icon')] | //a[contains(@href,'SearchFormSubmit')]")
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
        searchBox.sendKeys(Keys.ENTER);
        try {
            Thread.sleep(2000); // Wait for search results
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

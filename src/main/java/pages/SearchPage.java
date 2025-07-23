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
            @FindBy(xpath = "//input[@type='search']"),
            @FindBy(xpath = "//input[@name='q']"),
            @FindBy(xpath = "//input[contains(@placeholder, 'Search')]"),
            @FindBy(css = "form[action*='/search'] input[type='search']"),
            @FindBy(xpath = "//form[contains(@action, '/search')]//input"),
            @FindBy(xpath = "//input[@type='text']"),
    })
    private WebElement searchBox;

    @FindAll({
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

    public void enterInSearchBox(String data)
    {   searchBox.click();
        searchBox.sendKeys(data); }

    @FindAll({
            @FindBy(xpath = "//span[contains(@id,'ProductCount')]"),
            @FindBy(xpath = "//div[contains(text(),'products')]"),
            @FindBy(xpath = "//span[contains(text(),'items')]"),
            @FindBy(xpath = "//*[contains(text(),'Showing')]"),
            @FindBy(css = ".product-count")
    })
    private List<WebElement> productCountElements;




    public void closePopupIfPresent() {
            try {
                if (closeIcon.isDisplayed()) {
                    closeIcon.click();
                    System.out.println("✅ Popup closed via @FindAll");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Failed to click popup close button: " + e.getMessage());
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
}

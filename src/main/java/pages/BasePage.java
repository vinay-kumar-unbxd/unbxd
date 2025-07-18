package pages;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;



public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public SearchPage searchPage;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);  // Initializes @FindBy fields
        searchPage = new SearchPage(driver);
    }

    /**
     * Generic method to verify presence of expected titles in UI (case-insensitive, normalized).
     *
     * @param titles List of expected product or suggestion titles to validate in the DOM
     */
//    public void verifyTitlesPresentInUI(List<String> titles) {
//        for (String expectedTitle : titles) {
//            String normalizedTitle = expectedTitle.toLowerCase();
//
//            String xpath = String.format(
//                    "//*[contains(translate(normalize-space(text()), " +
//                            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"%s\")]",
//                    normalizedTitle
//            );
//            List<WebElement> elements = driver.findElements(By.xpath(xpath));
//
//            if (!elements.isEmpty()) {
//                System.out.println("✅ Title found in UI: " + expectedTitle);
//            } else {
//                System.out.println("❌ Title NOT found in UI: " + expectedTitle);
//            }
//        }
//    }

    public void verifyTitlesPresentInUI(List<String> titles) {
        for (String expectedTitle : titles) {
            String normalizedTitle = expectedTitle
                    .replaceAll("[^\\p{ASCII}]", "") // remove fancy symbols
                    .replaceAll("\\s+", " ")         // normalize whitespace
                    .trim()
                    .toLowerCase();

            String xpath = String.format(
                    "//*[contains(translate(normalize-space(.), " +
                            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"%s\")]",
                    normalizedTitle
            );

            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            if (!elements.isEmpty()) {
                System.out.println("✅ Title found in UI: " + expectedTitle);
            } else {
                System.out.println("❌ Title NOT found in UI: " + expectedTitle);
            }
        }
    }




    /**
     * Generic method to verify presence of expected imageurl in UI
     */
    public void validateImagesPresentInUI(List<String> expectedImageUrls) {
        for (String imageUrl : expectedImageUrls) {
            String xpath = String.format("//img[contains(@src, \"%s\")]", imageUrl);

            List<WebElement> imageElements = driver.findElements(By.xpath(xpath));

            if (!imageElements.isEmpty()) {
                System.out.println("✅ Image found in UI: " + imageUrl);
            } else {
                System.out.println("❌ Image NOT found in UI: " + imageUrl);
            }
        }
    }

    @FindAll({
            @FindBy(xpath = "//button[contains(@class,'close')]"),
            @FindBy(xpath = "//div[contains(@class,'popup')]//button[contains(@class,'close')]"),
            @FindBy(xpath = "(//button[@data-role='closeBtn'])[1]"),
            @FindBy(xpath = "//div[contains(@class, 'overlay')]//button[contains(@class,'close')]")
    })
    private List<WebElement> popupCloseButtons;

    /**
     * Closes the first visible popup element if any are present.
     */
    public void closePopupIfPresent() {
        for (WebElement button : popupCloseButtons) {
            try {
                if (button.isDisplayed()) {
                    button.click();
                    System.out.println("✅ Popup closed via @FindAll");
                    Thread.sleep(1000); // Let overlay disappear
                    break;
                }
            } catch (Exception e) {
                // It's normal for many buttons to not be available — so just log and move on
                System.out.println("⚠️ Popup not interactable or not found: " + e.getMessage());
            }
        }
    }

//    public void closePopupIfPresent() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
//        for (WebElement button : popupCloseButtons) {
//            try { if (button.isDisplayed()) {
//                    // Wait briefly if needed for clickability
//                    wait.until(ExpectedConditions.elementToBeClickable(button));
//                    // Use JS to avoid intercepts
//                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
//                    System.out.println("✅ Popup closed via JS click");
//                    return;
//                }
//            } catch (Exception e) {
//                // Continue checking next close button if this one fails
//                System.out.println("⚠️ Could not click popup: " + e.getMessage());
//            }
//        }
//    }











    // Generic method to return the first visible and enabled element from multiple locators
    public WebElement findFirstVisibleElement(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement el : elements) {
                if (el.isDisplayed() && el.isEnabled()) {
                    return el;
                }
            }
        }
        throw new NoSuchElementException("No visible/enabled element found from provided locators.");
    }

    // Generic method to get list of elements from the first matching non-empty locator
    public List<WebElement> findVisibleElements(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            if (!elements.isEmpty()) {
                return elements;
            }
        }
        throw new NoSuchElementException("No elements found from provided autosuggest locators.");
    }

}

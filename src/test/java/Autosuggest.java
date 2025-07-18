import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Autosuggest {

    public static void waitForPageToLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete")
        );
    }
    public static WebElement findSearchField(WebDriver driver) {
        By[] locators = {
                By.xpath("//input[@type='text']"),
                By.xpath("//input[@name='q']"),
                By.xpath("//input[@id='Search-In-Modal']"),
                By.xpath("//input[@type='search']"),
                By.xpath("//input[contains(@placeholder, 'Search')]"),
                By.cssSelector("form[action*='/search'] input[type='search']"),

        };
        for (By locator : locators) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            } catch (Exception ignored) {}
        }
        throw new NoSuchElementException("Search input field not found.");
    }

    public static List<WebElement> autosuggestElement(WebDriver driver) {
        By[] locators = {
                By.xpath("//*[contains(@class, 'autosuggest')]//li"),
                By.xpath("//*[contains(@class, 'suggestion')]//li"),
                By.xpath("//*[contains(@class, 'unbxd')]//li"),
                By.xpath("//*[contains(@class, 'ui-autocomplete')]//li"),
                By.xpath("//div[contains(@class, 'unbxd-as-popular-product-name')]//span[@class='product-search-title']"),
                By.xpath("//a[@data-testid='product-card']//div[2]//div[2]")
        };

        for (By locator : locators) {
            try {
                List<WebElement> elements = driver.findElements(locator);
                if (elements != null && !elements.isEmpty()) {
                    return elements;
                }
            } catch (Exception ignored) {
                // Continue trying next locator
            }
        }

        throw new NoSuchElementException("Autosuggest element not found.");
    }

    public static void closePopupIfPresent(WebDriver driver) {
        By[] closeIconLocators = {
                By.xpath("//button[contains(@class,'close')]"),
                By.xpath("//div[contains(@class,'popup')]//button[contains(@class,'close')]"),
                By.xpath("//div[contains(@class,'modal')]//button[contains(text(),'×')]"),
                By.xpath("//div[contains(@class,'newsletter')]//button"),
                By.xpath("(//button[@data-role='closeBtn'])[1]"),
                By.xpath("//button[@aria-label='Close']"),
                By.xpath("//div[contains(@class,'popup-close') or contains(@class,'close-popup')]"),
                By.xpath("//div[contains(@class, 'overlay')]//button[contains(@class,'close')]")
        };

        for (By locator : closeIconLocators) {
            try {
                WebElement closeBtn = driver.findElement(locator);
                if (closeBtn.isDisplayed() && closeBtn.isEnabled()) {
                    closeBtn.click();
                    System.out.println("✅ Popup closed using: " + locator);
                    return; // Exit after first successful close
                }
            } catch (NoSuchElementException | ElementClickInterceptedException | StaleElementReferenceException ignored) {}
        }

        System.out.println("ℹ️ No popup close button found using defined locators.");
    }
    public static boolean isTitlePresentInUI(WebDriver driver, String title) {
        String[] words = title.toLowerCase().split("\\s+");
        StringBuilder xpathBuilder = new StringBuilder("//*[");

        for (int i = 0; i < words.length; i++) {
            xpathBuilder.append(String.format(
                    "contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"%s\")",
                    words[i]
            ));
            if (i < words.length - 1) {
                xpathBuilder.append(" and ");
            }
        }

        xpathBuilder.append("]");

        List<WebElement> elements = driver.findElements(By.xpath(xpathBuilder.toString()));
        return !elements.isEmpty();
    }



    public static void main(String[] args) throws InterruptedException {
        String query = "shoes";
        String apiUrl = "https://search.unbxd.io/ff71d6d18d1496ee4f51d0b95b052ec0/ss-unbxd-aapac-prod-mapactive-en-PH65121748366925/autosuggest?q="+query+"&topQueries.count=2&keywordSuggestions.count=2&popularProducts.count=4&promotedSuggestion.count=5&indent=off";
        String siteUrl = "https://www.footlocker.ph/";

//        String query = "dress";
//        String apiUrl = "https://search.unbxd.io/de171e93e18f254ee90dc9ac6e26070b/ss-unbxd-gus-Prod-UniqueVintage36331749185404/autosuggest?q="+query+"&inFields.count=0&topQueries.count=5&keywordSuggestions.count=6&popularProducts.count=6&promotedSuggestion.count=3&indent=off&variants=true";
//       String siteUrl =     "https://www.unique-vintage.com/";

//          String query = "saree";
//        String apiUrl ="https://search.unbxd.io/c884128c3d372ec016ebc092761d472f/ss-unbxd-aapac-AzaFashions---dev63501740736152/autosuggest?q="+query+"&inFields.count=0&popularProducts.count=6&keywordSuggestions.count=0&topQueries.count=5&promotedSuggestion.count=0";
//        String siteUrl =     "https://shop.azaonline.in/";

//          String query = "fridge";
//          String apiUrl = "https://search.unbxd.io/bd0be42f86b292a839b2a9f4570c3141/ss-unbxd-aanz-bsr-stancash-dev35741741236250/autosuggest?q="+query+"&version=V2&uid=uid-1752565779252-80002&inFields.count=10&topQueries.count=4&keywordSuggestions.count=8&promotedSuggestion.count=2&popularProducts.count=8";
//          String siteUrl = "https://sc-au-staging.myshopify.com/";

//        String query = "dress";
//        String apiUrl = "https://search.unbxd.io/579c6c9e792e43e038e7f40ca11b6103/ss-unbxd-aapac-shoppersstop-dev50901709028198/search?q="+query+"&inFields.count=10&topQueries.count=4&keywordSuggestions.count=8&promotedSuggestion.count=2&popularProducts.count=6";
//        String siteUrl = "https://www.shoppersstop.com/";
//
//          String query = "mouse";
//          String apiUrl ="https://search.unbxd.io/0de1e06bc0d3f62b42fb175e361758af/ss-unbxd-prod-mwave43601693203163/search?q="+query+"&topQueries.count=4&keywordSuggestions.count=8&promotedSuggestion.count=2&popularProducts.count=8";
//          String siteUrl = "https://www.mwave.com.au/";



        Response response = RestAssured.get(apiUrl);
        response.then().statusCode(200);

        List<String> rawTitles = response.jsonPath().getList("response.products.title");
        List<String> titles = new ArrayList<>(rawTitles.stream()
                .filter(title -> title != null && !title.trim().isEmpty())
                .toList());

        // Extract full list of product maps
        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");

        List<String> topSearchSuggestions = allProducts.stream()
                .filter(product -> "TOP_SEARCH_QUERIES".equals(product.get("doctype")))
                .map(product -> (String) product.get("autosuggest"))
                .filter(s -> s != null && !s.trim().isEmpty())
                .toList();

        List<String> keywordSuggestions = allProducts.stream()
                .filter(product -> "KEYWORD_SUGGESTION".equals(product.get("doctype")))
                .map(product -> (String) product.get("autosuggest"))
                .filter(s -> s != null && !s.trim().isEmpty())
                .toList();


        titles.addAll(topSearchSuggestions);
        titles.addAll(keywordSuggestions);

        System.out.println("Product Titles:");
        for (String title : titles) {
            System.out.println(title);

        }

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.manage().window().maximize();
        driver.get(siteUrl);
        waitForPageToLoad(driver);
     //   closePopupIfPresent(driver);



        WebElement searchBox = findSearchField(driver);
        searchBox.click();
       searchBox.sendKeys(query);
       Thread.sleep(5000);


//        List<WebElement> suggestionItems = autosuggestElement(driver);
//        for (WebElement item : suggestionItems) {
//            System.out.println(item.getText().trim());
//        }
//        System.out.println("UI Suggestions:");
//
//        for (WebElement suggestion : suggestionItems) {
//            String suggestionText = suggestion.getText().trim();
//            System.out.println(suggestionText);
//
//            String normalizedSuggestion = suggestionText.toLowerCase().trim();
//
//            boolean matchFound = titles.stream()
//                    .map(title -> title.toLowerCase().trim())
//                    .anyMatch(normalizedSuggestion::contains);
//
//            if (matchFound) {System.out.println("✅ Match found: " + suggestionText);}
//            else {System.out.println("❌ No match for: " + suggestionText);}
//        }

//        for (String expectedTitle : titles) {
//            String normalizedTitle = expectedTitle.toLowerCase();
//
//            String xpath = String.format(
//                    "//*[contains(translate(normalize-space(text()), " +
//                            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"%s\")]",
//                    normalizedTitle
//            );
//
//            List<WebElement> elements = driver.findElements(By.xpath(xpath));
//            if (!elements.isEmpty()) {
//                System.out.println("✅ Title found in UI: " + expectedTitle);
//            } else {
//                System.out.println("❌ Title NOT found in UI: " + expectedTitle);
//            }
//        }
//        for (String expectedTitle : titles) {
//            boolean found = isTitlePresentInUI(driver, expectedTitle);
//            if (found) {
//                System.out.println("✅ Title found in UI: " + expectedTitle);
//            } else {
//                System.out.println("❌ Title NOT found in UI: " + expectedTitle);
//            }
//         }

 //       public void verifyTitlesPresentInUI(List<String> titles) {
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
  //      }


        driver.quit();
    }
}

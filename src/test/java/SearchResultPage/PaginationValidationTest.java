package SearchResultPage;

import Base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BasePage;
import utils.ScreenshotUtil;
import utils.TestData;
import java.util.List;
import org.openqa.selenium.WebElement;

public class PaginationValidationTest extends BaseTest {

    @Test
    public void TC_187_validatePaginationFunctionality() throws InterruptedException {
        TestData searchData = new TestData("data.xlsx", "mwave");
        driver.get(searchData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Step 1: Enter search query
        String searchQuery = "mouse";
        logInfo("Entering search query: " + searchQuery);
        basePage.searchPage.enterInSearchBox(searchQuery);
        basePage.searchPage.pressEnterInSearchBox();

        // Wait for search results and take screenshot
        Thread.sleep(3000);
        String initialUrl = driver.getCurrentUrl();
        logInfo("Initial URL: " + initialUrl);
        ScreenshotUtil.takeScreenshotAndAttachToReport(driver, test, "search_results_page");
        
        boolean isPaginationPresent = basePage.searchPage.isPaginationPresent();
        Assert.assertTrue(isPaginationPresent, "Pagination should be present on search results page");
        logPass("Pagination is present on the page");
    }

    @Test //(dependsOnMethods = "TC_187_validatePaginationFunctionality")
    public void TC_188_validatePaginationNavigation() throws InterruptedException {
        TestData searchData = new TestData("data.xlsx", "mwave");
        driver.get(searchData.getSiteUrl());
        BasePage basePage = new BasePage(driver);
        
        // Step 1: Enter search query that will return multiple pages
        String searchQuery = "mouse";
        logInfo("Entering search query: " + searchQuery);
        basePage.searchPage.enterInSearchBox(searchQuery);
        basePage.searchPage.pressEnterInSearchBox();
        String intialUrl = driver.getCurrentUrl();
        Thread.sleep(2000);

        basePage.paginationPage.clickNextButton();
        Thread.sleep(2000);
        String nextpage2Url = driver.getCurrentUrl();
        Assert.assertNotEquals(intialUrl, nextpage2Url, "Navigation to next page is not working");
        logPass("Navigation to next page 2 is working" + nextpage2Url);

        basePage.paginationPage.clickNextButton();
        Thread.sleep(2000);
        String nextpage3Url = driver.getCurrentUrl();
        Assert.assertNotEquals(nextpage2Url, nextpage3Url, "Navigation to next page is not working");
        logPass("Navigation to next page 3 is working"+nextpage3Url);

        basePage.paginationPage.clickPreviousButton();
        Thread.sleep(2000);
        String previouspageUrl = driver.getCurrentUrl();
        Assert.assertEquals(nextpage2Url, previouspageUrl, "Navigation to previous page is not working");
        logPass("Navigation to previous page is working" + previouspageUrl);  
    }
} 
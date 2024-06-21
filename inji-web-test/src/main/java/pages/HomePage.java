package pages;


import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class HomePage extends BasePage {

    private WebDriver driver;


    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickOnHelp() {
        if (isElementIsVisible(driver, By.xpath("//div[@data-testid='Header-Menu-Help']"))) {
            clickOnElement(driver, By.xpath("//div[@data-testid='Header-Menu-Help']"));
        } else {
            clickOnElement(driver, By.xpath("//li[@data-testid='Header-Menu-Help']"));
        }
    }

    public Boolean isLogoDisplayed() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return isElementIsVisible(driver, By.xpath("//div[@data-testid='Header-InjiWeb-Logo-Container']"));
    }

    public String isPageTitleDisplayed() {
        return driver.getCurrentUrl();
    }

    public String isHomePageTextDisplayed() {
        return getElementText(driver, By.xpath("//h2[@data-testid='IntroBox-Text']"));
    }

    public String isHomePageDiscriptinTextDisplayed() {
        return getElementText(driver, By.xpath("//p[@data-testid='IntroBox-SubText']"));
    }

    public void clickOnDownloadMosipCredentials() {
        clickOnElement(driver, By.xpath("(//h3[@data-testid='ItemBox-Text'])[1]"));
    }

    public Boolean isListOfCredentialsTypeDisplayed() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isElementIsVisible(driver, By.xpath("//div[@data-testid='HeaderTile-Text']"));
    }

    public void clickOnVerify() {
        clickOnElement(driver, By.xpath("//button[@id='verify_otp']"));
        if (isElementIsVisible(driver, By.xpath("//button[@id='verify_otp']"))) {
            clickOnElement(driver, By.xpath("//button[@id='verify_otp']"));
        }
    }

    public String isSuccessMessageDisplayed() {
        try {
            Thread.sleep(6000);
            ;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getElementText(driver, By.xpath("//p[@data-testid='DownloadResult-Title']"));
    }

    public Boolean isMosipNationalIdDisplayed() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isElementIsVisible(driver, By.xpath("//h3[@data-testid='ItemBox-Text']"));
    }

    //
    public void enterIssuersInSearchBox(String string) {
        enterText(driver, By.xpath("//input[@type='text']"), string);
//		clickOnElement(driver,By.xpath("//p[@data-testid='IntroBox-SubText']"));
    }

    //
    public Boolean isGoHomeButtonDisplayed() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isElementIsVisible(driver, By.xpath("//button[text()='Go To Home']"));
    }

    public Boolean isBackButtonDisplayed() {
        return isElementIsVisible(driver, By.xpath("//*[@class='cursor-pointer']"));
    }

    //
    public Boolean isLanguageDisplayed() {
        return isElementIsVisible(driver, By.xpath("//button[@class='inline-flex items-center']"));
    }

    //
    public void clickOnLanguageButton() {
        clickOnElement(driver, By.xpath("//button[@data-testid='Language-Selector-Button']"));
    }

    public boolean verifyLanguagesInLanguageFilter() {
        List<String> expectedLanguages = Arrays.asList("English", "தமிழ்", "ಕನ್ನಡ", "हिंदी", "Français", "عربي");
        List<String> actualLanguages = null;
        try {
            actualLanguages = getElementTexts(driver, By.xpath("//ul[@class='py-1 divide-y divide-gray-200']//li"));

        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("expectedLanguages" + expectedLanguages);
        System.out.println("actualLanguages" + actualLanguages);
        return new HashSet<>(expectedLanguages).equals(new HashSet<>(actualLanguages));
    }

    public void selectOtherLangauge() {
        clickOnElement(driver, By.xpath("(//button[@type='button'])[3]"));

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //
    public Boolean isNoIssuerFoundMessageDisplayed() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return isElementIsVisible(driver, By.xpath("//p[@data-testid='EmptyList-Text']"));
    }
//
//	public void isPdfDownloaded() {
//		 Download download = page.waitForDownload((
//	}

    public void clickOnArabicLanguage() {
        clickOnElement(driver, By.xpath("//button[contains(text(), 'عربي')]"));
    }

    public void clickOnTamilLanguage() {
        clickOnElement(driver, By.xpath("//button[contains(text(), 'தமிழ்')]"));
    }

    public void clickOnKannadaLanguage() {
        clickOnElement(driver, By.xpath("//button[contains(text(), 'ಕನ್ನಡ')]"));
    }

    public void clickOnHindiLanguage() {
        clickOnElement(driver, By.xpath("//button[contains(text(), 'हिंदी')]"));
    }

    public void clickOnFranchLanguage() {
        clickOnElement(driver, By.xpath("//button[contains(text(), 'Français')]"));
    }

    public String isCredentialTypesDisplayed() {
        return getElementText(driver, By.xpath("//div[@data-testid='HeaderTile-Text']"));
    }

    public String isNationalIdentityDepartmentTextDisplayed() {
        return getElementText(driver, By.xpath("//span[@data-testid='NavBar-Text']"));
    }

    public String isVeridoniaInsuranceCompanyTextDisplayed() {
        return getElementText(driver, By.xpath("//span[@data-testid='NavBar-Text']"));
    }

    public static void scrollDownByPage(WebDriver driver) {
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.PAGE_DOWN).build().perform();
    }

    public void ClickOnHelpForMobileBrowse() {
        if (isElementIsVisible(driver, By.xpath("//div[@data-testid='Header-InjiWeb-Logo-Container']/div"))) {
            clickOnElement(driver, By.xpath("//div[@data-testid='Header-InjiWeb-Logo-Container']/div"));
        }
    }


}

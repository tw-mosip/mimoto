package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SunbirdCredentials extends BasePage {
    private WebDriver driver;

    public SunbirdCredentials(WebDriver driver) {
        this.driver = driver;
    }

    public Boolean isDownloadSunbirdCredentialsDisplayed() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isElementIsVisible(driver, By.xpath("//*[text()='Veridonia Insurance Company']"));

    }

    public Boolean isSunbirdInsuranceDisplayed() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isElementIsVisible(driver, By.xpath("(//div[@class='justify-center items-center'])[1]"));
    }

    public void clickOnSunbirdInsurance() {

        clickOnElement(driver, By.xpath("(//div[@class='justify-center items-center'])[1]"));
    }

    public void clickOnDownloadSunbird() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        clickOnElement(driver, By.xpath("//*[text()='Veridonia Insurance Company']"));

    }


    public void enterPolicyNumer(String string) {
        enterText(driver, By.xpath("//input[@id='_form_policyNumber']"), string);
    }

    public Boolean isPolicyNumeTextBoxDisplayed() {
        return isElementIsVisible(driver, By.xpath("//input[@id='_form_policyNumber']"));
    }

    public void enterFullName(String string) {
        enterText(driver, By.xpath("//input[@id='_form_fullName']"), string);
    }

    public void selectDateOfBirth() {
//		 page.keyboard().press("Tab");
//	page.fill("#_form_dob", "2024-01-01");

        driver.findElement(By.xpath("//input[@id='_form_fullName']")).sendKeys(Keys.TAB);
        driver.findElement(By.id("_form_dob")).sendKeys("01/01/2024");


//		WebElement ele = driver.findElement(By.xpath("//input[@id='_form_fullName']"));
//		ele.sendKeys(Keys.TAB);
//		ele.sendKeys(Keys.SPACE);
//
//		driver.findElement(By.xpath("//*[contains(text(), 'SET')]")).click();
    }

    public void clickOnLogin() {
        clickOnElement(driver, By.xpath("//button[@id='verify_form']"));
    }

    public Boolean isLifeInceranceDisplayed() {
        return isElementIsVisible(driver, By.xpath("(//div[@class='justify-center items-center'])[2]"));
    }

    public void clickOnLifeInsurance() {
        clickOnElement(driver, By.xpath("(//div[@class='justify-center items-center'])[2]"));
    }

    public Boolean isEnterPolicyNumberHeaderDisplayed() {
        return isElementIsVisible(driver, By.xpath("//label[text() = 'Enter Policy Number']"));
    }

    public Boolean isEnterFullNameHeaderDisplayed() {
        return isElementIsVisible(driver, By.xpath("//label[text() = 'Enter Full Name']"));
    }

    public Boolean isEnterDOBHeaderDisplayed() {
        return isElementIsVisible(driver, By.xpath("//label[text() = 'Enter DOB']"));
    }

    public Boolean isAuthenticationFailedDisplayed() {
        return isElementIsVisible(driver, By.xpath("//div[contains(text(), 'Authentication Failed: ')]  "));
    }

    public Boolean isVehicleInsuranceDisplayed() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isElementIsVisible(driver, By.xpath("(//div[@class='justify-center items-center'])[2]"));
    }

    public void clickOnVehicleInsurance() {
        clickOnElement(driver, By.xpath("(//div[@class='justify-center items-center'])[2]"));
    }
}

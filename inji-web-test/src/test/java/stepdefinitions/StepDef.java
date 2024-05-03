
package stepdefinitions;

import org.junit.Assert;

import com.microsoft.playwright.Page;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.HomePage;
import utils.DriverManager;

public class StepDef{

	private String pageTitle;

	Page page;
	DriverManager driver;
	HomePage homePage;

	public StepDef(DriverManager driver) {
		this.driver = driver;
		page = driver.getPage();
		homePage = new HomePage(page);
	}
	
	@Given("User gets the title of the page")
	public void getTheTitleOfThePage() {
		pageTitle = page.title();
	}

	@Then("Validate the title of the page")
	public void validateTheTitleOfThePage() {
		Assert.assertEquals(pageTitle, "INJI Web");
	}

	@Then("Verify that inji web logo is displayed")
	public void verifyInjiWebLogoIsDisplayed() throws InterruptedException {
		Thread.sleep(3000);
		Assert.assertEquals(homePage.isLogoDisplayed(), true);
	}

	@When("User clicks on the help button")
	public void clicksOnHelpButton() {
		homePage.clickOnHelp();
	}

	@Then("Verify that text help is displayed on page")
	public void verifiyTextHelpIsDisplayedOnPage() throws InterruptedException {
		Thread.sleep(3000);
		Assert.assertEquals(homePage.isTextWordIsDisplayed(), true);
	}

}

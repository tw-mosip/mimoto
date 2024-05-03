Feature: Inji web homepage testing

  @smoke @verifyingHomepage
  Scenario: Verify the Inji web homepage

    Given User gets the title of the page
    Then Validate the title of the page
    And Verify that inji web logo is displayed
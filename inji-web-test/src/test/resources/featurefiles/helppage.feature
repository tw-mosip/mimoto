Feature: Inji web help page testing

@regression @verifyHelpPage
  Scenario: Navigating to help page and verfying it

    Given User gets the title of the page
    Then Validate the title of the page
    When User clicks on the help button
    Then Verify that text help is displayed on page
    And Verify that inji web logo is displayed
package com.ftn.iss.eventPlanner.selenium.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavigationBarPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public NavigationBarPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//button[.//mat-icon[text()='menu']]")
    private WebElement menuButton;

    @FindBy(xpath = "//button[.//mat-icon[text()='account_circle']]")
    private WebElement loginButton;

    @FindBy(xpath = "//button[.//span[text()='Create event']]")
    private WebElement createEventMenuItem;
    @FindBy(xpath = "//button[.//span[text()='Budget']]")
    private WebElement budgetMenuItem;

    public void openMenuAndClickCreateEvent() {
        // Click the menu icon
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();

        // Wait for the "Create event" menu item to appear
        wait.until(ExpectedConditions.visibilityOf(createEventMenuItem));

        // Click on "Create event"
        createEventMenuItem.click();
    }
    public void openMenuAndClickBudget() {
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();

        wait.until(ExpectedConditions.visibilityOf(budgetMenuItem));

        budgetMenuItem.click();
    }
    public void openLoginPage() {
        // Click the login button
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();

        // Wait for the login page to load
        wait.until(ExpectedConditions.urlContains("/login"));
    }
}
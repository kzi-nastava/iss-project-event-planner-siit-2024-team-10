package com.ftn.iss.eventPlanner.selenium.page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FilterEventsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public FilterEventsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // === FORM FIELDS ===

    // Dropdown: Event Type
    @FindBy(css = "mat-select[formcontrolname='eventTypeId']")
    public WebElement eventTypeDropdown;

    // Input: Location (City)
    @FindBy(css = "input[formcontrolname='location']")
    public WebElement locationInput;

    // Input: Max Participants
    @FindBy(css = "input[formcontrolname='maxParticipants']")
    public WebElement maxParticipantsInput;

    // Slider: Min Rating
    @FindBy(css = "input[formcontrolname='minRating']")
    public WebElement ratingInput;

    // Date Range Inputs
    @FindBy(css = "input[formcontrolname='startDate']")
    public WebElement startDateInput;

    @FindBy(css = "input[formcontrolname='endDate']")
    public WebElement endDateInput;

    // === BUTTONS ===

    @FindBy(xpath = "//button[contains(.,'Cancel')]")
    public WebElement cancelButton;

    @FindBy(xpath = "//button[contains(.,'Apply')]")
    public WebElement applyButton;

    // === ACTIONS ===

    public void selectEventType(String eventTypeName) {
        // Open dropdown options
        eventTypeDropdown.click();

        // Find span inside the dropdown
        By optionLocator = By.xpath("//mat-option//span[normalize-space(text())='" + eventTypeName + "']");

        // Wait for the option to become visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));

        // Scroll and click
        WebElement option = driver.findElement(optionLocator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
        new Actions(driver).moveToElement(option).click().perform();
    }

    public void setMinRating(double rating) {
        WebElement sliderInput = ratingInput; // već nađen element inputa u slideru

        // Postavi vrednost slidera programatski
        String script = "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));";
        ((JavascriptExecutor) driver).executeScript(script, sliderInput, String.valueOf(rating));
    }

    public void setLocation(String location) {
        locationInput.clear();
        locationInput.sendKeys(location);
    }

    public void setMaxParticipants(int max) {
        maxParticipantsInput.clear();
        maxParticipantsInput.sendKeys(String.valueOf(max));
    }

    public void setDateRange(String startDate, String endDate) {
        startDateInput.clear();
        startDateInput.sendKeys(startDate);
        endDateInput.clear();
        endDateInput.sendKeys(endDate);
    }

    public void clickApply() {
        applyButton.click();
    }

    public void clickCancel() {
        cancelButton.click();
    }
}

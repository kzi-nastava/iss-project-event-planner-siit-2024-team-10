package com.ftn.iss.eventPlanner.selenium.page;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EditEventPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public EditEventPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // Checkbox: No event types apply
    @FindBy(css = "mat-checkbox[formcontrolname='noEventType'] input[type='checkbox']")
    public WebElement noEventTypeCheckbox;

    // Select: Event Type
    @FindBy(css = "mat-select[formcontrolname='eventType']")
    public WebElement eventTypeSelect;

    // Input: Name
    @FindBy(css = "input[formcontrolname='name']")
    public WebElement nameInput;

    // Textarea: Description
    @FindBy(css = "textarea[formcontrolname='description']")
    public WebElement descriptionInput;

    // Input: Max Participants
    @FindBy(css = "input[formcontrolname='maxParticipants']")
    public WebElement maxParticipantsInput;

    // Input: Country
    @FindBy(css = "input[formcontrolname='country']")
    public WebElement countryInput;

    // Input: City
    @FindBy(css = "input[formcontrolname='city']")
    public WebElement cityInput;

    // Input: Street
    @FindBy(css = "input[formcontrolname='street']")
    public WebElement streetInput;

    // Input: House Number
    @FindBy(css = "input[formcontrolname='houseNumber']")
    public WebElement houseNumberInput;

    // Input: Date Picker
    @FindBy(css = "input[formcontrolname='date']")
    public WebElement dateInput;

    @FindBy(xpath = "//mat-button-toggle[@value='open']//button")
    private WebElement openToggleButton;

    @FindBy(xpath = "//mat-button-toggle[@value='closed']//button")
    private WebElement closedToggleButton;

    // Submit button
    @FindBy(css = "button.submit-btn")
    public WebElement submitButton;

    public void selectFirstEventType(){
        eventTypeSelect.click();

        // Wait for options panel to become visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-mdc-select-panel")));

        // Select the first option (if present)
        WebElement firstOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".mat-mdc-option:not([aria-disabled='true'])")));

        firstOption.click();
    }

    public void setName(String name) {
        nameInput.clear();
        nameInput.sendKeys(name);
    }

    public void setDescription(String description) {
        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    public void setMaxParticipants(int maxParticipants) {
        maxParticipantsInput.clear();
        maxParticipantsInput.sendKeys(String.valueOf(maxParticipants));
    }

    public void setCountry(String country) {
        countryInput.clear();
        countryInput.sendKeys(country);
    }

    public void setCity(String city) {
        cityInput.clear();
        cityInput.sendKeys(city);
    }

    public void setStreet(String street) {
        streetInput.clear();
        streetInput.sendKeys(street);
    }

    public void setHouseNumber(String houseNumber) {
        houseNumberInput.clear();
        houseNumberInput.sendKeys(houseNumber);
    }

    public void setDate(String date) {
        dateInput.clear();
        dateInput.sendKeys(date);
    }

    public void setPublicityOpen() {
        openToggleButton.click();
    }

    public void setPublicityClosed() {
        closedToggleButton.click();
    }

    public void submitForm() {
        dismissSnackbarIfPresent();
        submitButton.click();
    }

    public void dismissSnackbarIfPresent() {
        try {
            WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'mat-mdc-snack-bar-action')]//button[normalize-space()='OK']")
            ));

            // Click the OK button
            okButton.click();

            // Optionally wait for snackbar label to disappear
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div[matSnackbarLabel]")));

        } catch (TimeoutException e) {
            // Snackbar not present or no action button – nothing to dismiss
        }
    }

    public void fillForm(String name, String description, int maxParticipants, String country,
                         String city, String street, String houseNumber, String date, boolean isOpen) {

        setName(name);
        setDescription(description);
        setMaxParticipants(maxParticipants);
        setCountry(country);
        setCity(city);
        setStreet(street);
        setHouseNumber(houseNumber);
        clearInput(dateInput);
        setDate(date);

        if (isOpen) {
            setPublicityOpen();
        } else {
            setPublicityClosed();
        }
    }

    public void clearInput(WebElement input) {
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(Keys.DELETE);
    }

    public void waitForSubmitButtonEnabled(boolean shouldBeEnabled) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        if (shouldBeEnabled) {
            wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        } else {
            wait.until(driver -> !submitButton.isEnabled());
        }
    }
}

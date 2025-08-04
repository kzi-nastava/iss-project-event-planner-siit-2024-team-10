package com.ftn.iss.eventPlanner.selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AgendaDialogPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public AgendaDialogPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    // Name field
    @FindBy(css = "input[formcontrolname='name']")
    public WebElement nameInput;

    // Description field
    @FindBy(css = "textarea[formcontrolname='description']")
    public WebElement descriptionInput;

    // Start Time
    @FindBy(css = "input[formcontrolname='startTime']")
    public WebElement startTimeInput;

    // End Time
    @FindBy(css = "input[formcontrolname='endTime']")
    public WebElement endTimeInput;

    // Location
    @FindBy(css = "input[formcontrolname='location']")
    public WebElement locationInput;

    // Save button
    @FindBy(xpath = "//button[contains(., 'Save')]")
    public WebElement saveButton;

    // Cancel button
    @FindBy(xpath = "//button[.='Cancel']")
    public WebElement cancelButton;

    public void setName(String name) {
        nameInput.clear();
        nameInput.sendKeys(name);
    }

    public void setDescription(String description) {
        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    public void setStartTime(String startTime) {
        startTimeInput.sendKeys(startTime);
    }

    public void setEndTime(String endTime) {
        endTimeInput.sendKeys(endTime);
    }

    public void setLocation(String location) {
        locationInput.clear();
        locationInput.sendKeys(location);
    }

    public void fillForm(String name, String desc, String start, String end, String location) {
        nameInput.clear();
        nameInput.sendKeys(name);
        descriptionInput.clear();
        descriptionInput.sendKeys(desc);
        clearTimeInput(startTimeInput);
        startTimeInput.sendKeys(start);
        clearTimeInput(endTimeInput);
        endTimeInput.sendKeys(end);
        locationInput.clear();
        locationInput.sendKeys(location);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        saveButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='startTime']")
        ));
    }

    public void clickCancel() {
        cancelButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='startTime']")
        ));
    }

    public void clearInput(WebElement input) {
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(Keys.DELETE);
    }

    public void clearTimeInput(WebElement input) {
        input.sendKeys(Keys.DELETE);
        input.sendKeys(Keys.ARROW_RIGHT);
        input.sendKeys(Keys.DELETE);
        input.sendKeys(Keys.ARROW_RIGHT);
        input.sendKeys(Keys.DELETE);
        input.sendKeys(Keys.ARROW_LEFT);
        input.sendKeys(Keys.ARROW_LEFT);
    }

    public void waitForSaveButtonEnabled(boolean shouldBeEnabled) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        if (shouldBeEnabled) {
            wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        } else {
            wait.until(driver -> !saveButton.isEnabled());
        }
    }
}

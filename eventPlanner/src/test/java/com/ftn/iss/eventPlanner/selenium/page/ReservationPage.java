package com.ftn.iss.eventPlanner.selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ReservationPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Page Elements
    @FindBy(css = "input[formcontrolname='startTime']")
    private WebElement startTimeInput;

    @FindBy(css = "input[formcontrolname='endTime']")
    private List<WebElement> endTimeInputs;

    @FindBy(id = "book-button")
    private WebElement bookButton;

    @FindBy(xpath = "//h2[contains(text(),'Confirmation')]")
    private WebElement confirmationHeader;

    @FindBy(id = "confirm-button")
    private WebElement confirmButton;

    @FindBy(css = "mat-select[formcontrolname='event']")
    private WebElement eventSelect;

    @FindBy(css = "mat-option")
    private List<WebElement> eventOptions;

    public ReservationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void fillServiceTimeInputs(String startTime, String endTime) {
        wait.until(ExpectedConditions.elementToBeClickable(startTimeInput));
        startTimeInput.clear();
        startTimeInput.sendKeys(startTime);

        if (!endTimeInputs.isEmpty() && endTime != null) {
            WebElement endTimeInput = endTimeInputs.get(0);
            endTimeInput.clear();
            endTimeInput.sendKeys(endTime);
        }
    }
    public void selectEventByName(String eventName) {
        wait.until(ExpectedConditions.elementToBeClickable(eventSelect));
        eventSelect.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(eventOptions));

        for (WebElement option : eventOptions) {
            if (option.getText().contains(eventName)) {
                option.click();
                return;
            }
        }

        throw new RuntimeException("Event with name '" + eventName + "' not found");
    }

    public void confirmServiceBooking() {
        wait.until(ExpectedConditions.elementToBeClickable(bookButton));
        bookButton.click();

        wait.until(ExpectedConditions.visibilityOf(confirmationHeader));

        wait.until(ExpectedConditions.elementToBeClickable(confirmButton));
        confirmButton.click();
    }

    public void fillStartTime(String startTime) {
        wait.until(ExpectedConditions.elementToBeClickable(startTimeInput));
        startTimeInput.clear();
        startTimeInput.sendKeys(startTime);
    }

    public void fillEndTime(String endTime) {
        if (!endTimeInputs.isEmpty() && endTime != null) {
            WebElement endTimeInput = endTimeInputs.get(0);
            wait.until(ExpectedConditions.elementToBeClickable(endTimeInput));
            endTimeInput.clear();
            endTimeInput.sendKeys(endTime);
        }
    }

    public void clickBookButton() {
        wait.until(ExpectedConditions.elementToBeClickable(bookButton));
        bookButton.click();
    }

    public void waitForConfirmationDialog() {
        wait.until(ExpectedConditions.visibilityOf(confirmationHeader));
    }

    public void clickConfirmButton() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmButton));
        confirmButton.click();
    }
}
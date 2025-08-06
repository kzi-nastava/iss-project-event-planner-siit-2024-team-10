package com.ftn.iss.eventPlanner.selenium.page;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OfferingDetailsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Page Elements
    @FindBy(css = "button.book-now-btn")
    private WebElement bookNowButton;

    @FindBy(xpath = "//h1[contains(text(), 'Book a Service')]")
    private WebElement serviceBookingHeader;

    @FindBy(xpath = "//h2[contains(text(), 'Select Event')]")
    private WebElement eventSelectionHeader;

    @FindBy(css = "simple-snack-bar")
    private WebElement snackBar;

    @FindBy(css = "mat-select[formcontrolname='event']")
    private WebElement eventSelect;

    @FindBy(css = "mat-option")
    private List<WebElement> eventOptions;

    @FindBy(xpath = "//button[span[contains(text(), 'Confirm')]]")
    private WebElement confirmButton;

    @FindBy(xpath = "//h2[contains(text(),'Confirmation')]")
    private WebElement confirmationHeader;

    @FindBy(id = "confirm-button")
    private WebElement finalConfirmButton;

    public OfferingDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickBookNowButton() {
        wait.until(ExpectedConditions.elementToBeClickable(bookNowButton));
        bookNowButton.click();
    }

    public boolean isBookNowButtonEnabled() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.book-now-btn")));
        return bookNowButton.isEnabled();
    }

    public void waitForServiceBookingDialog() {
        wait.until(ExpectedConditions.visibilityOf(serviceBookingHeader));
    }

    public void waitForEventSelectionDialog() {
        wait.until(ExpectedConditions.visibilityOf(eventSelectionHeader));
    }

    public void selectEventByName(String eventName) {
        wait.until(ExpectedConditions.elementToBeClickable(eventSelect));
        eventSelect.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-option[contains(., '" + eventName + "')]")));
        option.click();
    }
    public void confirmProductPurchase() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmButton));
        confirmButton.click();

        wait.until(ExpectedConditions.visibilityOf(confirmationHeader));

        wait.until(ExpectedConditions.elementToBeClickable(finalConfirmButton));
        finalConfirmButton.click();
    }

    public void waitForSnackbarWithText(String expectedText) {
        WebElement snackBarElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., \"" + expectedText + "\")]")));
        Assertions.assertNotNull(snackBarElement, "Expected snackbar with text '" + expectedText + "' was not shown.");
    }

    public String getSnackBarMessage() {
        wait.until(ExpectedConditions.visibilityOf(snackBar));
        return snackBar.getText();
    }
}
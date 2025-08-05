package com.ftn.iss.eventPlanner.selenium.page;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class OfferingListPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public OfferingListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickOfferingCardById(int offeringId) {
        String selector = String.format("mat-card.offering-card[data-id='%d']", offeringId);
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));

        card.click();
    }
}

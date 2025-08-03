package com.ftn.iss.eventPlanner.selenium.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EventDetailsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(tagName = "h1")
    private WebElement eventName;

    public void waitForFetch() {
        wait.until(driver -> {
            String title = getEventName();
            return title != null && !title.trim().isEmpty();
        });
    }

    public EventDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public String getEventName() {
        return eventName.getText();
    }
}

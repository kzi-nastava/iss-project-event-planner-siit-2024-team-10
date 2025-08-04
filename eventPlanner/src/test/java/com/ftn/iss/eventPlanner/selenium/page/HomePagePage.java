package com.ftn.iss.eventPlanner.selenium.page;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomePagePage {

    private final WebDriver driver;

    public HomePagePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @Getter
    @FindBy(css = ".all-event-cards app-event-card")
    private List<WebElement> allEventCards;

    // === SEARCH EVENT ===

    @FindBy(css = ".all-events .name-search-section input")
    public WebElement eventSearchInput;

    @FindBy(css = ".all-events .name-search-section .search-button")
    public WebElement eventSearchButton;

    // === FILTER EVENTS ===

    @FindBy(xpath = "//button[contains(.,'Filter') and ancestor::div[contains(@class, 'event-filter')]]")
    public WebElement eventFilterButton;


    // === ACTIONS ===

    public void searchEventByName(String query) {
        eventSearchInput.clear();
        eventSearchInput.sendKeys(query);
        eventSearchButton.click();
    }

    public void clickEventFilterButton() {
        eventFilterButton.click();
    }

    public WebElement getSearchEventInput() {
        return eventSearchInput;
    }

    public List<String> getAllEventTypesFromCards() {
        List<WebElement> cards = getAllEventCards();
        List<String> eventTypes = new ArrayList<>();

        for (WebElement card : cards) {
            List<WebElement> tags = card.findElements(By.cssSelector(".tag"));
            if (tags.size() >= 2) { // prvi je uvek "EVENT", drugi je tip
                eventTypes.add(tags.get(1).getText().trim());
            }
        }
        return eventTypes;
    }

    public boolean areAllEventDatesInRange(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

        return getAllEventCards().stream().allMatch(card -> {
            String text = card.getText();
            int onIndex = text.lastIndexOf("on");
            if (onIndex == -1) return false;
            try {
                String afterOn = text.substring(onIndex + 3).trim();
                String dateStr = afterOn.split("\\s+")[0].trim();
                LocalDate date = LocalDate.parse(dateStr, formatter);
                return !date.isBefore(start) && !date.isAfter(end);
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean areAllEventRatingsAbove(double minRating) {
        return getAllEventCards().stream().allMatch(card -> {
            try {
                String text = card.getText();
                int starIndex = text.indexOf("★");
                if (starIndex == -1) return false;

                String beforeStar = text.substring(0, starIndex).trim();
                String[] tokens = beforeStar.split("\\s+");
                String ratingStr = tokens[tokens.length - 1];
                double rating = Double.parseDouble(ratingStr);

                return rating >= minRating;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


}


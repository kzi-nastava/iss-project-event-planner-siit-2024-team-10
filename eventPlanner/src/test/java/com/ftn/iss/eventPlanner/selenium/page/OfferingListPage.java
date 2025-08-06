package com.ftn.iss.eventPlanner.selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OfferingListPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public OfferingListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchAndClickOffering(String searchText, boolean service) {
        if(service) {
            WebElement servicesRadio = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("mat-radio-button[value='1']")));
            servicesRadio.click();
        }else{
            WebElement servicesRadio = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("mat-radio-button[value='2']")));
            servicesRadio.click();
        }

        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("offering-input")));
        searchInput.clear();
        searchInput.sendKeys(searchText);

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("search-button")));
        searchButton.click();

        WebElement firstCard = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("app-offering-card")));
        firstCard.click();
    }
}
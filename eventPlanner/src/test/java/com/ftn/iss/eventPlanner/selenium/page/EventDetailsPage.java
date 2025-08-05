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

public class EventDetailsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(tagName = "h1")
    private WebElement eventName;

    @FindBy(xpath = "(//button[contains(@class, 'fab') and .//mat-icon[text()='create']])[1]")
    private WebElement editEventButton;

    @FindBy(xpath = "//div[contains(@class, 'meta-info')]/span[contains(@class, 'meta-item')][1]")
    private WebElement eventType;

    @FindBy(xpath = "//h2[text()='Description']/following-sibling::p")
    private WebElement eventDescription;

    @FindBy(xpath = "//h2[text()='Location']/following-sibling::p")
    private WebElement eventLocation;

    @FindBy(xpath = "//h2[text()='Date']/following-sibling::p")
    private WebElement eventDate;

    @FindBy(xpath = "//div[contains(@class, 'meta-info')]/span[contains(@class, 'meta-item')]")
    private List<WebElement> metaItems;

    @FindBy(css = ".participants-section")
    private WebElement participantsSection;

    @FindBy(xpath = "//h2[text()='Agenda']/following-sibling::button")
    private WebElement addAgendaItemButton;

    @FindBy(css = ".agenda-item")
    private List<WebElement> agendaItems;

    @FindBy(xpath = "//button[mat-icon[text()='create']]")
    private List<WebElement> editAgendaButtons;

    @FindBy(xpath = "//button[mat-icon[text()='delete']]")
    private List<WebElement> deleteAgendaButtons;

    @FindBy(css = ".fab.left.secondary")
    private WebElement deleteEventButton;

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

    public void openUpdateEventPage() {
        editEventButton.click();
        wait.until(ExpectedConditions.urlContains("/edit-event"));
    }

    public String getEventDescription() {
        return eventDescription.getText();
    }

    public String getEventLocation() {
        return eventLocation.getText();
    }

    public String getEventDate() {
        return eventDate.getText();
    }

    public boolean hasEventType(){
        return metaItems.size() == 2;
    }

    public String getCity() {
        String location = getEventLocation();
        String[] parts = location.split(", ");
        return parts.length > 1 ? parts[1] : "";
    }

    public String getCountry() {
        String location = getEventLocation();
        String[] parts = location.split(", ");
        return parts.length > 2 ? parts[2] : "";
    }

    public String getStreet() {
        String location = getEventLocation();
        String streetPart = location.split(", ")[0];
        return streetPart.substring(0, streetPart.lastIndexOf(" "));
    }

    public String getHouseNumber() {
        String location = getEventLocation();
        String streetPart = location.split(", ")[0];
        return streetPart.substring(streetPart.lastIndexOf(" ") + 1);
    }

    public boolean isPublic() {
        return participantsSection.isDisplayed();
    }

    public void clickDeleteEventButton(){
        deleteEventButton.click();
    }

    public void confirmDialog(){
        WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button/span[contains(text(), 'Confirm')]")
        ));
        okButton.click();
    }

    public void cancelDialog(){
        WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button/span[contains(text(), 'Cancel')]")
        ));
        cancelButton.click();
    }

    public void clickAddAgendaItemButton() {
        addAgendaItemButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='startTime']")
        ));
    }

    public String getFirstAgendaItemName() {
        if (agendaItems.isEmpty()) return null;
        WebElement agendaItem = agendaItems.get(0);
        return agendaItem.findElement(By.xpath(".//span[contains(@class,'agenda-label') and text()='Name: ']/following-sibling::span[contains(@class,'agenda-value')]"))
                .getText().trim();
    }

    public String getFirstAgendaItemDescription() {
        if (agendaItems.isEmpty()) return null;
        WebElement agendaItem = agendaItems.get(0);
        return agendaItem.findElement(By.xpath(".//span[contains(@class,'agenda-label') and text()='Description: ']/following-sibling::span[contains(@class,'agenda-value')]"))
                .getText().trim();
    }

    public String getFirstAgendaItemStartTime() {
        if (agendaItems.isEmpty()) return null;
        WebElement agendaItem = agendaItems.get(0);
        return agendaItem.findElement(By.xpath(".//span[contains(@class,'agenda-label') and text()='Start: ']/following-sibling::span[contains(@class,'agenda-value')]"))
                .getText().trim();
    }

    public String getFirstAgendaItemEndTime() {
        if (agendaItems.isEmpty()) return null;
        WebElement agendaItem = agendaItems.get(0);
        return agendaItem.findElement(By.xpath(".//span[contains(@class,'agenda-label') and text()='End: ']/following-sibling::span[contains(@class,'agenda-value')]"))
                .getText().trim();
    }

    public String getFirstAgendaItemLocation() {
        if (agendaItems.isEmpty()) return null;
        WebElement agendaItem = agendaItems.get(0);
        return agendaItem.findElement(By.xpath(".//span[contains(@class,'agenda-label') and text()='Location: ']/following-sibling::span[contains(@class,'agenda-value')]"))
                .getText().trim();
    }

    public void clickFirstAgendaItemEditButton() {
        if (!agendaItems.isEmpty()) {
            WebElement firstAgendaItem = agendaItems.get(0);
            WebElement editButton = firstAgendaItem.findElement(By.xpath(".//button[mat-icon[text()='create']]"));
            editButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[formcontrolname='startTime']")
            ));
        } else {
            throw new IllegalStateException("No agenda items to edit");
        }
    }

    public void clickFirstAgendaItemDeleteButton() {
        if (!agendaItems.isEmpty()) {
            WebElement firstAgendaItem = agendaItems.get(0);
            WebElement editButton = firstAgendaItem.findElement(By.xpath(".//button[mat-icon[text()='delete']]"));
            editButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button/span[contains(text(), 'Confirm')]")
            ));
        } else {
            throw new IllegalStateException("No agenda items to edit");
        }
    }

    public boolean isAgendaEmpty(){
        return agendaItems.isEmpty();
    }

}

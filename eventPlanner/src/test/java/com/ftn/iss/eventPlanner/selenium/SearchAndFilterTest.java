package com.ftn.iss.eventPlanner.selenium;

import com.ftn.iss.eventPlanner.selenium.page.FilterEventsPage;
import com.ftn.iss.eventPlanner.selenium.page.HomePagePage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SearchAndFilterTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:4200";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private HomePagePage homePage;
    private FilterEventsPage filterEventsPage;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.get(BASE_URL);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);

        homePage = new HomePagePage(driver);
        homePage.scrollToElement(homePage.getSearchEventInput());
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }

    @Test
    @Order(1)
    public void searchEventByName_DisplayOnlyEventsThatMatchTheQuery() throws InterruptedException {
        String query = "Music";

        homePage.searchEventByName(query);

        Thread.sleep(2000);

        Assertions.assertFalse(homePage.getAllEventCards().isEmpty(),
                "Expected some events to be shown.");

        Assertions.assertTrue(
                homePage.getAllEventCards().stream()
                        .allMatch(card -> card.getText().toLowerCase().contains("music")),
                "Some event titles do not match the search query 'Music'."
        );
    }

    @Test
    @Order(2)
    public void filterEventsByLocation_ShouldDisplayOnlyLondonEvents() throws InterruptedException {
        homePage.clickEventFilterButton();
        filterEventsPage = new FilterEventsPage(driver);

        filterEventsPage.setLocation("London");
        filterEventsPage.clickApply();
        Thread.sleep(3000);

        Assertions.assertFalse(homePage.getAllEventCards().isEmpty(),
                "Expected some events to be shown.");

        Assertions.assertTrue(
                homePage.getAllEventCards().stream()
                        .allMatch(card -> card.getText().contains("London")),
                "Some events are not located in London."
        );
    }

    @Test
    @Order(3)
    public void filterEventsByType_ShouldDisplayOnlyConferenceEvents() throws InterruptedException {
        homePage.clickEventFilterButton();
        filterEventsPage = new FilterEventsPage(driver);

        filterEventsPage.selectEventType("Conference");
        filterEventsPage.clickApply();
        Thread.sleep(3000);

        Assertions.assertFalse(homePage.getAllEventCards().isEmpty(),
                "Expected some events to be shown.");

        Assertions.assertTrue(
                homePage.getAllEventTypesFromCards().stream()
                        .allMatch(type -> type.equals("CONFERENCE")),
                "Some events are not of type 'Conference'."
        );
    }

    @Test
    @Order(4)
    public void filterByDateRange_ShouldDisplayOnlyEventsInRange() throws InterruptedException {
        homePage.clickEventFilterButton();
        filterEventsPage = new FilterEventsPage(driver);

        filterEventsPage.setDateRange("10/01/2025", "12/31/2025");
        filterEventsPage.clickApply();
        Thread.sleep(3000);

        Assertions.assertFalse(homePage.getAllEventCards().isEmpty(),
                "Expected some events to be shown.");

        LocalDate start = LocalDate.of(2025, 10, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        Assertions.assertTrue(
                homePage.areAllEventDatesInRange(start, end),
                "Some events are outside the selected date range."
        );
    }

    @Test
    @Order(5)
    public void filterByMinRating_ShouldDisplayHighRatedEvents() throws InterruptedException {
        homePage.clickEventFilterButton();
        filterEventsPage = new FilterEventsPage(driver);

        filterEventsPage.setMinRating(3.5);
        filterEventsPage.clickApply();
        Thread.sleep(2000);

        Assertions.assertFalse(homePage.getAllEventCards().isEmpty(),
                "Expected events with rating ≥ 3.5.");

        Assertions.assertTrue(
                homePage.areAllEventRatingsAbove(3.5),
                "Some events have rating below 3.5."
        );
    }


    @Test
    @Order(6)
    public void searchEventByName_NoResults() throws InterruptedException {
        String query = "NonexistentEventName123";

        homePage.searchEventByName(query);
        Thread.sleep(2000);

        Assertions.assertTrue(homePage.getAllEventCards().isEmpty(),
                "Expected no event cards for a nonexistent search query.");
    }

    @Test
    @Order(7)
    public void filterEvents_NoResults() throws InterruptedException {
        homePage.clickEventFilterButton();
        filterEventsPage = new FilterEventsPage(driver);

        filterEventsPage.selectEventType("Conference");
        filterEventsPage.setLocation("Mars");

        filterEventsPage.clickApply();
        Thread.sleep(3000);

        Assertions.assertTrue(homePage.getAllEventCards().isEmpty(),
                "Expected no event cards for this filter combination.");
    }

    @Test
    @Order(8)
    public void filterThenSearch_CombinedResults() throws InterruptedException {
        homePage.clickEventFilterButton();
        filterEventsPage = new FilterEventsPage(driver);

        filterEventsPage.selectEventType("Workshop");
        filterEventsPage.setDateRange("09/01/2025", "09/31/2025");
        filterEventsPage.clickApply();
        Thread.sleep(2000);

        homePage.searchEventByName("Photo");
        Thread.sleep(2000);

        Assertions.assertFalse(homePage.getAllEventCards().isEmpty(),
                "Expected filtered and searched events to be displayed.");

        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);
        Assertions.assertTrue(
                homePage.areAllEventDatesInRange(start, end),
                "Some events are outside the selected date range."
        );

        Assertions.assertTrue(
                homePage.getAllEventTypesFromCards().stream()
                        .allMatch(type -> type.equals("WORKSHOP")),
                "Some events are not of type 'Workshop'."
        );

        Assertions.assertTrue(
                homePage.getAllEventCards().stream().allMatch(card ->
                        card.getText().toLowerCase().contains("photo")),
                "Some events do not match the search term 'Photo'."
        );
    }

    @Test
    @Order(9)
    public void filterWithAllCriteria_ShouldReturnValidEvents() throws InterruptedException {
        homePage.clickEventFilterButton();
        filterEventsPage = new FilterEventsPage(driver);

        filterEventsPage.selectEventType("Conference");
        filterEventsPage.setLocation("London");
        filterEventsPage.setDateRange("10/01/2025", "11/31/2025");
        filterEventsPage.setMaxParticipants(300);
        filterEventsPage.setMinRating(4.0);
        filterEventsPage.clickApply();
        Thread.sleep(3000);

        Assertions.assertFalse(homePage.getAllEventCards().isEmpty(),
                "Expected at least one event matching all criteria.");

        // Provera lokacije
        Assertions.assertTrue(
                homePage.getAllEventCards().stream()
                        .allMatch(card -> card.getText().contains("London")),
                "Some events are not located in London."
        );

        // Provera datuma
        LocalDate start = LocalDate.of(2025, 10, 1);
        LocalDate end = LocalDate.of(2025, 11, 30);
        Assertions.assertTrue(
                homePage.areAllEventDatesInRange(start, end),
                "Some events are outside the selected date range."
        );

        Assertions.assertTrue(
                homePage.getAllEventTypesFromCards().stream()
                        .allMatch(type -> type.equals("CONFERENCE")),
                "Some events are not of type 'Conference'."
        );

        // Provera rejtinga
        Assertions.assertTrue(
                homePage.areAllEventRatingsAbove(4.0),
                "Some events have rating below 4.0."
        );
    }


}

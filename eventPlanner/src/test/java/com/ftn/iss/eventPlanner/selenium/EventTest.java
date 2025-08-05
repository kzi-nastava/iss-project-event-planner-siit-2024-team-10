package com.ftn.iss.eventPlanner.selenium;

import com.ftn.iss.eventPlanner.selenium.page.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";
    private LoginPage loginPage;
    private NavigationBarPage navigationBarPage;

    // Constants for test data
    private static final String LOGIN_EMAIL = "organizer@mail.com";
    private static final String LOGIN_PASSWORD = "password123";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final int UI_REFRESH_DELAY = 5000;
    private static final int SCROLL_DELAY = 1000;
    private static final int DATA_LOAD_DELAY = 2000;



    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.get(BASE_URL);
        wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        driver.manage().window().maximize();
        navigationBarPage = new NavigationBarPage(driver);
        navigationBarPage.openLoginPage();
        loginPage = new LoginPage(driver);
        loginPage.login(LOGIN_EMAIL, LOGIN_PASSWORD);
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void create_WithValidData_CreatesEvent() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);
        createEventPage.selectFirstEventType();
        createEventPage.fillForm(
                "Tech Conference 2025",    // name
                "A major international conference on emerging technologies.", // description
                150,                      // max participants
                "Serbia",                // country
                "Novi Sad",              // city
                "Bulevar oslobođenja",   // street
                "12B",                   // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        createEventPage.submitForm();
        wait.until(ExpectedConditions.urlContains("/event"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/event"),
                "Expected to be redirected to /events after creation, but URL was: " + currentUrl);

        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        verifyEventDetails(eventDetailsPage,
                "Tech Conference 2025",    // expected name
                "A major international conference on emerging technologies.", // expected description
                "Serbia",                // expected country
                "Novi Sad",              // expected city
                "Bulevar oslobođenja",   // expected street
                "12B",                   // expected house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("MM.dd.yyyy.")), // expected date
                true                     // expected publicity
        );

        Assertions.assertTrue(eventDetailsPage.hasEventType(),
                "Event should have an event type after creation with valid data");

    }

    @Test
    public void create_WithNoEventType_CreatesEventWithoutType() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);

        // Check the "No event types apply" checkbox instead of selecting a type
        createEventPage.noEventTypeCheckbox.click();

        createEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        createEventPage.submitForm();
        wait.until(ExpectedConditions.urlContains("/event"));

        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        verifyEventDetails(eventDetailsPage,
                "Unique Gathering",      // expected name
                "A one-of-a-kind event that doesn't fit standard categories.", // expected description
                "Serbia",                // expected country
                "Kragujevac",            // expected city
                "Svetozara Markovića",   // expected street
                "36",                    // expected house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("MM.dd.yyyy.")), // expected date
                true                     // expected publicity
        );

        Assertions.assertFalse(eventDetailsPage.hasEventType(),
                "Event should not have an event type after creation with 'No event types apply' checked");
    }

    @Test
    public void create_EmptyFields_DisablesSubmit() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);

        createEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        createEventPage.selectFirstEventType();

        // Verify submit button is enabled with all fields filled
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled when all required fields are filled.");

        // Clear name field
        createEventPage.clearInput(createEventPage.nameInput);
        createEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing name field.");

        // Restore name and clear description
        createEventPage.setName("Complete Test Event");
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring name.");

        createEventPage.clearInput(createEventPage.descriptionInput);
        createEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing description field.");

        // Restore description and clear country
        createEventPage.setDescription("Testing progressive form clearing");
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring description.");

        createEventPage.clearInput(createEventPage.countryInput);
        createEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing country field.");

        // Restore country and clear city
        createEventPage.setCountry("Serbia");
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring country.");

        createEventPage.clearInput(createEventPage.cityInput);
        createEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing city field.");

        // Restore city and clear street
        createEventPage.setCity("Belgrade");
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring city.");

        createEventPage.clearInput(createEventPage.streetInput);
        createEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing street field.");

        // Restore street and clear house number
        createEventPage.setStreet("Bulevar oslobođenja");
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring street.");

        createEventPage.clearInput(createEventPage.houseNumberInput);
        createEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing house number field.");

        // Restore house number and clear date
        createEventPage.setHouseNumber("12B");
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring house number.");

        createEventPage.clearInput(createEventPage.dateInput);
        createEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing date field.");

        // Restore date
        createEventPage.setDate(LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        createEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring date.");

    }

    @Test
    public void create_PastDate_DisablesSubmit() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);
        createEventPage.selectFirstEventType();
        createEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );

        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled with walid date.");

        createEventPage.clearInput(createEventPage.dateInput);
        createEventPage.setDate(LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should be disabled with past date.");
    }

    //helper method for creating event whose update will be tested
    private void createEvent(){
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);
        createEventPage.selectFirstEventType();
        createEventPage.fillForm(
                "Tech Conference 2025",    // name
                "A major international conference on emerging technologies.", // description
                150,                      // max participants
                "Serbia",                // country
                "Novi Sad",              // city
                "Bulevar oslobođenja",   // street
                "12B",                   // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        createEventPage.submitForm();
        wait.until(ExpectedConditions.urlContains("/event"));
    }

    //helper method for verifying event details
    private void verifyEventDetails(EventDetailsPage eventDetailsPage,
                                    String expectedName,
                                    String expectedDescription,
                                    String expectedCountry,
                                    String expectedCity,
                                    String expectedStreet,
                                    String expectedHouseNumber,
                                    String expectedDate,
                                    boolean expectedPublicity) {

        assertEquals(expectedName, eventDetailsPage.getEventName(),"Event name should match");
        assertEquals(expectedDescription, eventDetailsPage.getEventDescription(),"Event description should match");
        assertEquals(expectedCountry, eventDetailsPage.getCountry(), "Country should match");
        assertEquals(expectedCity, eventDetailsPage.getCity(), "City should match");
        assertEquals(expectedStreet, eventDetailsPage.getStreet(), "Street should match");
        assertEquals(expectedHouseNumber, eventDetailsPage.getHouseNumber(), "House number should match");
        assertEquals(expectedDate, eventDetailsPage.getEventDate(), "Event date should match");
        assertEquals(expectedPublicity, eventDetailsPage.isPublic(), "Publicity status should match");
    }

    @Test
    public void updateEvent_WithValidData_UpdatesEvent() {
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.openUpdateEventPage();
        EditEventPage editEventPage = new EditEventPage(driver);

        editEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        editEventPage.submitForm();
        wait.until(ExpectedConditions.urlContains("/event"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/event"),
                "Expected to be redirected to /events after creation, but URL was: " + currentUrl);

        eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();

        verifyEventDetails(eventDetailsPage,
                "Unique Gathering",      // expected name
                "A one-of-a-kind event that doesn't fit standard categories.", // expected description
                "Serbia",                // expected country
                "Kragujevac",            // expected city
                "Svetozara Markovića",   // expected street
                "36",                    // expected house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("MM.dd.yyyy.")), // expected date
                true                     // expected publicity
        );
    }
    @Test
    public void updateEvent_WithoutEventType_UpdatesEvent() {
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.openUpdateEventPage();
        EditEventPage editEventPage = new EditEventPage(driver);
        editEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        editEventPage.noEventTypeCheckbox.click();
        editEventPage.submitForm();
        wait.until(ExpectedConditions.urlContains("/event"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/event"),
                "Expected to be redirected to /events after creation, but URL was: " + currentUrl);

        eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();



        assertFalse(eventDetailsPage.hasEventType(),
                "Event should not have an event type after update without selecting one");

        verifyEventDetails(eventDetailsPage,
                "Unique Gathering",      // expected name
                "A one-of-a-kind event that doesn't fit standard categories.", // expected description
                "Serbia",                // expected country
                "Kragujevac",            // expected city
                "Svetozara Markovića",   // expected street
                "36",                    // expected house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("MM.dd.yyyy.")), // expected date
                true                     // expected publicity
        );
    }

    @Test
    public void edit_EmptyFields_DisablesSubmit() {
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.openUpdateEventPage();
        EditEventPage editEventPage = new EditEventPage(driver);

        // Fill the form with valid data
        editEventPage.fillForm(
                "Unique Gathering",
                "A one-of-a-kind event that doesn't fit standard categories.",
                50,
                "Serbia",
                "Kragujevac",
                "Svetozara Markovića",
                "36",
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                true
        );

        // Verify submit button is enabled
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled when all required fields are filled.");

        // Clear name field
        editEventPage.clearInput(editEventPage.nameInput);
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing name field.");

        // Restore name and clear description
        editEventPage.setName("Updated Test Event");
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring name.");

        editEventPage.clearInput(editEventPage.descriptionInput);
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing description field.");

        // Restore description and clear country
        editEventPage.setDescription("Updated description for edit validation.");
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring description.");

        editEventPage.clearInput(editEventPage.countryInput);
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing country field.");

        // Restore country and clear city
        editEventPage.setCountry("Serbia");
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring country.");

        editEventPage.clearInput(editEventPage.cityInput);
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing city field.");

        // Restore city and clear street
        editEventPage.setCity("Belgrade");
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring city.");

        editEventPage.clearInput(editEventPage.streetInput);
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing street field.");

        // Restore street and clear house number
        editEventPage.setStreet("Nemanjina");
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring street.");

        editEventPage.clearInput(editEventPage.houseNumberInput);
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing house number field.");

        // Restore house number and clear date
        editEventPage.setHouseNumber("14A");
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring house number.");

        editEventPage.clearInput(editEventPage.dateInput);
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing date field.");

        // Restore date
        editEventPage.setDate(LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring date.");
    }

    @Test
    public void edit_PastDate_DisablesSubmit() {
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.openUpdateEventPage();
        EditEventPage editEventPage = new EditEventPage(driver);

        // Fill the form with valid data
        editEventPage.fillForm(
                "Unique Gathering",
                "A one-of-a-kind event that doesn't fit standard categories.",
                50,
                "Serbia",
                "Kragujevac",
                "Svetozara Markovića",
                "36",
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                true
        );

        // Verify submit button is enabled
        editEventPage.waitForSubmitButtonEnabled(true);
        Assertions.assertTrue(editEventPage.submitButton.isEnabled(),
                "Submit button should be enabled when all required fields are filled.");

        // Clear date field and set past date
        editEventPage.clearInput(editEventPage.dateInput);
        editEventPage.setDate(LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        editEventPage.waitForSubmitButtonEnabled(false);
        Assertions.assertFalse(editEventPage.submitButton.isEnabled(),
                "Submit button should be disabled when date is set to a past date.");
    }

    @Test
    public void delete_Confirmed_DeletesEvent(){
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();

        // Click the delete button
        eventDetailsPage.clickDeleteEventButton();
        eventDetailsPage.confirmDialog();

        // Wait for the page to redirect after deletion
        wait.until(ExpectedConditions.urlContains("/home"));

        // Verify we are back on the events list page
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/home"),
                "Expected to be redirected to /home after deletion, but URL was: " + currentUrl);
    }

    @Test
    public void delete_Canceled_DoesntDeleteEvent(){
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();

        // Click the delete button
        eventDetailsPage.clickDeleteEventButton();
        eventDetailsPage.cancelDialog();

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/event"),
                "Expected to remain on the event details page after cancellation, but URL was: " + currentUrl);
    }

    @Test
    public void createAgendaItem_WithValidData_CreatesAgendaItem() {
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.dismissSnackbarIfPresent();
        eventDetailsPage.clickAddAgendaItemButton();
        AgendaDialogPage agendaDialogPage = new AgendaDialogPage(driver);

        agendaDialogPage.fillForm(
                "Opening Ceremony", // name
                "Welcome speech and introduction to the conference.", // description
                "09:00AM", // start time
                "10:00AM", // end time
                "Main Hall" // location
        );

        agendaDialogPage.clickSave();
        eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();

        verifyAgendaItem(eventDetailsPage,
                "Opening Ceremony", // expected name
                "Welcome speech and introduction to the conference.", // expected description
                "09:00:00", // expected start time
                "10:00:00", // expected end time
                "Main Hall" // expected location
        );
    }

    @Test
    public void createAgendaItem_EmptyFields_DisablesSubmit() {
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.clickAddAgendaItemButton();
        AgendaDialogPage agendaDialogPage = new AgendaDialogPage(driver);

        // Fill with valid data
        agendaDialogPage.fillForm(
                "Opening Ceremony",
                "Welcome speech and introduction to the conference.",
                "09:00AM",
                "10:00AM",
                "Main Hall"
        );

        // Wait until save button enabled
        agendaDialogPage.waitForSaveButtonEnabled(true);
        Assertions.assertTrue(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be enabled when all required fields are filled.");

        // Clear name field and check save button disabled
        agendaDialogPage.clearInput(agendaDialogPage.nameInput);
        agendaDialogPage.waitForSaveButtonEnabled(false);
        Assertions.assertFalse(agendaDialogPage.saveButton.isEnabled(),
                "Save button should become disabled after clearing name field.");

        // Restore name and clear description
        agendaDialogPage.setName("Opening Ceremony");
        agendaDialogPage.waitForSaveButtonEnabled(true);
        Assertions.assertTrue(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be enabled again after restoring name.");

        agendaDialogPage.clearInput(agendaDialogPage.descriptionInput);
        agendaDialogPage.waitForSaveButtonEnabled(false);
        Assertions.assertFalse(agendaDialogPage.saveButton.isEnabled(),
                "Save button should become disabled after clearing description field.");

        // Restore description and clear start time
        agendaDialogPage.setDescription("Welcome speech and introduction to the conference.");
        agendaDialogPage.waitForSaveButtonEnabled(true);
        Assertions.assertTrue(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be enabled again after restoring description.");

        agendaDialogPage.clearTimeInput(agendaDialogPage.startTimeInput);
        agendaDialogPage.waitForSaveButtonEnabled(false);
        Assertions.assertFalse(agendaDialogPage.saveButton.isEnabled(),
                "Save button should become disabled after clearing start time field.");

        // Restore start time and clear end time
        agendaDialogPage.setStartTime("09:00AM");
        agendaDialogPage.waitForSaveButtonEnabled(true);
        Assertions.assertTrue(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be enabled again after restoring start time.");

        agendaDialogPage.clearTimeInput(agendaDialogPage.endTimeInput);
        agendaDialogPage.waitForSaveButtonEnabled(false);
        Assertions.assertFalse(agendaDialogPage.saveButton.isEnabled(),
                "Save button should become disabled after clearing end time field.");

        // Restore end time and clear location
        agendaDialogPage.setEndTime("10:00AM");
        agendaDialogPage.waitForSaveButtonEnabled(true);
        Assertions.assertTrue(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be enabled again after restoring end time.");

        agendaDialogPage.clearInput(agendaDialogPage.locationInput);
        agendaDialogPage.waitForSaveButtonEnabled(false);
        Assertions.assertFalse(agendaDialogPage.saveButton.isEnabled(),
                "Save button should become disabled after clearing location field.");

        // Restore location
        agendaDialogPage.setLocation("Main Hall");
        agendaDialogPage.waitForSaveButtonEnabled(true);
        Assertions.assertTrue(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be enabled again after restoring location.");
    }

    @Test
    public void createAgendaItem_StartTimeAfterEndTime_DisablesSubmit() {
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.clickAddAgendaItemButton();
        AgendaDialogPage agendaDialogPage = new AgendaDialogPage(driver);

        // Fill with valid data
        agendaDialogPage.fillForm(
                "Opening Ceremony",
                "Welcome speech and introduction to the conference.",
                "09:00AM",
                "10:00AM",
                "Main Hall"
        );

        // Verify save button is enabled
        agendaDialogPage.waitForSaveButtonEnabled(true);
        Assertions.assertTrue(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be enabled when all required fields are filled.");

        agendaDialogPage.clearTimeInput(agendaDialogPage.startTimeInput);
        agendaDialogPage.setStartTime("11:00AM");
        agendaDialogPage.waitForSaveButtonEnabled(false);
        Assertions.assertFalse(agendaDialogPage.saveButton.isEnabled(),
                "Save button should be disabled when start time is after end time.");
    }

    public void verifyAgendaItem(EventDetailsPage eventDetailsPage,
                                  String expectedName,
                                  String expectedDescription,
                                  String expectedStartTime,
                                  String expectedEndTime,
                                  String expectedLocation) {
        assertEquals(expectedName, eventDetailsPage.getFirstAgendaItemName(),
                "Agenda item name should match");
        assertEquals(expectedDescription, eventDetailsPage.getFirstAgendaItemDescription(),
                "Agenda item description should match");
        assertEquals(expectedStartTime, eventDetailsPage.getFirstAgendaItemStartTime(),
                "Agenda item start time should match");
        assertEquals(expectedEndTime, eventDetailsPage.getFirstAgendaItemEndTime(),
                "Agenda item end time should match");
        assertEquals(expectedLocation, eventDetailsPage.getFirstAgendaItemLocation(),
                "Agenda item location should match");
    }

    private void createEventWithAgendaItem(){
        createEvent();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.dismissSnackbarIfPresent();
        eventDetailsPage.clickAddAgendaItemButton();
        AgendaDialogPage agendaDialogPage = new AgendaDialogPage(driver);

        agendaDialogPage.fillForm(
                "Opening Ceremony", // name
                "Welcome speech and introduction to the conference.", // description
                "09:00AM", // start time
                "10:00AM", // end time
                "Main Hall" // location
        );

        agendaDialogPage.clickSave();
        agendaDialogPage.dismissSnackbarIfPresent();
        eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
    }

    @Test
    public void editAgendaItem_WithValidData_UpdatesAgendaItem() {
        createEventWithAgendaItem();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.clickFirstAgendaItemEditButton();
        AgendaDialogPage agendaDialogPage = new AgendaDialogPage(driver);

        agendaDialogPage.fillForm(
                "Opening Ceremony Updated", // name
                "Updated welcome speech and introduction to the conference.", // description
                "09:30AM", // start time
                "10:30AM", // end time
                "Main Hall Updated" // location
        );

        agendaDialogPage.clickSave();
        eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();

        verifyAgendaItem(eventDetailsPage,
                "Opening Ceremony Updated", // expected name
                "Updated welcome speech and introduction to the conference.", // expected description
                "09:30:00", // expected start time
                "10:30:00", // expected end time
                "Main Hall Updated" // expected location
        );
    }

    @Test
    public void deleteAgendaItem(){
        createEventWithAgendaItem();
        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        eventDetailsPage.clickFirstAgendaItemDeleteButton();
        eventDetailsPage.confirmDialog();

        eventDetailsPage=new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        assertTrue(eventDetailsPage.isAgendaEmpty(),
                "Agenda should be empty after deleting the only agenda item.");
    }
}

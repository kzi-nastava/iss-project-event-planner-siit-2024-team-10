package com.ftn.iss.eventPlanner.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BudgetTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";

    // Constants for test data
    private static final String LOGIN_EMAIL = "organizer@mail.com";
    private static final String LOGIN_PASSWORD = "password123";
    private static final String BUDGET_AMOUNT = "500";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final int UI_REFRESH_DELAY = 5000;
    private static final int SCROLL_DELAY = 1000;
    private static final int DATA_LOAD_DELAY = 2000;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        driver.manage().window().maximize();
        loginAsOrganizer();
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ==================== HELPER METHODS ====================

    private void loginAsOrganizer() {
        driver.get(BASE_URL + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[formcontrolname='email']")));
        emailInput.sendKeys(LOGIN_EMAIL);

        WebElement passwordInput = driver.findElement(
                By.cssSelector("input[formcontrolname='password']"));
        passwordInput.sendKeys(LOGIN_PASSWORD);

        WebElement loginButton = driver.findElement(
                By.cssSelector("button.login-button"));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/home"));
    }

    private void selectFirstEvent() {
        WebElement eventSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select")));
        eventSelect.click();

        WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-option")));
        firstOption.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("table.budget-table")));
    }

    private void selectEventByIndex(int index) {
        WebElement eventSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        eventSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertTrue(options.size() > index,
                "Not enough event options available to select index " + index);
        options.get(index).click();
    }

    private void waitForSnackbarWithText(String expectedText) {
        WebElement snackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., \"" + expectedText + "\")]")));
        Assertions.assertNotNull(snackBar, "Expected snackbar with text '" + expectedText + "' was not shown.");
    }


    private void clickBookNowButton() {
        WebElement bookNowBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.book-now-btn")));
        bookNowBtn.click();
    }

    private void waitForServiceBookingDialog() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(), 'Book a Service')]")));
    }

    private void waitForEventSelectionDialog() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Select Event')]")));
    }

    private void fillServiceTimeInputs(String startTime, String endTime) {
        WebElement startTimeInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[formcontrolname='startTime']")));
        startTimeInput.clear();
        startTimeInput.sendKeys(startTime);

        List<WebElement> endTimeInputs = driver.findElements(
                By.cssSelector("input[formcontrolname='endTime']"));
        if (!endTimeInputs.isEmpty() && endTime != null) {
            WebElement endTimeInput = endTimeInputs.get(0);
            endTimeInput.clear();
            endTimeInput.sendKeys(endTime);
        }
    }

    private void confirmServiceBooking() {
        WebElement bookBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("book-button")));
        bookBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Confirmation')]")));

        WebElement finalConfirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("confirm-button")));
        finalConfirmBtn.click();
    }

    private void confirmProductPurchase() {
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[contains(text(), 'Confirm')]]")));
        confirmBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Confirmation')]")));

        WebElement finalConfirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("confirm-button")));
        finalConfirmBtn.click();
    }

    private void navigateToBudgetPage() {
        driver.get(BASE_URL + "/budget");
    }

    private WebElement selectEventInBudgetPage(int eventIndex) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".budget-container")));

        WebElement eventSelectBudget = findBudgetEventSelect();
        eventSelectBudget.click();

        List<WebElement> budgetOptions = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertTrue(budgetOptions.size() > eventIndex,
                "Not enough event options available in budget page");

        budgetOptions.get(eventIndex).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("table.budget-table")));

        return eventSelectBudget;
    }

    private WebElement findBudgetEventSelect() {
        WebElement eventSelectBudget = null;
        try {
            eventSelectBudget = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("mat-select.custom-select")));
        } catch (TimeoutException e1) {
            try {
                eventSelectBudget = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("mat-form-field.event-selector mat-select")));
            } catch (TimeoutException e2) {
                eventSelectBudget = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("mat-select")));
            }
        }
        return eventSelectBudget;
    }

    private boolean findBudgetItemInTable(String categoryKeyword, String offeringKeyword) {
        waitForDataLoad();

        List<WebElement> rows = driver.findElements(By.cssSelector("table.budget-table tr.data-row"));

        if (rows.isEmpty()) {
            WebElement emptyState = driver.findElement(By.cssSelector(".empty-state"));
            if (emptyState != null && emptyState.isDisplayed()) {
                System.out.println("Budget table is empty - no budget items found");
                return false;
            }
        }

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            if (cells.size() >= 3) {
                String category = cells.get(0).getText().trim().toLowerCase();
                String offering = cells.get(2).getText().trim().toLowerCase();

                System.out.println("Category: " + category + ", Offering: " + offering);

                if (category.contains(categoryKeyword.toLowerCase()) &&
                        offering.contains(offeringKeyword.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void waitForDataLoad() {
        try {
            Thread.sleep(DATA_LOAD_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitForUIRefresh() {
        try {
            Thread.sleep(UI_REFRESH_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try {
            Thread.sleep(SCROLL_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================== TEST METHODS ====================

    @Test
    @Order(1)
    public void testAddNewBudgetItem() {
        navigateToBudgetPage();
        selectFirstEvent();

        // Click Add Budget Item button
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.add-button")));
        addButton.click();

        // Wait for dialog and select category
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("mat-dialog-container")));

        WebElement categorySelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='category']")));
        categorySelect.click();

        List<WebElement> categoryOptions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(categoryOptions.isEmpty(), "Category options should not be empty");
        categoryOptions.get(0).click();

        // Enter amount
        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[formcontrolname='amount']")));
        amountInput.clear();
        amountInput.sendKeys(BUDGET_AMOUNT);

        // Add item
        WebElement addDialogButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-dialog-actions[@id='add']//button[normalize-space()='Add' and not(@disabled)]")));
        addDialogButton.click();

        wait.until(ExpectedConditions.invisibilityOf(dialog));
        waitForUIRefresh();

        // Verify item was added
        List<WebElement> amounts = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("td.amount-cell input.currency-input")));
        boolean found = amounts.stream().anyMatch(e -> e.getAttribute("value").equals(BUDGET_AMOUNT));
        Assertions.assertTrue(found, "New budget item with amount " + BUDGET_AMOUNT + " should be visible in the table");
    }

    @Test
    @Order(2)
    public void testDeleteBudgetItemFailureDueToReservedOfferings() {
        navigateToBudgetPage();
        selectFirstEvent();

        WebElement firstDeleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(1) button.delete-btn")));
        firstDeleteBtn.click();

        WebElement snackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("simple-snack-bar")));
        String message = snackBar.getText();
        Assertions.assertTrue(message.contains("has not been deleted"));
    }

    @Test
    @Order(3)
    public void testDeleteBudgetItem() {
        navigateToBudgetPage();
        selectFirstEvent();

        WebElement secondDeleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(2) button.delete-btn")));
        secondDeleteBtn.click();

        WebElement snackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("simple-snack-bar")));
        String message = snackBar.getText();
        Assertions.assertTrue(message.contains("Budget item deleted"));
    }

    @Test
    @Order(4)
    public void testReserveServiceWithCategoryNotInPlannedBudget_Manual() {
        driver.get(BASE_URL + "/offering/12");
        clickBookNowButton();
        waitForServiceBookingDialog();

        selectEventByIndex(2);
        fillServiceTimeInputs("1200PM", "0400PM");
        confirmServiceBooking();

        waitForSnackbarWithText("Reservation request is pending! Email confirmation will been sent.");
    }

    @Test
    @Order(5)
    public void testReserveServiceWithCategoryNotInPlannedBudget_Autoconfirm() {
        driver.get(BASE_URL + "/offering/13");
        clickBookNowButton();
        waitForServiceBookingDialog();

        selectEventByIndex(2);
        fillServiceTimeInputs("1200AM", "0300AM");
        confirmServiceBooking();

        waitForSnackbarWithText("Reservation successful! Budget updated. Email confirmation has been sent.");

        // Verify budget update
        navigateToBudgetPage();
        selectEventInBudgetPage(2);

        boolean matchFound = findBudgetItemInTable("electronics", "dj service");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'dj service'.");
        scrollToBottom();
    }

    @Test
    @Order(6)
    public void testBuyProductWithCategoryNotInPlannedBudget() {
        driver.get(BASE_URL + "/offering/10");
        clickBookNowButton();
        waitForEventSelectionDialog();

        // Select event with double-click workaround
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();
        matSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available to select.");
        options.get(0).click();

        confirmProductPurchase();
        waitForSnackbarWithText("Product reserved successfully!");

        // Verify budget update
        navigateToBudgetPage();
        selectFirstEvent();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("table.budget-table tr.data-row")));

        boolean matchFound = findBudgetItemInTable("nova kategorija", "funeral memorial kit");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'Funeral Memorial Kit'.");
        scrollToBottom();
    }

    @Test
    @Order(7)
    public void testBuyProductWithCategoryInPlannedBudget() {
        driver.get(BASE_URL + "/offering/5");
        clickBookNowButton();
        waitForEventSelectionDialog();

        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();
        matSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available to select.");
        options.get(0).click();

        confirmProductPurchase();
        waitForSnackbarWithText("Product reserved successfully!");

        // Verify budget update
        navigateToBudgetPage();
        selectFirstEvent();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("table.budget-table tr.data-row")));

        boolean matchFound = findBudgetItemInTable("electronics", "table linens");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'table linens'.");
        scrollToBottom();
    }

    @Test
    @Order(8)
    public void testBuyAlreadyPurchasedProduct() {
        driver.get(BASE_URL + "/offering/10");
        clickBookNowButton();
        waitForEventSelectionDialog();

        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available.");
        options.get(0).click();

        confirmProductPurchase();
        waitForSnackbarWithText("This product has already been purchased.");
    }

    @Test
    @Order(9)
    public void testReserveServicePriceOutOfBudget() {
        driver.get(BASE_URL + "/offering/14");
        clickBookNowButton();
        waitForServiceBookingDialog();

        selectEventByIndex(2);
        fillServiceTimeInputs("0600PM", "1000PM");
        confirmServiceBooking();

        waitForSnackbarWithText("Not enough budget to record the purchase.");
    }

    @Test
    @Order(10)
    public void testBuyProductWithPriceOutOfBudget() {
        driver.get(BASE_URL + "/offering/3");
        clickBookNowButton();
        waitForEventSelectionDialog();

        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available.");
        options.get(0).click();

        confirmProductPurchase();
        waitForSnackbarWithText("Insufficient budget for this purchase.");
    }

    @Test
    @Order(11)
    public void testBuyNotAvailableProduct() {
        driver.get(BASE_URL + "/offering/8");

        WebElement bookNowBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("button.book-now-btn")));

        boolean isDisabled = !bookNowBtn.isEnabled();
        Assertions.assertTrue(isDisabled, "Book Now button should be disabled for offering 8.");
    }

    @Test
    @Order(12)
    public void testUpdateToSmallAmount() {
        navigateToBudgetPage();
        selectFirstEvent();

        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(1) input.currency-input")));

        amountInput.clear();
        amountInput.sendKeys("0");
        driver.findElement(By.cssSelector("body")).click(); // trigger blur

        waitForSnackbarWithText("Failed to update amount");
    }

    @Test
    @Order(13)
    public void testUpdateToBigAmount() {
        navigateToBudgetPage();
        selectFirstEvent();

        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(1) input.currency-input")));

        amountInput.clear();
        amountInput.sendKeys("160000");
        amountInput.sendKeys(Keys.TAB);

        WebElement successSnackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("simple-snack-bar")));
        String successMessage = successSnackBar.getText();
        Assertions.assertTrue(successMessage.toLowerCase().contains("updated"),
                "Expected success message on valid amount");
    }

    @Test
    @Order(14)
    public void testReserveServiceWithCategoryInPlannedBudget_Autoconfirm() {
        driver.get(BASE_URL + "/offering/19");
        clickBookNowButton();
        waitForServiceBookingDialog();

        selectEventByIndex(0);
        fillServiceTimeInputs("1200AM", "0200AM");
        confirmServiceBooking();

        waitForSnackbarWithText("Reservation successful! Budget updated. Email confirmation has been sent.");

        // Verify budget update
        navigateToBudgetPage();
        selectEventInBudgetPage(0);

        boolean matchFound = findBudgetItemInTable("nova kategorija", "party balloon setup");
        Assertions.assertTrue(matchFound, "No row found with category 'Nova kategorina' and offering 'Party Balloon Setup'.");
        scrollToBottom();
    }

    @Test
    @Order(15)
    public void testReserveAlreadyReservedService() {
        driver.get(BASE_URL + "/offering/19");
        clickBookNowButton();
        waitForServiceBookingDialog();

        selectEventByIndex(0);
        fillServiceTimeInputs("1200AM", "0300AM");
        confirmServiceBooking();

        waitForSnackbarWithText("You've already made a reservation for selected event.");
    }
}
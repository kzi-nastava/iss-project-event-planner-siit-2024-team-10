package com.ftn.iss.eventPlanner.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BudgetTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(BASE_URL + "/login");

        // Log in before each test
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[formcontrolname='email']")));
        emailInput.sendKeys("organizer@mail.com");

        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        passwordInput.sendKeys("password123");

        WebElement loginButton = driver.findElement(By.cssSelector("button.login-button"));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/home"));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void testAddNewBudgetItem() {
        driver.get(BASE_URL + "/budget");

        // Select event from the dropdown (mat-select for event)
        WebElement eventSelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("mat-select")));
        eventSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "Event list should not be empty");
        options.get(0).click();

        // Click on "Add Budget Item" button
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.add-button")));
        addButton.click();

        // Wait for the dialog to appear
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-dialog-container")));

        // Select category in the dialog
        WebElement categorySelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("mat-select[formcontrolname='category']")));
        categorySelect.click();

        List<WebElement> categoryOptions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("mat-option")));
        Assertions.assertFalse(categoryOptions.isEmpty(), "Category options should not be empty");
        categoryOptions.get(0).click();

        // Enter amount
        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[formcontrolname='amount']")));
        amountInput.clear();
        amountInput.sendKeys("500");

        // Click Add button in the dialog
        WebElement addDialogButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-dialog-actions[@id='add']//button[normalize-space()='Add' and not(@disabled)]")
        ));
        addDialogButton.click();

        // Wait for the dialog to disappear
        wait.until(ExpectedConditions.invisibilityOf(dialog));

        // (Optional) short sleep for UI to asynchronously refresh the table
        try {
            Thread.sleep(5000); // Necessary due to asynchronous UI updates
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the new item with amount 500 is visible in the table
        List<WebElement> amounts = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("td.amount-cell input.currency-input")));
        boolean found = amounts.stream().anyMatch(e -> e.getAttribute("value").equals("500"));
        Assertions.assertTrue(found, "New budget item with amount 500 should be visible in the table");
    }

    @Test
    public void testReserveServiceWithCategoryNotInPlannedBudget() {
        driver.get(BASE_URL + "/offering/10");

        // Klikni na Book Now
        WebElement bookNowBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.book-now-btn")));
        bookNowBtn.click();

        // Sačekaj dijalog Book a Service
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(), 'Book a Service')]")));

        // Otvori mat-select za event
        WebElement eventSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        eventSelect.click();

        // Sačekaj sve opcije
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertTrue(options.size() >= 3, "Nema dovoljno event opcija da se izabere treći.");

        // Klikni treću opciju
        options.get(2).click();

        // Popuni vreme — startTime = 00:00
        WebElement startTimeInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[formcontrolname='startTime']")));
        startTimeInput.clear();
        startTimeInput.sendKeys("00:00");

        // Ako postoji endTime input (kada minDuration != maxDuration)
        List<WebElement> endTimeInputs = driver.findElements(
                By.cssSelector("input[formcontrolname='endTime']"));

        if (!endTimeInputs.isEmpty()) {
            WebElement endTimeInput = endTimeInputs.get(0);
            endTimeInput.clear();
            endTimeInput.sendKeys("01:00");
        }

        // Klikni Book dugme
        WebElement bookBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Book') and not(@disabled)]")));
        bookBtn.click();

        // Sačekaj snackbar
        WebElement successSnackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., 'Product reserved successfully')]")));

        Assertions.assertNotNull(successSnackbar, "Reservation confirmation snackbar not shown.");
    }



    @Test
    public void testBuyProductWithCategoryNotInPlannedBudget() {
        driver.get(BASE_URL + "/offering/10");

        // Click on "Book Now"
        WebElement bookNowBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.book-now-btn")));
        bookNowBtn.click();

        // Wait for the "Select Event" dialog to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Select Event')]")));

        // Click 1: trigger dropdown
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();

        // Click 2: open options again
        matSelect.click();

        // Wait for options to appear
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available to select.");
        options.get(0).click();

        // Click the first Confirm button (from Select Event dialog)
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[contains(text(), 'Confirm')]]")));
        confirmBtn.click();

        // Wait for the Confirmation dialog to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Confirmation')]")));

        // Final confirm click
        WebElement finalConfirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("confirm-button")));
        finalConfirmBtn.click();

        // Wait for snackbar confirming successful reservation
        WebElement successSnackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., 'Product reserved successfully!')]")));
        Assertions.assertNotNull(successSnackbar, "Success snackbar not shown after reservation.");

        // Navigate to /budget
        driver.get(BASE_URL + "/budget");
        selectFirstEvent();

        // Wait for table to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("table.budget-table tr.data-row")));

        // Check if the reserved product is listed in the table
        List<WebElement> rows = driver.findElements(By.cssSelector("table.budget-table tr.data-row"));
        boolean matchFound = false;

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));

            if (cells.size() >= 3) {
                String category = cells.get(0).getText().trim().toLowerCase();
                String offering = cells.get(2).getText().trim().toLowerCase();

                System.out.println("Category: " + category + ", Offering: " + offering);

                if (category.contains("nova kategorija") && offering.contains("funeral memorial kit")) {
                    matchFound = true;
                    break;
                }
            }
        }

        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'Funeral Memorial Kit'.");

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        try {
            Thread.sleep(1000); // Ensure page scrolls completely and UI renders any dynamic content
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    @Test
    public void testBuyProductWithCategoryInPlannedBudget() {
        driver.get(BASE_URL + "/offering/5");

        // Click on "Book Now"
        WebElement bookNowBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.book-now-btn")));
        bookNowBtn.click();

        // Wait for the "Select Event" dialog to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Select Event')]")));

        // Click 1: trigger dropdown
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();

        // Click 2: open options again
        matSelect.click();

        // Wait for options to appear
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available to select.");
        options.get(0).click();

        // Click the first Confirm button (from Select Event dialog)
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[contains(text(), 'Confirm')]]")));
        confirmBtn.click();

        // Wait for the Confirmation dialog to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Confirmation')]")));

        // Final confirm click
        WebElement finalConfirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("confirm-button")));
        finalConfirmBtn.click();

        // Wait for snackbar confirming successful reservation
        WebElement successSnackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., 'Product reserved successfully!')]")));
        Assertions.assertNotNull(successSnackbar, "Success snackbar not shown after reservation.");

        // Navigate to /budget
        driver.get(BASE_URL + "/budget");
        selectFirstEvent();

        // Wait for table to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("table.budget-table tr.data-row")));

        // Check if the reserved product is listed in the table
        List<WebElement> rows = driver.findElements(By.cssSelector("table.budget-table tr.data-row"));
        boolean matchFound = false;

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));

            if (cells.size() >= 3) {
                String category = cells.get(0).getText().trim().toLowerCase();
                String offering = cells.get(2).getText().trim().toLowerCase();

                System.out.println("Category: " + category + ", Offering: " + offering);

                if (category.contains("electronics") && offering.contains("table linens")) {
                    matchFound = true;
                    break;
                }
            }
        }

        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'table linens'.");

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        try {
            Thread.sleep(1000); // Ensure page scrolls completely and UI renders any dynamic content
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testBuyAlreadyPurchasedProduct() {
        driver.get(BASE_URL + "/offering/10");

        // Click on Book Now
        WebElement bookNowBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.book-now-btn")));
        bookNowBtn.click();

        // Wait for "Select Event" dialog
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Select Event')]")));

        // Select event from dropdown
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available.");
        options.get(0).click();

        // Click Confirm in event selection dialog
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[contains(text(), 'Confirm')]]")));
        confirmBtn.click();

        // Wait for confirmation dialog
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Confirmation')]")));

        // Final confirm button
        WebElement finalConfirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("confirm-button")));
        finalConfirmBtn.click();

        // Wait for snackbar with expected message that product is already purchased
        WebElement errorSnackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., 'This product has already been purchased.')]")));

        Assertions.assertNotNull(errorSnackbar, "Expected snackbar with 'Product not purchased' was not shown.");
    }


    @Test
    public void testReserveServiceWithPriceHigherThanPlannedBudget() {
        // Test reservation of a service with price higher than planned budget
    }

    @Test
    public void testBuyProductWithPriceOutOfBudget() {
        driver.get(BASE_URL + "/offering/3");

        // Click on Book Now
        WebElement bookNowBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.book-now-btn")));
        bookNowBtn.click();

        // Wait for "Select Event" dialog
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Select Event')]")));

        // Select event from dropdown
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("mat-select[formcontrolname='event']")));
        matSelect.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(options.isEmpty(), "No event options available.");
        options.get(0).click();

        // Click Confirm in event selection dialog
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[contains(text(), 'Confirm')]]")));
        confirmBtn.click();

        // Wait for confirmation dialog
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Confirmation')]")));

        // Final confirm button
        WebElement finalConfirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("confirm-button")));
        finalConfirmBtn.click();

        // Wait for snackbar with expected message that product is already purchased
        WebElement errorSnackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., 'Insufficient budget for this purchase.')]")));

        Assertions.assertNotNull(errorSnackbar, "Expected snackbar with 'Product not purchased' was not shown.");
    }
    @Test
    public void testUpdateAmountFirstBudgetItem_failThenPass() {
        driver.get(BASE_URL + "/budget");
        selectFirstEvent();

        // Find the amount input in the first row
        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(1) input.currency-input")));

        // 1. Try invalid value (0) - expect error
        amountInput.clear();
        amountInput.sendKeys("0");
        driver.findElement(By.cssSelector("body")).click(); // trigger blur and update

        // Wait for exact snackbar error message
        WebElement errorSnackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., 'Failed to update amount')]")));

        Assertions.assertNotNull(errorSnackBar, "Expected failure snackbar was not shown.");
    }

    @Test
    public void testUpdateAmountFirstBudgetItem_failThengPass() {
        driver.get(BASE_URL + "/budget");
        selectFirstEvent();

        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(1) input.currency-input")));

        amountInput.clear();
        amountInput.sendKeys("1600");
        amountInput.sendKeys(Keys.TAB);

        WebElement successSnackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("simple-snack-bar")));
        String successMessage = successSnackBar.getText();
        Assertions.assertTrue(successMessage.toLowerCase().contains("updated"),
                "Expected success message on valid amount");
    }

    @Test
    public void testDeleteBudgetItemFailureDueToReservedOfferings() {
        driver.get(BASE_URL + "/budget");
        selectFirstEvent();

        // Click delete button on the first item
        WebElement firstDeleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(1) button.delete-btn")));
        firstDeleteBtn.click();

        // Wait for snackbar and check failure message
        WebElement snackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("simple-snack-bar")));
        String message = snackBar.getText();

        Assertions.assertTrue(message.contains("has not been deleted"));
    }

    @Test
    public void testDeleteBudgetItem() {
        driver.get(BASE_URL + "/budget");
        selectFirstEvent();

        // Click delete button on the second item
        WebElement secondDeleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table.budget-table tr.data-row:nth-child(2) button.delete-btn")));
        secondDeleteBtn.click();

        // Wait for snackbar and check success message
        WebElement snackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("simple-snack-bar")));
        String message = snackBar.getText();

        Assertions.assertTrue(message.contains("Budget item deleted"));
    }

    @Test
    public void testViewDetailsOfProductOrServiceFromBudget() {
        // Test viewing details of product/service from budget by clicking "details" button
    }
    @Test
    public void testBuyNotAvailableProduct() {
        driver.get(BASE_URL + "/offering/8");

        // Pronađi dugme "Book Now" na stranici
        WebElement bookNowBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("button.book-now-btn")));

        // Proveri da li je dugme disabled
        boolean isDisabled = !bookNowBtn.isEnabled();
        Assertions.assertTrue(isDisabled, "Book Now button should be disabled for offering 8.");
    }

    private void selectFirstEvent() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement eventSelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("mat-select")));
        eventSelect.click();
        WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("mat-option")));
        firstOption.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.budget-table")));
    }
}

package com.ftn.iss.eventPlanner.selenium.page;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BudgetManagerPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final int UI_REFRESH_DELAY = 5000;
    private static final int SCROLL_DELAY = 1000;
    private static final int DATA_LOAD_DELAY = 2000;

    // Page Elements
    @FindBy(css = "mat-select")
    private WebElement eventSelect;

    @FindBy(css = "mat-option")
    private List<WebElement> eventOptions;

    @FindBy(css = "table.budget-table")
    private WebElement budgetTable;

    @FindBy(css = "button.add-button")
    private WebElement addBudgetItemButton;

    @FindBy(css = "mat-dialog-container")
    private WebElement dialog;

    @FindBy(css = "mat-select[formcontrolname='category']")
    private WebElement categorySelect;

    @FindBy(css = "input[formcontrolname='amount']")
    private WebElement amountInput;

    @FindBy(xpath = "//mat-dialog-actions[@id='add']//button[normalize-space()='Add' and not(@disabled)]")
    private WebElement addDialogButton;

    @FindBy(css = "td.amount-cell input.currency-input")
    private List<WebElement> amountInputs;

    @FindBy(css = "table.budget-table tr.data-row button.delete-btn")
    private List<WebElement> deleteButtons;

    @FindBy(css = "simple-snack-bar")
    private WebElement snackBar;

    @FindBy(css = ".budget-container")
    private WebElement budgetContainer;

    @FindBy(css = "mat-select.custom-select")
    private WebElement customEventSelect;

    @FindBy(css = "mat-form-field.event-selector mat-select")
    private WebElement eventSelectorSelect;

    @FindBy(css = "table.budget-table tr.data-row")
    private List<WebElement> budgetTableRows;

    @FindBy(css = ".empty-state")
    private WebElement emptyState;

    @FindBy(css = "table.budget-table tr.data-row:nth-child(1) input.currency-input")
    private WebElement firstRowAmountInput;

    @FindBy(css = "body")
    private WebElement body;

    public BudgetManagerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void selectEventByName(String eventName) {
        wait.until(ExpectedConditions.elementToBeClickable(eventSelect));
        eventSelect.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-option[contains(., '" + eventName + "')]")));
        option.click();
    }
    public void clickAddBudgetItem() {
        wait.until(ExpectedConditions.elementToBeClickable(addBudgetItemButton));
        addBudgetItemButton.click();
    }

    public void waitForDialog() {
        wait.until(ExpectedConditions.visibilityOf(dialog));
    }

    public void selectCategory(int categoryIndex) {
        wait.until(ExpectedConditions.elementToBeClickable(categorySelect));
        categorySelect.click();

        List<WebElement> categoryOptions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("mat-option")));
        Assertions.assertFalse(categoryOptions.isEmpty(), "Category options should not be empty");
        categoryOptions.get(categoryIndex).click();
    }

    public void enterAmount(String amount) {
        wait.until(ExpectedConditions.elementToBeClickable(amountInput));
        amountInput.clear();
        amountInput.sendKeys(amount);
    }

    public void clickAddInDialog() {
        By addButtonLocator = By.xpath("//mat-dialog-actions//button[normalize-space()='Add' and not(@disabled)]");
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addButtonLocator));
        addButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("mat-dialog-container")));
    }

    public boolean isBudgetItemPresent(String amount) {
        waitForUIRefresh();
        List<WebElement> amounts = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("td.amount-cell input.currency-input")));
        return amounts.stream().anyMatch(e -> e.getAttribute("value").equals(amount));
    }

    public void clickDeleteButton(int rowIndex) {
        wait.until(ExpectedConditions.elementToBeClickable(deleteButtons.get(rowIndex)));
        deleteButtons.get(rowIndex).click();
    }

    public String getSnackBarMessage() {
        wait.until(ExpectedConditions.visibilityOf(snackBar));
        return snackBar.getText();
    }

    public void waitForSnackbarWithText(String expectedText) {
        WebElement snackBarElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//simple-snack-bar[contains(., \"" + expectedText + "\")]")));
        Assertions.assertNotNull(snackBarElement, "Expected snackbar with text '" + expectedText + "' was not shown.");
    }

    private WebElement findBudgetEventSelect() {
        WebElement eventSelectBudget = null;
        try {
            eventSelectBudget = wait.until(ExpectedConditions.elementToBeClickable(customEventSelect));
        } catch (TimeoutException e1) {
            try {
                eventSelectBudget = wait.until(ExpectedConditions.elementToBeClickable(eventSelectorSelect));
            } catch (TimeoutException e2) {
                eventSelectBudget = wait.until(ExpectedConditions.elementToBeClickable(eventSelect));
            }
        }
        return eventSelectBudget;
    }

    public boolean findBudgetItemInTable(String categoryKeyword, String offeringKeyword) {
        waitForDataLoad();

        if (budgetTableRows.isEmpty()) {
            if (emptyState != null && emptyState.isDisplayed()) {
                System.out.println("Budget table is empty - no budget items found");
                return false;
            }
        }

        for (WebElement row : budgetTableRows) {
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

    public void updateFirstRowAmount(String newAmount) {
        wait.until(ExpectedConditions.elementToBeClickable(firstRowAmountInput));
        firstRowAmountInput.clear();
        firstRowAmountInput.sendKeys(newAmount);
    }

    public void clickBody() {
        body.click();
    }

    public void pressTab() {
        firstRowAmountInput.sendKeys(Keys.TAB);
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try {
            Thread.sleep(SCROLL_DELAY);
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

    private void waitForDataLoad() {
        try {
            Thread.sleep(DATA_LOAD_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
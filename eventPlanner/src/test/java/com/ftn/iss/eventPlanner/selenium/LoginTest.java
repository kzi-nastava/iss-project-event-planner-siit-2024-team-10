package com.ftn.iss.eventPlanner.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";

    @BeforeAll
    public void setupClass() {
        // Ako treba, postavi path do chromedrivera sistemski
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
    }

    @BeforeEach
    public void setupTest() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(BASE_URL + "/login");
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSuccessfulLogin() {
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[formcontrolname='email']")));
        emailInput.sendKeys("organizer@mail.com");

        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        passwordInput.sendKeys("password123");

        WebElement loginButton = driver.findElement(By.cssSelector("button.login-button"));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/home"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/home"), "User should be redirected to home page after login");
    }

    @Test
    public void testInvalidLoginShowsError() {
        // Unesi nevalidan email
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[formcontrolname='email']")));
        emailInput.sendKeys("wrong@mail.com");

        // Unesi pogrešan password
        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        passwordInput.sendKeys("wrongpassword");

        // Klikni na login dugme
        WebElement loginButton = driver.findElement(By.cssSelector("button.login-button"));
        loginButton.click();

        // Čekaj da se pojavi error poruka
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-error")));

        Assertions.assertEquals("Incorrect username or password", errorMsg.getText());
    }
}

package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\pasqu\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get("http://localhost/login");

        WebElement usernameInput = driver.findElement(By.id("email"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("input[type=submit]"));

        usernameInput.sendKeys("pippobaudo@gmail.com");
        passwordInput.sendKeys("Pippino0");
        submitButton.click();

        // Imposta il tempo di attesa massimo in secondi (puoi personalizzarlo)
        int tempoDiAttesaMassimo = 10;

        WebDriverWait wait = new WebDriverWait(driver, tempoDiAttesaMassimo);

        // Attendere che la pagina di destinazione si carichi e l'URL corrente corrisponda
        String urlPaginaDiRedirezione = "http://localhost/main";
        wait.until(ExpectedConditions.urlToBe(urlPaginaDiRedirezione));

        // Verifica la redirezione
        if (driver.getCurrentUrl().equals(urlPaginaDiRedirezione)) {
            System.out.println("Test passato! Sei stato rediretto correttamente.");
        } else {
            System.out.println("Test fallito! La redirezione non è avvenuta correttamente.");
        }

        driver.quit();
    }
}
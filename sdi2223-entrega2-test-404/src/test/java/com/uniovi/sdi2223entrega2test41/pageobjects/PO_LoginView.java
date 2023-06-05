package com.uniovi.sdi2223entrega2test41.pageobjects;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_LoginView extends PO_NavView {
    static public void fillLoginForm(WebDriver driver, String emailp, String passwordp) {
        WebElement email = driver.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys(emailp);
        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(passwordp);
        //Pulsar el boton de Alta.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }

    static public void fillLoginFormApiClient(WebDriver driver, String emailp, String passwordp) {
        WebElement email = driver.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys(emailp);
        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(passwordp);
        //Pulsar el boton de Alta.
        By boton = By.id("boton-login");
        driver.findElement(boton).click();
    }

    static public void logIn(WebDriver driver, String dnip, String passwordp) {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, dnip, passwordp);
    }

    static public void logOut(WebDriver driver) {
        String logout = PO_HomeView.getP().getString("logout.message", PO_Properties.getSPANISH());
        PO_PrivateView.clickOption(driver, "logout", "text", logout);
    }
}


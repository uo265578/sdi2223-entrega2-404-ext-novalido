package com.uniovi.sdi2223entrega2test41.pageobjects;


import com.uniovi.sdi2223entrega2test41.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_PrivateView extends  PO_NavView{

    static public void fillFormAddMessage(WebDriver driver, String contentp)
    {
        //Espera por el DOM
        SeleniumUtils.waitSeconds(driver, 5);
        //Escribimos el mensaje
        WebElement description = driver.findElement(By.name("content"));
        description.clear();
        description.sendKeys(contentp);
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }


    static public void logout(WebDriver driver){
        String loginText ="Identificacion de usuario";
        clickOption(driver, "logout", "text", loginText);

    }

}

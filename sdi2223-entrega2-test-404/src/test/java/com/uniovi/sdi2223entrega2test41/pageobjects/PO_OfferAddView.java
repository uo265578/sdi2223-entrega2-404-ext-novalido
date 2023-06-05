package com.uniovi.sdi2223entrega2test41.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_OfferAddView extends PO_NavView{

    public static void fillAddForm(WebDriver driver, String titulo, String detalle, String precio, boolean featured) {
        // Open dropdown menu
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "offers-add");
        elements.get(0).click();


        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys(titulo);
        WebElement description = driver.findElement(By.name("details"));
        description.click();
        description.clear();
        description.sendKeys(detalle);
        WebElement price = driver.findElement(By.name("price"));
        price.click();
        price.clear();
        price.sendKeys(precio);

        WebElement feature = driver.findElement(By.name("featured"));
        if(featured) {
            feature.click();
        }

        //Pulsar el boton de Alta.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }

    public static void fillAddForm(WebDriver driver, String titulo, String detalle, String precio, String image) {

        // Open dropdown menu
        String text = PO_HomeView.getP().getString("offer.management", PO_Properties.getSPANISH());
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", text);
        elements.get(0).click();


        text = PO_HomeView.getP().getString("offer.addoffer", PO_Properties.getSPANISH());
        elements = PO_View.checkElementBy(driver, "text", text);
        elements.get(0).click();

        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys(titulo);
        WebElement description = driver.findElement(By.name("details"));
        description.click();
        description.clear();
        description.sendKeys(detalle);
        WebElement price = driver.findElement(By.name("price"));
        price.click();
        price.clear();
        price.sendKeys(precio);
        WebElement input_element = driver.findElement(By.xpath("//input[@type='file']"));

        String filePath = System.getProperty("user.dir");
        input_element.sendKeys(filePath + "\\src\\test\\resources_test\\test.jpg");


        //Pulsar el boton de Alta.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}

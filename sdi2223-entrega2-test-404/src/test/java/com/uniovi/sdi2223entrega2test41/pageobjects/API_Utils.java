package com.uniovi.sdi2223entrega2test41.pageobjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

public class API_Utils {

    public static String login(WebDriver driver, String user, String password){
        String urlLogin = "http://localhost:8081/api/v1.0/users/login";
        //1. Creamos la peticion
        RequestSpecification request = RestAssured.given();
        //2. Creamos los parametros de la peticion
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", user);
        requestParams.put("password", password);
        //3. Añadimos los parametros a la peticion
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //4. Hacemos la peticion
        Response response = request.post(urlLogin);
        //5. Comprobamos el resultado
        Assertions.assertEquals(200, response.getStatusCode());
        //6.0 Obtenemos el token
        String token = response.getBody().jsonPath().get("token");
        Assertions.assertTrue(token.length() > 20);
        return token;
    }
}

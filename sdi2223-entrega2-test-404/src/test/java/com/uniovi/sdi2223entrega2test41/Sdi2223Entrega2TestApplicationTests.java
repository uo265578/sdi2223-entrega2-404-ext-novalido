package com.uniovi.sdi2223entrega2test41;

import com.uniovi.sdi2223entrega2test41.pageobjects.*;
import com.uniovi.sdi2223entrega2test41.util.SeleniumUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;


import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckodriver = "C:\\tools\\selenium\\geckodriver-v0.30.0-win64.exe";

    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:8081";

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        driver.navigate().to(URL);
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {
    }

    //Al finalizar la Ãºltima prueba
    @AfterAll
    static public void end() {
//Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    //[Prueba1] Registro de Usuario con datos válidos
    @Test
    @Order(1)
    public void PR01() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "user16@email.com", "Josefo", "Perez", "user16", "user16", "2022-12-12");

        //Comprobamos que entramos en la sección privada y nos nuestra el texto a buscar
        String checkText = "Ofertas propias";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    //[Prueba2] Registro de Usuario con datos inválidos (email, nombre, apellidos y fecha de nacimiento
    //vacíos).
    @Test
    @Order(2)
    public void PR02() {
        //Vamos al registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");

        //Email vacío
        PO_SignUpView.fillForm(driver, "", "Jose", "Perez", "77777", "77777", "2022-12-12");
        String checkText = "El email no es valido";
        List<WebElement> result = PO_SignUpView.checkElementBy(driver, "text", checkText);

        //Comprobamos el error de campo vacio
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Nombre vacío
        PO_SignUpView.fillForm(driver, "myEmail@gmail.com", "", "Perez", "77777", "77777", "2022-12-12");
        checkText = "El nombre no debe ser vacio";
        result = PO_SignUpView.checkElementBy(driver, "text", checkText);

        //Comprobamos el error de campo vacio
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Apellidos vacíos
        PO_SignUpView.fillForm(driver, "myEmail@gmail.com", "Jose", "", "77777", "77777", "2022-12-12");
        checkText = "El apellido no debe ser vacio";
        result = PO_SignUpView.checkElementBy(driver, "text", checkText);

        //Comprobamos el error de campo vacio
        Assertions.assertEquals(checkText, result.get(0).getText());


        //Fecha de nacimiento vacia
        PO_SignUpView.fillForm(driver, "myEmail@gmail.com", "Jose", "Alonso", "77777", "77777", "");
        checkText = "La fecha no es valida";
        result = PO_SignUpView.checkElementBy(driver, "text", checkText);

        //Comprobamos el error de campo vacio
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    //[Prueba3] Registro de Usuario con datos inválidos (repetición de contraseña inválida).
    @Test
    @Order(3)
    public void PR03() {
        //Vamos al registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");

        //Contraseñas no coinciden
        PO_SignUpView.fillForm(driver, "myEmail@gmail.com", "Jose",
                "Perez", "77777", "77779", "2022-12-12");
        String message = "Las contrasenas no coinciden";
        List<WebElement> result = PO_SignUpView.checkElementBy(driver, "text", message);
        Assertions.assertEquals(message, result.get(0).getText());
    }

    //[Prueba4] Registro de Usuario con datos inválidos (email existente).
    @Test
    @Order(4)
    public void PR04() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");

        //Email vacío
        PO_SignUpView.fillForm(driver, "user01@email.com", "Jose",
                "Perez", "77777", "77777", "2022-12-12");
        String message = "Ese email ya existe";
        List<WebElement> result = PO_SignUpView.checkElementBy(driver, "text", message);

        Assertions.assertEquals(message, result.get(0).getText());
    }

    //[Prueba5] Inicio de sesión con datos válidos (administrador).
    @Test
    @Order(5)
    public void PR05() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Comprobamos que entramos en la pagina privada de Admin
        List<WebElement> result = PO_View.checkElementBy(driver, "id", "title-user-list");

        Assertions.assertEquals("Usuarios del sistema", result.get(0).getText());
    }

    //[Prueba6] Inicio de sesión con datos válidos (usuario estándar).
    @Test
    @Order(6)
    public void PR06() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "user01@email.com", "user01");

        // COmprobamos que entramos en la pagina privada de usuario
        List<WebElement> result = PO_View.checkElementBy(driver, "text", "Ofertas propias");

        Assertions.assertEquals("Ofertas propias", result.get(0).getText());
    }


    //[Prueba7] Inicio de sesión con datos inválidos (usuario estándar, email existente, pero contraseña
    //incorrecta).
    @Test
    @Order(7)
    public void PR07() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "user01@email.com", "123456789");

        // Comprobamos que salen los errores
        List<WebElement> result = PO_View.checkElementBy(driver, "text", "No se encuentra el usuario");
        Assertions.assertEquals("No se encuentra el usuario", result.get(0).getText());
    }

    //[Prueba8] Inicio de sesión con datos inválidos (usuario estándar, campo email o contraseña vacíos).
    @Test
    @Order(8)
    public void PR08() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "", "");

        // Comprobamos que salen los errores
        List<WebElement> result = PO_View.checkElementBy(driver, "text", "El email no debe ser vacio");
        Assertions.assertEquals("El email no debe ser vacio", result.get(0).getText());
        result = PO_View.checkElementBy(driver, "text", "La contrasena no debe ser vacia");
        Assertions.assertEquals("La contrasena no debe ser vacia", result.get(0).getText());
    }

    //[Prueba9] Hacer clic en la opción de salir de sesión y comprobar que se redirige a la página de inicio de
    //sesión (Login).
    @Test
    @Order(9)
    public void PR09() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");
        // Comprobamos que entramos en la pagina privada de usuario
        List<WebElement> result = PO_View.checkElementBy(driver, "id", "title-user-list");

        Assertions.assertEquals("Usuarios del sistema", result.get(0).getText());
        // Ahora nos desconectamos
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        result = PO_View.checkElementBy(driver, "text", "Identificacion de usuario");

        Assertions.assertEquals("Identificacion de usuario", result.get(0).getText());
    }

    //[Prueba10] Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
    @Test
    @Order(10)
    public void PR10() {
        // Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        // Comprobamos que en home sin estar logeado no aparece la opcion de cerrar
        // sesion (desconectar)
        boolean assertion = false;
        try {
            SeleniumUtils.waitLoadElementsByXpath(driver, "Desconectar", PO_View.getTimeout());
        } catch (TimeoutException e) {
            assertion = true;
        }
        Assertions.assertTrue(assertion);
    }


    //[Prueba11] Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el sistema.
    @Test
    @Order(11)
    public void PR11() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Comprobamos que entramos en la pagina privada de admin
        String checkText = "Usuarios del sistema";
        List<WebElement> result = PO_View.checkElementBy(driver, "id", "title-user-list");
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Contamos el número de filas de usuarios en la primera página
        List<WebElement> userList = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());
        Assertions.assertEquals(4, userList.size());

        //Contamos el número de filas de usuarios en la segunda página
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "a-2");
        pages.get(0).click();//Vamos a la segunda página
        userList = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());
        Assertions.assertEquals(4, userList.size());


        //Contamos el número de filas de usuarios en la tercera página
        pages = PO_View.checkElementBy(driver, "id", "a-3");
        pages.get(0).click();//Vamos a la tercera página
        userList = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());
        Assertions.assertEquals(4, userList.size());


        //Contamos el número de filas de usuarios en la cuarta página
        pages = PO_View.checkElementBy(driver, "id", "a-4");
        pages.get(0).click();//Vamos a la cuarta página
        userList = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());
        Assertions.assertEquals(4, userList.size());

        //Ahora nos desconectamos y comprobamos que aparece el menú de registro
        PO_PrivateView.logout(driver);
    }

    //[Prueba12] Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza
    //y dicho usuario desaparece.
    @Test
    @Order(12)
    public void PR12() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Comprobamos que entramos en la pagina privada de admin
        String checkText = "Usuarios del sistema";
        List<WebElement> result = PO_View.checkElementBy(driver, "id", "title-user-list");
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Contamos el número de filas de usuarios
        List<WebElement> userList = PO_View.checkElementBy(driver, "@type", "checkbox");
        Assertions.assertEquals(4, userList.size());

        //Hacemos click en la primera checkbox
        userList.get(0).click();

        //Eliminamos usuario
        List<WebElement> button = PO_View.checkElementBy(driver, "id", "deleteButton");
        button.get(0).click();

        //Comprobamos que el usuario "user01@email.com" se ha borrado: ningun checkbox puede tener como valor "/user/delete/1"
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user/delete/1", PO_View.getTimeout());


        //Ahora nos desconectamos y comprobamos que aparece el menú de registro
        PO_PrivateView.logout(driver);
    }

    //[Prueba13] Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
    //y dicho usuario desaparece.
    @Test
    @Order(13)
    public void PR13() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Comprobamos que entramos en la pagina privada de admin
        String checkText = "Usuarios del sistema";
        List<WebElement> result = PO_View.checkElementBy(driver, "id", "title-user-list");
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Vamos a la ultima página
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "a-3");
        pages.get(0).click();//Vamos a la tercera página
        pages = PO_View.checkElementBy(driver, "id", "a-4");
        pages.get(0).click();//Vamos a la cuarta página

        //Contamos el número de filas de usuarios
        List<WebElement> userList = PO_View.checkElementBy(driver, "@type", "checkbox");
        Assertions.assertEquals(3, userList.size()); //Hay 4 usuarios en la pagina

        //Hacemos click en la ultima checkbox
        userList.get(userList.size() - 1).click();

        //Eliminamos usuario
        List<WebElement> button = PO_View.checkElementBy(driver, "id", "deleteButton");
        button.get(0).click();


        //Comprobamos que el usuario "user15@email.com" se ha borrado: ningun checkbox puede tener como valor "/user/delete/15"
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user/delete/15", PO_View.getTimeout());

        //Ahora nos desconectamos y comprobamos que aparece el menú de registro
        PO_PrivateView.logout(driver);
    }


    //[Prueba14] Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se actualiza y dichos
    //usuarios desaparecen.
    @Test
    @Order(14)
    public void PR14() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Comprobamos que entramos en la pagina privada de admin
        String checkText = "Usuarios del sistema";
        List<WebElement> result = PO_View.checkElementBy(driver, "id", "title-user-list");
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Contamos el número de filas de usuarios
        List<WebElement> userList = PO_View.checkElementBy(driver, "@type", "checkbox");
        Assertions.assertEquals(4, userList.size()); //En la primera página hay 5 usuarios


        //Hacemos click en las 3 primeras checkboxes
        userList.get(0).click();
        userList.get(1).click();
        userList.get(2).click();

        //Eliminamos los usuarios
        List<WebElement> button = PO_View.checkElementBy(driver, "id", "deleteButton");
        button.get(0).click();

        //Comprobamos que los usuarios "user02@email.com","user03@email.com","user04@email.com" se han borrado
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user/delete/2", PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user/delete/3", PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user/delete/4", PO_View.getTimeout());

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba15] Intentar borrar el usuario que se encuentra en sesión y comprobar que no ha sido borrado
    //(porque no es un usuario administrador o bien, porque, no se puede borrar a sí mismo, si está
    //autenticado)
    @Test
    @Order(15)
    public void PR15() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Intentamos borrar al usuario en sesion (al ser el admin, NO aparece en la lista para borrar usuarios,
        //hay que intentar borrar por URL)
        driver.navigate().to("http://localhost:8081/users/delete/admin");

        //Comprobamos que se muestra el error
        String checkText = "No puedes eliminar al usuario administrador";
        List<WebElement> list = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, list.get(0).getText());

        PO_PrivateView.logout(driver);

        //Ahora entramos como usuario estandar
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        //Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "user06@email.com", "user06");

        //Intentamos borrar al usuario en sesion. Para ello, intentamos ir a users/list
        driver.navigate().to("http://localhost:8081/users/list");

        //Comprobamos que se muestra el error
        checkText = "Zona autorizada solo para administradores";
        list = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, list.get(0).getText());

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);


    }

    //[Prueba16] Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Enviar.
    //Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
    @Test
    @Order(16)
    public void PR16() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user07@email.com", "user07");

        //Añadir y comprobar Oferta
        createAndCheckOffer("Aitulo", "detalle de la oferta", "120");

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }


    //[Prueba16] Añadir varias ofertas
    @Test
    @Order(17)
    public void PR16_A() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user07@email.com", "user07");

        //Añadir y comprobar Oferta
        createAndCheckOffer("1Titulo", "detalle de la oferta", "120");
        createAndCheckOffer("1Titulo_1", "detalle de la oferta_1", "12");
        createAndCheckOffer("1Titulo_2", "detalle de la oferta_2", "10");
        createAndCheckOffer("1Titulo_3", "detalle de la oferta_3", "1");

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba17] Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío y precio
    //en negativo) y pulsar el botón Submit. Comprobar que se muestra el mensaje de campo inválido.
    @Test
    @Order(18)
    public void PR17() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user06@email.com", "user06");

        //Comprobamos el fallo
        createAndCheckOfferWrong("Titulo", "detalle de la oferta", "-120", "El precio debe ser un numero positivo");

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba17] Comprobar varias ofertas invalidas
    @Test
    @Order(19)
    public void PR17_A() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user06@email.com", "user06");

        //Comprobamos el fallo
        createAndCheckOfferWrong("Titulo", "detalle de la oferta", "-120", "El precio debe ser un numero positivo");
        createAndCheckOfferWrong("Titu", "detalle de la oferta", "120", "El titulo debe contener como minimo 5 caracteres");
        createAndCheckOfferWrong("Titulo", "det", "120", "Los detalles deben contener como minimo 5 caracteres");

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    private void createAndCheckOfferWrong(String titulo, String detalle, String price, String errorName) {
        //Rellenamos el formulario
        PO_OfferAddView.fillAddForm(driver, titulo, detalle, price, false);

        //Comprobamos que ha fallado
        List<WebElement> result = PO_View.checkElementBy(driver, "text", errorName);
        Assertions.assertEquals(errorName, result.get(0).getText());
    }

    //[Prueba18] Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas los que
    //existen para este usuario
    @Test
    @Order(20)
    public void PR18() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user06@email.com", "user06");

        checkExists("Oferta1-Usuario6", 3);
        checkExists("Oferta2-Usuario6", 3);
        checkExists("Oferta3-Usuario6", 3);
        checkExists("Oferta4-Usuario6", 3);
        checkExists("Oferta5-Usuario6", 3);
        checkExists("Oferta6-Usuario6", 3);
        checkExists("Oferta7-Usuario6", 3);
        checkExists("Oferta8-Usuario6", 3);
        checkExists("Oferta9-Usuario6", 3);
        checkExists("Oferta10-Usuario6", 3);

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);

    }

    //[Prueba19] Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza y
    //que la oferta desaparece.
    @Test
    @Order(21)
    public void PR19() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user06@email.com", "user06");

        //Cogemos la primera oferta
        WebElement oferta = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout()).get(0);

        List<WebElement> elements = oferta.findElements(By.xpath("//td"));

        //Obtenemos el título de la oferta
        String titulo = elements.get(0).getText();

        //Borramos la oferta
        elements.get(4).click();

        checkNotExists(titulo, 3);

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba20] Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza y
    //que la oferta desaparece.
    @Test
    @Order(22)
    public void PR20() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user06@email.com", "user06");

        //Cogemos la primera oferta
        List<WebElement> ofertas = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());

        WebElement oferta = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout()).get(ofertas.size() - 1);

        List<WebElement> elements = oferta.findElements(By.xpath("//td"));

        //Obtenemos el título de la oferta
        String titulo = elements.get(0).getText();

        //Borramos la oferta
        elements.get(4).click();

        checkNotExists(titulo, 3);

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba21] Ir a la lista de ofertas, borrar una oferta de otro usuario, comprobar que la oferta no se
    //borra.
    @Test
    @Order(23)
    public void PR21() {
        PO_LoginView.logIn(driver, "user06@email.com", "user06");

        driver.navigate().to("http://localhost:8081/offers/delete/645674ce37538dbbf7505e07");

        List<WebElement> error = PO_View.checkElementBy(driver, "text", "No puedes borrar una oferta que no es tuya");
        String errorText = error.get(0).getText();

        Assert.assertEquals("No puedes borrar una oferta que no es tuya", errorText);

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }


    //Prueba22] Ir a la lista de ofertas, borrar una oferta propia que ha sido vendida, comprobar que la
    //oferta no se borra.
    @Test
    @Order(24)
    public void PR22() {
        //Hacemos login
        PO_LoginView.logIn(driver, "user06@email.com", "user06");


        WebElement oferta  = findOffer("Oferta6-Usuario6", 3);

        List<WebElement> elements = oferta.findElements(By.xpath("//td"));
        //Obtenemos el título de la oferta
        String titulo = elements.get(0).getText();

        //Borramos la oferta
        oferta.findElement(By.id("del-Oferta6-Usuario6")).click();

        List<WebElement> error = PO_View.checkElementBy(driver, "text", "No puedes borrar una oferta comprada");
        String errorText = error.get(0).getText();

        checkExists(titulo, 3);

        Assert.assertEquals("No puedes borrar una oferta comprada", errorText);

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba23] Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
    //corresponde con el listado de las ofertas existentes en el sistema
    @Test
    @Order(25)
    public void PR23() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        // Rellenamos el formulario
        // Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "user06@email.com", "user06");
        // COmprobamos que entramos en la pagina privada de usuario
        PO_View.checkElementBy(driver, "text", "Ofertas propias");
        // Pinchamos la opcion de comprar ofertas
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-shop");
        pages.get(0).click();
        // Comprobamos que tenga sus ofertas.
        WebElement buscar = driver.findElement(By.name("search"));
        buscar.click();
        buscar.clear();
        buscar.sendKeys("");
        By boton = By.className("btn");
        driver.findElement(boton).click();
        // Contamos el número de filas de ofertas --> todos los existentes
        List<WebElement> numElementos = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());

        // Falta comprobar el numero de ofertas que vamos a poner
        Assertions.assertEquals(5, numElementos.size());

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba24] Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
    //muestra la página que corresponde, con la lista de ofertas vacía.
    @Test
    @Order(26)
    public void PR24() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        // Rellenamos el formulario
        // Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "user06@email.com", "user06");
        // COmprobamos que entramos en la pagina privada de usuario
        PO_View.checkElementBy(driver, "text", "Ofertas propias");
        // Pinchamos la opcion de comprar ofertas
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-shop");
        pages.get(0).click();
        // Comprobamos que tenga sus ofertas.
        WebElement buscar = driver.findElement(By.name("search"));
        buscar.click();
        buscar.clear();
        buscar.sendKeys("Gorra");
        By boton = By.className("btn");
        driver.findElement(boton).click();
        // Comprobamos que no hay ninguna fila buscando el texto de "Destacar"
        boolean found;
        try {
            List<WebElement> offers = PO_View.checkElementBy(driver, "text", "Destacar");
            found = true; //Encontro el texto
        } catch (Exception e) {
            found = false; //No encontro el texto
        }

        Assert.assertFalse(found);//No debe encontrar el texto "Destacar"

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba25] Hacer una búsqueda escribiendo en el campo un texto en minúscula o mayúscula y
    //comprobar que se muestra la página que corresponde, con la lista de ofertas que contengan dicho
    //texto, independientemente que el título esté almacenado en minúsculas o mayúscula
    @Test
    @Order(27)
    public void PR25() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        // Rellenamos el formulario
        // Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "user06@email.com", "user06");
        // COmprobamos que entramos en la pagina privada de usuario
        PO_View.checkElementBy(driver, "text", "Ofertas propias");
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-shop");
        pages.get(0).click();
        // Comprobamos que tenga sus ofertas.
        WebElement buscar = driver.findElement(By.name("search"));
        buscar.click();
        buscar.clear();
        buscar.sendKeys("OFERTA1-USUARIO10");
        By boton = By.className("btn");
        driver.findElement(boton).click();
        // Contamos el número de filas de notas --> todos los existentes
        List<WebElement> numElementos = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());

        // Falta comprobar el numero de ofertas que vamos a poner
        Assertions.assertEquals(1, numElementos.size());

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }


    //[Prueba26] Sobre una búsqueda determinada (a elección del desarrollador), comprar una oferta que deja
    //un saldo positivo en el contador del comprador. Comprobar que el contador se actualiza correctamente
    //en la vista del comprador.
    @Test
    @Order(28)
    public void PR26() {
        PO_LoginView.logIn(driver, "user05@email.com", "user05");
        //Vamos a la tienda
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-shop");
        pages.get(0).click();
        //Buscamos la oferta
        WebElement buscar = driver.findElement(By.name("search"));
        buscar.click();
        buscar.clear();
        buscar.sendKeys("OFERTA1-USUARIO10");
        By boton = By.className("btn");
        driver.findElement(boton).click();
        //Compramos la oferta
        List<WebElement> buy = PO_View.checkElementBy(driver, "text", "Comprar");
        buy.get(1).click();

        WebElement s = PO_View.checkElementBy(driver, "id", "money").get(0);
        String saldo = s.getText().substring(8);
        double balance = Double.valueOf(saldo.split("€")[0]);
        Assertions.assertEquals(80.0, balance, 0);

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba27] Sobre una búsqueda determinada (a elección del desarrollador), comprar una oferta que deja
    //un saldo 0 en el contador del comprador. Comprobar que el contador se actualiza correctamente en la
    //vista del comprador.
    @Test
    @Order(29)
    public void PR27() {
        PO_LoginView.logIn(driver, "user06@email.com", "user06");
        //Vamos a la tienda
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-shop");
        pages.get(0).click();
        //Buscamos la oferta
        WebElement buscar = driver.findElement(By.name("search"));
        buscar.click();
        buscar.clear();
        buscar.sendKeys("OFERTA2-USUARIO10");
        By boton = By.className("btn");
        driver.findElement(boton).click();
        //Compramos la oferta
        List<WebElement> buy = PO_View.checkElementBy(driver, "text", "Comprar");
        buy.get(1).click();

        WebElement s = PO_View.checkElementBy(driver, "id", "money").get(0);
        String saldo = s.getText().substring(8);
        double balance = Double.valueOf(saldo.split("€")[0]);
        Assertions.assertEquals(0.0, balance, 0);


        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba28] Sobre una búsqueda determinada (a elección del desarrollador), intentar comprar una oferta
    //que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje de
    //saldo no suficiente.
    @Test
    @Order(30)
    public void PR28() {
        PO_LoginView.logIn(driver, "user07@email.com", "user07");
        //Vamos a la tienda
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-shop");
        pages.get(0).click();
        //Buscamos la oferta
        WebElement buscar = driver.findElement(By.name("search"));
        buscar.click();
        buscar.clear();
        buscar.sendKeys("OFERTA5-USUARIO11");
        By boton = By.className("btn");
        driver.findElement(boton).click();
        //Compramos la oferta
        List<WebElement> buy = PO_View.checkElementBy(driver, "text", "Comprar");
        buy.get(1).click();
        //Comprobamos que sale el error
        String checkText = "No tiene suficiente saldo para comprar la oferta";
        List<WebElement> err = PO_View.checkElementBy(driver, "text", checkText);
        Assert.assertEquals(checkText, err.get(0).getText());
        //Ahora nos desconectamos y comprobamos que aparece el menú de login**
        PO_PrivateView.logout(driver);
    }

    //[Prueba29] Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
    //las ofertas que deben aparecer.
    @Test
    @Order(31)
    public void PR29() {
        PO_LoginView.logIn(driver, "user08@email.com", "user08");

        //Vamos a las ofertas compradas
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-bought");
        pages.get(0).click();

        int numeroDeOfertas = 0;

        //Miramos que en la página 1 haya 5 ofertas
        List<WebElement> ofertas = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        numeroDeOfertas += ofertas.size();

        //Vamos a la página 2
        pages = PO_View.checkElementBy(driver, "id", "a-2");
        pages.get(0).click();

        //Miramos que en la página 2 haya 5 ofertas
        ofertas = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        numeroDeOfertas += ofertas.size();

        Assert.assertEquals(9, numeroDeOfertas);

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba30] Al crear una oferta marcar dicha oferta como destacada y a continuación comprobar: i) que
    //aparece en el listado de ofertas destacadas para los usuarios y que el saldo del usuario se actualiza
    //adecuadamente en la vista del ofertante (comprobar saldo antes y después, que deberá diferir en
    //20€).
    @Test
    @Order(32)
    public void PR30() {
        PO_LoginView.logIn(driver, "user09@email.com", "user09");

        //Primero obtenemos el balance del usuario
        WebElement s = PO_View.checkElementBy(driver, "id", "money").get(0);
        String saldo = s.getText().substring(8);
        double balanceInicial = Double.valueOf(saldo.split("€")[0]);

        //Rellenamos el formulario de añadir oferta
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-add");
        pages.get(0).click();
        PO_OfferAddView.fillAddForm(driver, "OFERTA-DESTACADA", "Descripcion", "20", true);

        s = PO_View.checkElementBy(driver, "id", "money").get(0);
        saldo = s.getText().substring(8);
        double balanceFinal = Double.valueOf(saldo.split("€")[0]);

        Assert.assertEquals(balanceInicial - 20, balanceFinal, 0);

        //Vamos a las ofertas destacadas
        pages = PO_View.checkElementBy(driver, "id", "offers-featured");
        pages.get(0).click();

        List<WebElement> elements = PO_View.checkElementBy(driver, "text", "OFERTA-DESTACADA");

        Assert.assertEquals(1, elements.size());

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba31] Sobre el listado de ofertas de un usuario con más de 20 euros de saldo, pinchar en el enlace
    //Destacada y a continuación comprobar: i) que aparece en el listado de ofertas destacadas para los
    //usuarios y que el saldo del usuario se actualiza adecuadamente en la vista del ofertante (comprobar
    //saldo antes y después, que deberá diferir en 20€ ).
    @Test
    @Order(33)
    public void PR31() {
        PO_LoginView.logIn(driver, "user09@email.com", "user09");

        //Primero obtenemos el balance del usuario
        WebElement s = PO_View.checkElementBy(driver, "id", "money").get(0);
        String saldo = s.getText().substring(8);
        double balanceInicial = Double.valueOf(saldo.split("€")[0]);

        //Vamos a la página de ofertas propias y destacamos la oferta
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-list");
        pages.get(0).click();
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", "Destacar");
        elements.get(0).click();

        s = PO_View.checkElementBy(driver, "id", "money").get(0);
        saldo = s.getText().substring(8);
        double balanceFinal = Double.valueOf(saldo.split("€")[0]);

        Assert.assertEquals(balanceInicial - 20, balanceFinal, 0);

        //Vamos a las ofertas destacadas
        pages = PO_View.checkElementBy(driver, "id", "offers-featured");
        pages.get(0).click();

        elements = PO_View.checkElementBy(driver, "text", "Usuario9");

        Assert.assertEquals(1, elements.size());

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba32] Sobre el listado de ofertas de un usuario con menos de 20 euros de saldo, pinchar en el
    //enlace Destacada y a continuación comprobar que se muestra el mensaje de saldo no suficiente.
    @Test
    @Order(34)
    public void PR32() {
        PO_LoginView.logIn(driver, "user10@email.com", "user10");

        //Vamos a la página de ofertas propias y destacamos la oferta
        List<WebElement> pages = PO_View.checkElementBy(driver, "id", "offers-list");
        pages.get(0).click();
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", "Destacar");
        elements.get(0).click();

        //Vamos a las ofertas destacadas
        WebElement element = PO_View.checkElementBy(driver, "text", "No tiene suficiente saldo para destacar la oferta").get(0);

        Assert.assertEquals("No tiene suficiente saldo para destacar la oferta", element.getText());

        //Ahora nos desconectamos y comprobamos que aparece el menú de login
        PO_PrivateView.logout(driver);
    }

    //[Prueba33] Intentar acceder sin estar autenticado a la opción de listado de usuarios. Se deberá volver al
    //formulario de login.
    @Test
    @Order(35)
    public void PR33() {
        //Intentamos ir al listado de usuarios
        driver.navigate().to("http://localhost:8081/users/list");

        //Comprobamos que nos redirige al login
        String loginText = "Identificacion de usuario";
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", loginText);
        Assertions.assertEquals(loginText, elements.get(0).getText());


    }

    //[Prueba34] Intentar acceder sin estar autenticado a la opción de listado de conversaciones de un usuario estándar.
    //Se deberá volver al formulario de login.
    @Test
    @Order(36)
    public void PR34() {
        //Intentamos ir al listado de conversaciones de un usuario estandar
        driver.navigate().to("http://localhost:8081/apiclient/client.html?w=chats");

        //Comprobamos que nos redirige al login
        String loginText = "Email:";
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", loginText);
        Assertions.assertEquals(loginText, elements.get(0).getText());

        loginText = "Password:";
        elements = PO_View.checkElementBy(driver, "text", loginText);
        Assertions.assertEquals(loginText, elements.get(0).getText());
    }


    //[Prueba32] Estando autenticado como usuario estándar intentar acceder a una opción disponible solo
    //para usuarios administradores (Añadir menú de auditoria (visualizar logs)). Se deberá indicar un mensaje
    //de acción prohibida.
    @Test
    @Order(37)
    public void PR35() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "user06@email.com", "user06");


        //Intentamos ir al listado de logs
        driver.navigate().to("http://localhost:8081/logs/list");

        //Comprobamos que sale la pagina de error
        String message = "Zona autorizada solo para administradores";
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", message);
        Assertions.assertEquals(message, elements.get(0).getText());

        //Nos desconectamos
        driver.navigate().back();
        PO_PrivateView.logout(driver);


    }


    //[Prueba35] Estando autenticado como usuario administrador visualizar todos los logs generados en una
    //serie de interacciones. Esta prueba deberá generar al menos dos interacciones de cada tipo y comprobar
    //que el listado incluye los logs correspondientes.
    @Test
    @Order(38)
    public void PR36() {
        //PARTE USUARIOS, CREAR LOGS
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");

        //Rellenamos el formulario -GENERA UN ALTA
        PO_SignUpView.fillForm(driver, "user25@email.com", "Josefo", "Perez", "user25", "user25", "2022-12-12");

        PO_PrivateView.logout(driver);

        //Otro registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");

        //Rellenamos el formulario -GENERA OTRO ALTA
        PO_SignUpView.fillForm(driver, "user26@email.com", "Josefo", "Perez", "user26", "user26", "2022-12-12");

        PO_PrivateView.logout(driver);

        //Intentamos hacer login
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        // Rellenamos el formulario - GENERA UN LOGIN-ERR
        PO_LoginView.fillLoginForm(driver, "user22@email.com", "user05");

        //GENERA OTRO LOGIN-ERR
        PO_LoginView.logIn(driver, "user23@email.com", "user05");


        // Rellenamos el formulario - GENERA UN LOGIN-EX
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Nos desconectamos - GENERA UN LOGOUT
        PO_PrivateView.logout(driver);

        //OTRO USUARIO
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        // Rellenamos el formulario - GENERA OTRO LOGIN-EX
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Nos desconectamos - GENERA OTRO LOGOUT
        PO_PrivateView.logout(driver);

        //PARTE ADMIN, VER LOGS
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        //Rellenamos el formulario
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Vamos al menu de logs
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "log-list");
        elements.get(0).click();

        //Comprobamos que hay varios logs, al menos 2 de cada tipo
        //Miramos el numero de logs de tipo PET
        List<WebElement> selectList = PO_View.checkElementBy(driver, "id", "pet");
        selectList.get(0).click();


        List<WebElement> logList = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        Assertions.assertTrue(logList.size() >= 2); //2 generadas ahora + las anteriores

        //Miramos el numero de logs de tipo ALTA
        selectList = PO_View.checkElementBy(driver, "id", "alta");
        selectList.get(0).click();

        logList = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        Assertions.assertTrue(logList.size() >= 2); //2 generadas ahora + las anteriores


        //Miramos el numero de logs de tipo LOGIN-EX
        selectList = PO_View.checkElementBy(driver, "id", "login-ex");
        selectList.get(0).click();

        logList = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        Assertions.assertTrue(logList.size() >= 2); //2 generadas ahora + las anteriores

        //Miramos el numero de logs de tipo LOGIN-ERR
        selectList = PO_View.checkElementBy(driver, "id", "login-err");
        selectList.get(0).click();

        logList = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        Assertions.assertTrue(logList.size() >= 2); //2 generadas ahora + las anteriores


        //Miramos el numero de logs de tipo LOGIN-OUT
        selectList = PO_View.checkElementBy(driver, "id", "logout");
        selectList.get(0).click();

        logList = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        Assertions.assertTrue(logList.size() >= 2); //2 generadas ahora + las anteriores

        //Nos desconectamos
        PO_PrivateView.logout(driver);
    }


    //[Prueba37] Estando autenticado como usuario administrador, ir a visualización de logs, pulsar el
    //botón/enlace borrar logs y comprobar que se eliminan los logs de la base de datos.
    @Test
    @Order(39)
    public void PR37() {
        //Vamos al formulario de logeo y hacemos login
        PO_LoginView.logIn(driver, "admin@email.com", "admin");

        //Vamos al menu de logs
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "log-list");
        elements.get(0).click();

        //Apretamos el boton de eliminar
        elements = PO_View.checkElementBy(driver, "id", "deleteButton");
        elements.get(0).click();

        //Comprobamos que solo queda un log en la lista: el log de la peticion de borrar
        List<WebElement> logList = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                PO_View.getTimeout());
        //Tras borrar, quedan dos logs: el que se genera al borrar, y el que se genera al redirigir a la /log/list tras borrar
        Assertions.assertEquals(2, logList.size());

        //Nos desconectamos
        PO_PrivateView.logout(driver);


    }

    //[Prueba38] Inicio de sesión con datos válidos.
    @Test
    @Order(40)
    public void PR38() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parÃ¡metro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user07@email.com");
        requestParams.put("password", "user07");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la peticiÃ³n
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }


    //[Prueba39] Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
    @Test
    @Order(41)
    public void PR39() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parÃ¡metro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user07@email.com");
        requestParams.put("password", "123");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la peticiÃ³n
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(401, response.getStatusCode());
    }

    //[Prueba40] Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
    @Test
    @Order(42)
    public void PR40() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parametro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user07@email.com");
        requestParams.put("password", "");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la peticion
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(401, response.getStatusCode());
    }

    //[Prueba41] Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las que
    //existen para este usuario. Esta prueba implica invocar a dos servicios: S1 y S2.
    @Test
    @Order(43)
    public void PR41() {
        //Primero realizamos una petición POST al endPoint /api/v1.0/users/login con el usuario user02
        //y la contraseña user02.
        String token = API_Utils.login(driver, "user08@email.com", "user08");

        //A continuación, realizamos una petición GET al endPoint /api/v1.0/offers con el token obtenido
        //en el paso anterior.
        String RestAssuredURL = "http://localhost:8081/api/v1.0/offers";
        //2. Preparamos el parametro en formato JSON
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("token", token);
        //3. Hacemos la peticion
        Response response = request.get(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        //5. Comprobamos que se han obtenido 3 ofertas
        String pritty = response.getBody().prettyPrint();
        int count = 0;
        int index = pritty.indexOf("_id");
        while (index != -1) {
            count++;
            index = pritty.indexOf("_id", index + 1);
        }
        //We compare it with the number of offers that we have in the database
        Assertions.assertEquals(104, count);
    }

    //[Prueba42] Enviar un mensaje a una oferta. Esta prueba consistirá en comprobar que el servicio
    //almacena correctamente el mensaje para dicha oferta. Por lo tanto, el usuario tendrá que
    //identificarse (S1), enviar un mensaje para una oferta de id conocido (S3) y comprobar que el
    //mensaje ha quedado bien registrado (S4).
    @Test
    @Order(44)
    public void PR42() {
        Assert.fail();
    }

    //[Prueba43] Enviar un primer mensaje una oferta propia y comprobar que no se inicia la conversación.
    //En este caso de prueba, el propietario de la oferta tendrá que identificarse (S1), enviar un mensaje
    //para una oferta propia (S3) y comprobar que el mensaje no se almacena (S4).
    @Test
    @Order(45)
    public void PR43() {
        Assert.fail();
    }

    //[Prueba44] Obtener los mensajes de una conversación. Esta prueba consistirá en comprobar que el
    //servicio retorna el número correcto de mensajes para una conversación. El ID de la conversación
    //deberá conocerse a priori. Por lo tanto, se tendrá primero que invocar al servicio de identificación
    //(S1), y solicitar el listado de mensajes de una conversación de id conocido a continuación (S4),
    //comprobando que se retornan los mensajes adecuados.
    @Test
    @Order(46)
    public void PR44() {
        Assert.fail();
    }

    //[Prueba45] Obtener la lista de conversaciones de un usuario. Esta prueba consistirá en comprobar que
    //el servicio retorna el número correcto de conversaciones para dicho usuario. Por lo tanto, se tendrá
    //primero que invocar al servicio de identificación (S1), y solicitar el listado de conversaciones a
    //continuación (S5) comprobando que se retornan las conversaciones adecuadas.
    @Test
    @Order(47)
    public void PR45() {
        Assert.fail();
    }


    //[Prueba47] Marcar como leído un mensaje de ID conocido. Esta prueba consistirá en comprobar que
    //el mensaje marcado de ID conocido queda marcado correctamente a true como leído. Por lo
    //tanto, se tendrá primero que invocar al servicio de identificación (S1), solicitar el servicio de
    //marcado (S7), comprobando que el mensaje marcado ha quedado marcado a true como leído (S4).
    @Test
    @Order(47)
    public void PR47() {
        String token = API_Utils.login(driver, "user12@email.com", "user12");

        //We send a put request to the endpoint /api/v1.0/messages with the messageId of the message
        //that we want to mark as read
        String RestAssuredURL = "http://localhost:8081/api/v1.0/messages";
        //2. Preparamos el parametro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        //This id is for a message that user12@email.com sends to user01@email.com
        String id = "645674ce37538dbbf7505211";
        requestParams.put("messageId", id);
        request.header("Content-Type", "application/json");
        request.header("token", token);
        request.body(requestParams.toJSONString());
        //3. Hacemos la peticion
        Response response = request.put(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        response.getBody().prettyPrint();

        //
        Assert.fail();
    }

    //[Prueba48] Inicio de sesión con datos válidos.
    @Test
    @Order(48)
    public void PR48() {
        driver.navigate().to("http://localhost:8081/apiclient/client.html?w=login");
        PO_LoginView.fillLoginFormApiClient(driver, "user07@email.com", "user07");
        // COmprobamos que entramos en la pagina privada de usuario
        List<WebElement> result = PO_View.checkElementBy(driver, "text", "Titulo");

        Assertions.assertEquals("Titulo", result.get(0).getText());
    }

    //[Prueba49] Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
    @Test
    @Order(49)
    public void PR49() {
        driver.navigate().to("http://localhost:8081/apiclient/client.html?w=login");
        PO_LoginView.fillLoginFormApiClient(driver, "user07@email.com", "123");

        List<WebElement> result = PO_View.checkElementBy(driver, "text", "No se encuentra el usuario");

        Assertions.assertEquals("No se encuentra el usuario", result.get(0).getText());
    }

    //[Prueba50] Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
    @Test
    @Order(50)
    public void PR50() {
        driver.navigate().to("http://localhost:8081/apiclient/client.html?w=login");

        //Comprobamos error de contraseña vacia
        PO_LoginView.fillLoginFormApiClient(driver, "user07@email.com", "");
        List<WebElement> result = PO_View.checkElementBy(driver, "text", "La contrasena no debe ser vacia");
        Assertions.assertEquals("La contrasena no debe ser vacia", result.get(0).getText());

        //Comprobamos error de campo vacio
        PO_LoginView.fillLoginFormApiClient(driver, "", "user07");
        result = PO_View.checkElementBy(driver, "text", "El email no debe ser vacio");
        Assertions.assertEquals("El email no debe ser vacio", result.get(0).getText());
        result = PO_View.checkElementBy(driver, "text", "El email no es valido");
        Assertions.assertEquals("El email no es valido", result.get(0).getText());
    }

    //[Prueba51] Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que existen,
    //menos las del usuario identificado.
    @Test
    @Order(51)
    public void PR51() {
        driver.navigate().to("http://localhost:8081/apiclient/client.html?w=login");

        //Comprobamos error de contraseña vacia
        PO_LoginView.fillLoginFormApiClient(driver, "user10@email.com", "user10");
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//tbody/tr");
        Assertions.assertEquals(104, elements.size()); //En total hay 114 ofertas tras correr los tests, pero hay que restar las 10 ofertas del ususario 10
    }

    //[Prueba52] Sobre listado de ofertas disponibles (a elección de desarrollador), enviar un mensaje a una
    //oferta concreta. Se abriría dicha conversación por primera vez. Comprobar que el mensaje aparece
    //en el listado de mensajes.
    @Test
    @Order(52)
    public void PR52() {
        Assert.fail();
    }

    //[Prueba53] Sobre el listado de conversaciones enviar un mensaje a una conversación ya abierta.
    //Comprobar que el mensaje aparece en el listado de mensajes.
    @Test
    @Order(53)
    public void PR53() {
        Assert.fail();
    }

    //[Prueba54] Mostrar el listado de conversaciones ya abiertas. Comprobar que el listado contiene la
    //cantidad correcta de conversaciones.
    @Test
    @Order(54)
    public void PR54() {
        Assert.fail();
    }


    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
    public void prueba1() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
        Response response = RestAssured.get(RestAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());
    }


    public void prueba2() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parÃ¡metro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "prueba1@prueba1.com");
        requestParams.put("password", "prueba1");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la peticiÃ³n
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }

    //Métodos auxliares
    private void checkNotExists(String textToFind, int numOfPages) {
        boolean found = false;

        for (int page = 1; page <= numOfPages; page++) {
            try {
                //Cambiamos de pagina
                List<WebElement> pages = PO_View.checkElementBy(driver, "id", "a-" + page);
                pages.get(0).click();
                List<WebElement> elements;//En caso de que la oferta no se encuentre en una pagina, para que se busque en la siguiente
                elements = PO_View.checkElementBy(driver, "text", textToFind);
                if(found){
                    break;
                }
            } catch (Exception e) {
            }
        }

        Assert.assertFalse(found); //Comprobamos que found es true
    }

    private void createAndCheckOffer(String titulo, String detalle, String price) {
        //Rellenamos el formulario
        PO_OfferAddView.fillAddForm(driver, titulo, detalle, price, false);

        //Comprobamos que se ha creado la oferta
        checkExists(detalle, 3);
//        List<WebElement> result = PO_View.checkElementBy(driver, "text", detalle);
//        Assertions.assertEquals(detalle, result.get(0).getText());
    }

    private void checkExists(String textToFind, int numOfPages) {
        boolean found = false;

        for (int page = 1; page <= numOfPages; page++) {
            try {
                //Cambiamos de pagina
                List<WebElement> pages = PO_View.checkElementBy(driver, "id", "a-" + page);
                pages.get(0).click();
                List<WebElement> elements;//En caso de que la oferta no se encuentre en una pagina, para que se busque en la siguiente
                elements = PO_View.checkElementBy(driver, "text", textToFind);

                found = textToFind.equals(elements.get(0).getText());
                if(found){
                    break;
                }
            } catch (Exception e) {
            }
        }

        Assert.assertTrue(found); //Comprobamos que found es true
    }

    private WebElement findOffer(String textToFind, int numOfPages) {
        for (int page = 1; page <= numOfPages; page++) {
            try {
                //Cambiamos de pagina
                List<WebElement> pages = PO_View.checkElementBy(driver, "id", "a-" + page);
                pages.get(0).click();
                List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//tbody/tr");//En caso de que la oferta no se encuentre en una pagina, para que se busque en la siguiente
                elements = elements.stream().filter(element -> element.getText().contains(textToFind)).toList();
                return elements.get(0);
            } catch (Exception e) {
            }
        }
        throw new RuntimeException("No se ha encontrado la oferta");
    }
}

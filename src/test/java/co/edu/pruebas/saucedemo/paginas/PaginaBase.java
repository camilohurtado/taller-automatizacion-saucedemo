package co.edu.pruebas.saucedemo.paginas;

import co.edu.pruebas.saucedemo.base.Config;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Comportamiento comun de las paginas: esperas, localizadores data-test y encabezado. */
public abstract class PaginaBase {

    protected final WebDriver driver;
    protected final WebDriverWait espera;

    protected PaginaBase(WebDriver driver) {
        this.driver = driver;
        this.espera = new WebDriverWait(driver, Config.espera());
    }

    /** SauceDemo marca sus elementos con data-test: localizadores estables y legibles. */
    protected static By dataTest(String valor) {
        return By.cssSelector("[data-test=\"" + valor + "\"]");
    }

    protected WebElement visible(By localizador) {
        return espera.until(ExpectedConditions.visibilityOfElementLocated(localizador));
    }

    protected void escribir(By localizador, String texto) {
        WebElement campo = visible(localizador);
        campo.clear();
        if (texto != null) {
            campo.sendKeys(texto);
        }
    }

    protected List<String> textos(By localizador) {
        return driver.findElements(localizador).stream().map(WebElement::getText).toList();
    }

    public String titulo() {
        return visible(dataTest("title")).getText();
    }

    public String url() {
        return driver.getCurrentUrl();
    }

    // ---------------------------------------------------- encabezado comun

    /** Numero del icono del carrito; 0 cuando no hay insignia. */
    public int cantidadEnCarrito() {
        List<WebElement> insignia = driver.findElements(dataTest("shopping-cart-badge"));
        return insignia.isEmpty() ? 0 : Integer.parseInt(insignia.get(0).getText());
    }

    public CarritoPage irAlCarrito() {
        driver.findElement(dataTest("shopping-cart-link")).click();
        return new CarritoPage(driver).esperarCarga();
    }

    public LoginPage cerrarSesion() {
        driver.findElement(By.id("react-burger-menu-btn")).click();
        espera.until(ExpectedConditions.elementToBeClickable(dataTest("logout-sidebar-link"))).click();
        return new LoginPage(driver).esperarCarga();
    }
}

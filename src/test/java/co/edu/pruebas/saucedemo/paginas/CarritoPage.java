package co.edu.pruebas.saucedemo.paginas;

import co.edu.pruebas.saucedemo.datos.Producto;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Carrito de compras (/cart.html). */
public class CarritoPage extends PaginaBase {

    private static final By ITEM = dataTest("inventory-item");
    private static final By NOMBRE = dataTest("inventory-item-name");

    public CarritoPage(WebDriver driver) {
        super(driver);
    }

    CarritoPage esperarCarga() {
        espera.until(ExpectedConditions.urlContains("/cart.html"));
        visible(dataTest("checkout"));
        return this;
    }

    public List<String> productos() {
        return textos(NOMBRE);
    }

    public CarritoPage quitar(Producto producto) {
        int antes = driver.findElements(ITEM).size();
        driver.findElement(dataTest("remove-" + slug(producto))).click();
        espera.until(d -> d.findElements(ITEM).size() == antes - 1);
        return this;
    }

    public InventarioPage seguirComprando() {
        driver.findElement(dataTest("continue-shopping")).click();
        return new InventarioPage(driver).esperarCarga();
    }

    public CheckoutInfoPage pagar() {
        driver.findElement(dataTest("checkout")).click();
        return new CheckoutInfoPage(driver).esperarCarga();
    }

    /** "Sauce Labs Bike Light" -> "sauce-labs-bike-light" (convencion de data-test del sitio). */
    static String slug(Producto producto) {
        return producto.nombre().toLowerCase().replace(' ', '-');
    }
}

package co.edu.pruebas.saucedemo.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Confirmacion de la compra (/checkout-complete.html). */
public class CompraCompletaPage extends PaginaBase {

    public CompraCompletaPage(WebDriver driver) {
        super(driver);
    }

    CompraCompletaPage esperarCarga() {
        espera.until(ExpectedConditions.urlContains("/checkout-complete.html"));
        return this;
    }

    public String encabezado() {
        return visible(dataTest("complete-header")).getText();
    }

    public InventarioPage volverAlInicio() {
        driver.findElement(dataTest("back-to-products")).click();
        return new InventarioPage(driver).esperarCarga();
    }
}

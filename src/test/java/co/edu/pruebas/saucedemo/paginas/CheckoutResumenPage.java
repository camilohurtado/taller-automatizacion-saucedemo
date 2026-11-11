package co.edu.pruebas.saucedemo.paginas;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Paso 2 del pago: resumen y totales (/checkout-step-two.html). */
public class CheckoutResumenPage extends PaginaBase {

    public CheckoutResumenPage(WebDriver driver) {
        super(driver);
    }

    CheckoutResumenPage esperarCarga() {
        espera.until(ExpectedConditions.urlContains("/checkout-step-two.html"));
        visible(dataTest("finish"));
        return this;
    }

    public List<String> productos() {
        return textos(dataTest("inventory-item-name"));
    }

    public BigDecimal subtotal() {
        return valor("subtotal-label");
    }

    public BigDecimal impuesto() {
        return valor("tax-label");
    }

    public BigDecimal total() {
        return valor("total-label");
    }

    public CompraCompletaPage finalizar() {
        driver.findElement(dataTest("finish")).click();
        return new CompraCompletaPage(driver).esperarCarga();
    }

    /** "Item total: $29.99" -> 29.99 */
    private BigDecimal valor(String dataTest) {
        return InventarioPage.aDecimal(visible(dataTest(dataTest)).getText());
    }
}

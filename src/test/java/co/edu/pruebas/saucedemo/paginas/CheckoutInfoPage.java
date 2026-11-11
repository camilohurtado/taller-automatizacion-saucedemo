package co.edu.pruebas.saucedemo.paginas;

import co.edu.pruebas.saucedemo.datos.Cliente;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Paso 1 del pago: datos del cliente (/checkout-step-one.html). */
public class CheckoutInfoPage extends PaginaBase {

    private static final By NOMBRE = dataTest("firstName");
    private static final By APELLIDO = dataTest("lastName");
    private static final By POSTAL = dataTest("postalCode");
    private static final By CONTINUAR = dataTest("continue");
    private static final By ERROR = dataTest("error");

    public CheckoutInfoPage(WebDriver driver) {
        super(driver);
    }

    CheckoutInfoPage esperarCarga() {
        espera.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
        visible(CONTINUAR);
        return this;
    }

    public CheckoutInfoPage llenar(Cliente cliente) {
        escribir(NOMBRE, cliente.nombre());
        escribir(APELLIDO, cliente.apellido());
        escribir(POSTAL, cliente.codigoPostal());
        return this;
    }

    public CheckoutResumenPage continuar() {
        driver.findElement(CONTINUAR).click();
        return new CheckoutResumenPage(driver).esperarCarga();
    }

    public CheckoutInfoPage intentarContinuar() {
        driver.findElement(CONTINUAR).click();
        return this;
    }

    public String mensajeError() {
        return visible(ERROR).getText();
    }
}

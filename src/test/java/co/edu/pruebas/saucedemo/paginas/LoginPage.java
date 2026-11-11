package co.edu.pruebas.saucedemo.paginas;

import co.edu.pruebas.saucedemo.base.Config;
import co.edu.pruebas.saucedemo.datos.Usuario;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Pagina de ingreso (https://www.saucedemo.com/). */
public class LoginPage extends PaginaBase {

    private static final By USUARIO = dataTest("username");
    private static final By CLAVE = dataTest("password");
    private static final By BOTON = dataTest("login-button");
    private static final By ERROR = dataTest("error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage abrir() {
        driver.get(Config.urlBase());
        return esperarCarga();
    }

    LoginPage esperarCarga() {
        visible(BOTON);
        return this;
    }

    /** Camino feliz: ingresa y devuelve el catalogo. */
    public InventarioPage ingresarComo(Usuario usuario) {
        intentarIngreso(usuario.nombre(), Usuario.CLAVE);
        return new InventarioPage(driver).esperarCarga();
    }

    /** Ingreso que puede fallar: permanece en esta pagina. */
    public LoginPage intentarIngreso(String usuario, String clave) {
        escribir(USUARIO, usuario);
        escribir(CLAVE, clave);
        driver.findElement(BOTON).click();
        return this;
    }

    public String mensajeError() {
        return visible(ERROR).getText();
    }

    public boolean estaVisible() {
        return !driver.findElements(BOTON).isEmpty();
    }
}

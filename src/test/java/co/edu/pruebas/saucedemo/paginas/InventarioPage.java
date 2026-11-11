package co.edu.pruebas.saucedemo.paginas;

import co.edu.pruebas.saucedemo.datos.Orden;
import co.edu.pruebas.saucedemo.datos.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/** Catalogo de productos (/inventory.html). */
public class InventarioPage extends PaginaBase {

    private static final By ITEM = dataTest("inventory-item");
    private static final By NOMBRE = dataTest("inventory-item-name");
    private static final By PRECIO = dataTest("inventory-item-price");
    private static final By ORDEN = dataTest("product-sort-container");
    private static final By IMAGEN = By.cssSelector("[data-test=\"inventory-item\"] img");

    public InventarioPage(WebDriver driver) {
        super(driver);
    }

    InventarioPage esperarCarga() {
        espera.until(d -> d.getCurrentUrl().endsWith("/inventory.html") && !d.findElements(ITEM).isEmpty());
        return this;
    }

    public List<String> nombres() {
        return textos(NOMBRE);
    }

    /** Precios mostrados, convertidos de "$29.99" a 29.99. */
    public List<BigDecimal> precios() {
        return textos(PRECIO).stream().map(InventarioPage::aDecimal).toList();
    }

    public List<String> fuentesDeImagen() {
        return driver.findElements(IMAGEN).stream().map(i -> i.getAttribute("src")).toList();
    }

    public InventarioPage ordenarPor(Orden orden) {
        new Select(driver.findElement(ORDEN)).selectByValue(orden.valor());
        return this;
    }

    public InventarioPage agregar(Producto producto) {
        boton(producto).click();
        return this;
    }

    public InventarioPage quitar(Producto producto) {
        return agregar(producto); // el mismo boton alterna "Add to cart" / "Remove"
    }

    public String textoBoton(Producto producto) {
        return boton(producto).getText();
    }

    private WebElement boton(Producto producto) {
        return driver.findElements(ITEM).stream()
                .filter(item -> item.findElement(NOMBRE).getText().equals(producto.nombre()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Producto no listado: " + producto.nombre()))
                .findElement(By.tagName("button"));
    }

    static BigDecimal aDecimal(String texto) {
        return new BigDecimal(texto.replaceAll("[^0-9.]", ""));
    }
}

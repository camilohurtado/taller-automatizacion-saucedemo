package co.edu.pruebas.saucedemo.pruebas;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.edu.pruebas.saucedemo.base.BaseTest;
import co.edu.pruebas.saucedemo.datos.Cliente;
import co.edu.pruebas.saucedemo.datos.Producto;
import co.edu.pruebas.saucedemo.datos.Usuario;
import co.edu.pruebas.saucedemo.paginas.CarritoPage;
import co.edu.pruebas.saucedemo.paginas.CheckoutInfoPage;
import co.edu.pruebas.saucedemo.paginas.CheckoutResumenPage;
import co.edu.pruebas.saucedemo.paginas.CompraCompletaPage;
import co.edu.pruebas.saucedemo.paginas.InventarioPage;
import co.edu.pruebas.saucedemo.paginas.LoginPage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Carrito y proceso de compra")
class CompraTest extends BaseTest {

    private static final BigDecimal TASA_IMPUESTO = new BigDecimal("0.08");

    private InventarioPage ingresar() {
        return new LoginPage(driver).abrir().ingresarComo(Usuario.ESTANDAR);
    }

    @Test
    @DisplayName("CP-CAR-01 El carrito conserva los productos y permite quitarlos")
    void carrito() {
        CarritoPage carrito = ingresar()
                .agregar(Producto.MORRAL).agregar(Producto.CHAQUETA).agregar(Producto.MAMELUCO)
                .irAlCarrito();
        capturar("carrito_tres_productos");
        assertEquals(List.of(Producto.MORRAL.nombre(), Producto.CHAQUETA.nombre(), Producto.MAMELUCO.nombre()),
                carrito.productos());

        carrito.quitar(Producto.CHAQUETA);
        capturar("carrito_sin_chaqueta");
        assertAll(
                () -> assertEquals(List.of(Producto.MORRAL.nombre(), Producto.MAMELUCO.nombre()), carrito.productos()),
                () -> assertEquals(2, carrito.cantidadEnCarrito()));

        // Al seguir comprando y regresar, el carrito mantiene su contenido
        CarritoPage deNuevo = carrito.seguirComprando().irAlCarrito();
        assertEquals(2, deNuevo.productos().size());
    }

    @Test
    @DisplayName("CP-COM-01 Compra completa: totales correctos (impuesto 8 %) y confirmacion")
    void compraCompleta() {
        List<Producto> compra = List.of(Producto.MORRAL, Producto.LUZ_BICICLETA, Producto.CAMISETA_BOLT);
        InventarioPage catalogo = ingresar();
        compra.forEach(catalogo::agregar);

        CheckoutInfoPage datos = catalogo.irAlCarrito().pagar().llenar(Cliente.valido());
        capturar("datos_cliente");
        CheckoutResumenPage resumen = datos.continuar();
        capturar("resumen");

        // Oraculo independiente: se calcula con los precios del catalogo esperado
        BigDecimal subtotal = compra.stream().map(Producto::precio).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal impuesto = subtotal.multiply(TASA_IMPUESTO).setScale(2, RoundingMode.HALF_UP);
        assertAll(
                () -> assertEquals(compra.stream().map(Producto::nombre).toList(), resumen.productos()),
                () -> assertEquals(subtotal, resumen.subtotal(), "Subtotal"),
                () -> assertEquals(impuesto, resumen.impuesto(), "Impuesto"),
                () -> assertEquals(subtotal.add(impuesto), resumen.total(), "Total"));

        CompraCompletaPage confirmacion = resumen.finalizar();
        capturar("confirmacion");
        assertAll(
                () -> assertEquals("Thank you for your order!", confirmacion.encabezado()),
                () -> assertEquals(0, confirmacion.cantidadEnCarrito(), "El carrito debe quedar vacio"));
    }

    @ParameterizedTest(name = "[{index}] {3}")
    @CsvSource(delimiter = '|', nullValues = "NULO", textBlock = """
            # nombre | apellido | postal  | mensaje esperado
            NULO     | Hurtado  | 110111  | Error: First Name is required
            Camilo   | NULO     | 110111  | Error: Last Name is required
            Camilo   | Hurtado  | NULO    | Error: Postal Code is required
            """)
    @DisplayName("CP-COM-02 Los datos del cliente son obligatorios")
    void datosObligatorios(String nombre, String apellido, String postal, String mensajeEsperado) {
        CheckoutInfoPage datos = ingresar().agregar(Producto.MAMELUCO).irAlCarrito().pagar()
                .llenar(new Cliente(nombre, apellido, postal))
                .intentarContinuar();
        String mensaje = datos.mensajeError();
        capturar("error");

        assertAll(
                () -> assertEquals(mensajeEsperado, mensaje),
                () -> assertEquals(true, datos.url().contains("checkout-step-one"), "No debe avanzar al resumen"));
    }
}

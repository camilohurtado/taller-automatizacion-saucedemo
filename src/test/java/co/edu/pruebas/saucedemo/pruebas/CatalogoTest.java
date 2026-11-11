package co.edu.pruebas.saucedemo.pruebas;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.edu.pruebas.saucedemo.base.BaseTest;
import co.edu.pruebas.saucedemo.datos.Orden;
import co.edu.pruebas.saucedemo.datos.Producto;
import co.edu.pruebas.saucedemo.datos.Usuario;
import co.edu.pruebas.saucedemo.paginas.InventarioPage;
import co.edu.pruebas.saucedemo.paginas.LoginPage;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Catalogo de productos")
class CatalogoTest extends BaseTest {

    private InventarioPage catalogo;

    @BeforeEach
    void ingresar() {
        catalogo = new LoginPage(driver).abrir().ingresarComo(Usuario.ESTANDAR);
    }

    @Test
    @DisplayName("CP-CAT-01 Muestra los 6 productos con el precio correcto")
    void productosYPrecios() {
        Map<String, BigDecimal> esperado = Arrays.stream(Producto.values())
                .collect(Collectors.toMap(Producto::nombre, Producto::precio));
        List<String> nombres = catalogo.nombres();
        List<BigDecimal> precios = catalogo.precios();
        capturar("catalogo");

        assertEquals(esperado.size(), nombres.size());
        for (int i = 0; i < nombres.size(); i++) {
            assertEquals(esperado.get(nombres.get(i)), precios.get(i), "Precio de " + nombres.get(i));
        }
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @EnumSource(Orden.class)
    @DisplayName("CP-CAT-02 Ordenar el catalogo")
    void ordenar(Orden orden) {
        catalogo.ordenarPor(orden);
        capturar(orden.valor());

        switch (orden) {
            case NOMBRE_A_Z -> assertEquals(ordenado(catalogo.nombres(), Comparator.naturalOrder()), catalogo.nombres());
            case NOMBRE_Z_A -> assertEquals(ordenado(catalogo.nombres(), Comparator.reverseOrder()), catalogo.nombres());
            case PRECIO_MENOR_A_MAYOR ->
                    assertEquals(ordenado(catalogo.precios(), Comparator.naturalOrder()), catalogo.precios());
            case PRECIO_MAYOR_A_MENOR ->
                    assertEquals(ordenado(catalogo.precios(), Comparator.reverseOrder()), catalogo.precios());
        }
    }

    @Test
    @DisplayName("CP-CAT-03 Agregar y quitar productos actualiza el boton y el contador del carrito")
    void agregarYQuitar() {
        catalogo.agregar(Producto.MORRAL).agregar(Producto.LUZ_BICICLETA);
        capturar("dos_agregados");
        assertAll(
                () -> assertEquals(2, catalogo.cantidadEnCarrito()),
                () -> assertEquals("Remove", catalogo.textoBoton(Producto.MORRAL)));

        catalogo.quitar(Producto.MORRAL);
        capturar("uno_quitado");
        assertAll(
                () -> assertEquals(1, catalogo.cantidadEnCarrito()),
                () -> assertEquals("Add to cart", catalogo.textoBoton(Producto.MORRAL)));
    }

    /** Oraculo: la misma lista ordenada por Java. */
    private static <T> List<T> ordenado(List<T> lista, Comparator<? super T> criterio) {
        return lista.stream().sorted(criterio).toList();
    }
}

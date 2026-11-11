package co.edu.pruebas.saucedemo.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.pruebas.saucedemo.base.BaseTest;
import co.edu.pruebas.saucedemo.datos.Cliente;
import co.edu.pruebas.saucedemo.datos.Orden;
import co.edu.pruebas.saucedemo.datos.Producto;
import co.edu.pruebas.saucedemo.datos.Usuario;
import co.edu.pruebas.saucedemo.paginas.InventarioPage;
import co.edu.pruebas.saucedemo.paginas.LoginPage;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Regresion por perfil de usuario: las MISMAS verificaciones se ejecutan con cada usuario
 * de SauceDemo. El usuario estandar es la referencia; los demas tienen defectos sembrados
 * por el sitio, y el objetivo es demostrar que la suite los detecta.
 *
 * <p>Se excluye de la ejecucion normal (falla a proposito). Ejecutar con: mvn test -Pdefectos
 */
@Tag("defectos")
@DisplayName("Regresion por perfil de usuario (deteccion de defectos)")
class RegresionPorUsuarioTest extends BaseTest {

    private static final Duration TIEMPO_MAXIMO_INGRESO = Duration.ofSeconds(3);

    private InventarioPage ingresar(Usuario usuario) {
        return new LoginPage(driver).abrir().ingresarComo(usuario);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @EnumSource(value = Usuario.class, names = "BLOQUEADO", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("DEF-01 Cada producto muestra su propia imagen")
    void imagenesDistintas(Usuario usuario) {
        List<String> imagenes = ingresar(usuario).fuentesDeImagen();
        capturar("catalogo");
        assertEquals(6, new HashSet<>(imagenes).size(), "Imagenes distintas en el catalogo: " + imagenes);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @EnumSource(value = Usuario.class, names = "BLOQUEADO", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("DEF-02 Los precios del catalogo coinciden con los esperados")
    void preciosCorrectos(Usuario usuario) {
        InventarioPage catalogo = ingresar(usuario);
        Map<String, BigDecimal> esperado = Arrays.stream(Producto.values())
                .collect(Collectors.toMap(Producto::nombre, Producto::precio));
        List<String> nombres = catalogo.nombres();
        List<BigDecimal> precios = catalogo.precios();
        for (int i = 0; i < nombres.size(); i++) {
            assertEquals(esperado.get(nombres.get(i)), precios.get(i), "Precio de " + nombres.get(i));
        }
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @EnumSource(value = Usuario.class, names = "BLOQUEADO", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("DEF-03 Ordenar por precio de menor a mayor")
    void ordenarPorPrecio(Usuario usuario) {
        InventarioPage catalogo = ingresar(usuario).ordenarPor(Orden.PRECIO_MENOR_A_MAYOR);
        List<BigDecimal> precios = catalogo.precios();
        capturar("orden_precio");
        assertEquals(precios.stream().sorted().toList(), precios, "El catalogo no quedo ordenado por precio");
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @EnumSource(value = Usuario.class, names = "BLOQUEADO", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("DEF-04 Completar una compra de un producto")
    void compraCompleta(Usuario usuario) {
        String encabezado = ingresar(usuario).agregar(Producto.MORRAL).irAlCarrito().pagar()
                .llenar(Cliente.valido()).continuar().finalizar().encabezado();
        assertEquals("Thank you for your order!", encabezado);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @EnumSource(value = Usuario.class, names = "BLOQUEADO", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("DEF-05 El ingreso responde en menos de 3 segundos")
    void tiempoDeIngreso(Usuario usuario) {
        LoginPage login = new LoginPage(driver).abrir();
        long inicio = System.nanoTime();
        login.ingresarComo(usuario);
        Duration tiempo = Duration.ofNanos(System.nanoTime() - inicio);
        assertTrue(tiempo.compareTo(TIEMPO_MAXIMO_INGRESO) < 0,
                "El ingreso tardo " + tiempo.toMillis() + " ms (maximo " + TIEMPO_MAXIMO_INGRESO.toMillis() + " ms)");
    }
}

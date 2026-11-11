package co.edu.pruebas.saucedemo.pruebas;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.pruebas.saucedemo.base.BaseTest;
import co.edu.pruebas.saucedemo.base.Config;
import co.edu.pruebas.saucedemo.datos.Usuario;
import co.edu.pruebas.saucedemo.paginas.InventarioPage;
import co.edu.pruebas.saucedemo.paginas.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Autenticacion y sesion")
class LoginTest extends BaseTest {

    @Test
    @DisplayName("CP-LOG-01 Usuario estandar ingresa y ve el catalogo")
    void ingresoExitoso() {
        LoginPage login = new LoginPage(driver).abrir();
        capturar("pagina_ingreso");

        InventarioPage catalogo = login.ingresarComo(Usuario.ESTANDAR);
        capturar("catalogo");

        assertAll(
                () -> assertTrue(catalogo.url().endsWith("/inventory.html")),
                () -> assertEquals("Products", catalogo.titulo()),
                () -> assertEquals(6, catalogo.nombres().size()));
    }

    @ParameterizedTest(name = "[{index}] {3}")
    @CsvSource(delimiter = '|', nullValues = "NULO", textBlock = """
            # usuario         | clave          | mensaje esperado                                                            | caso
            locked_out_user   | secret_sauce   | Epic sadface: Sorry, this user has been locked out.                         | Usuario bloqueado
            standard_user     | clave_erronea  | Epic sadface: Username and password do not match any user in this service   | Clave incorrecta
            NULO              | secret_sauce   | Epic sadface: Username is required                                          | Usuario vacio
            standard_user     | NULO           | Epic sadface: Password is required                                          | Clave vacia
            """)
    @DisplayName("CP-LOG-02 Ingreso rechazado muestra el mensaje adecuado")
    void ingresoRechazado(String usuario, String clave, String mensajeEsperado, String caso) {
        LoginPage login = new LoginPage(driver).abrir().intentarIngreso(usuario, clave);
        String mensaje = login.mensajeError();
        capturar("error");

        assertAll(
                () -> assertEquals(mensajeEsperado, mensaje),
                () -> assertTrue(login.estaVisible(), "Debe permanecer en la pagina de ingreso"));
    }

    @Test
    @DisplayName("CP-LOG-03 El catalogo no es accesible sin iniciar sesion")
    void accesoDirectoSinSesion() {
        driver.get(Config.urlBase() + "/inventory.html");
        LoginPage login = new LoginPage(driver);
        String mensaje = login.mensajeError();
        capturar("acceso_bloqueado");

        assertEquals("Epic sadface: You can only access '/inventory.html' when you are logged in.", mensaje);
    }

    @Test
    @DisplayName("CP-LOG-04 Cerrar sesion vuelve al ingreso y bloquea el catalogo")
    void cerrarSesion() {
        LoginPage login = new LoginPage(driver).abrir().ingresarComo(Usuario.ESTANDAR).cerrarSesion();
        capturar("sesion_cerrada");
        assertTrue(login.estaVisible());

        driver.navigate().back(); // intentar volver con el boton "atras" del navegador
        assertTrue(new LoginPage(driver).mensajeError().contains("when you are logged in"),
                "Tras cerrar sesion, volver atras no debe mostrar el catalogo");
    }
}

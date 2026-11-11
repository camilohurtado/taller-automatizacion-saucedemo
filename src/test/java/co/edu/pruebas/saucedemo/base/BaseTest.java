package co.edu.pruebas.saucedemo.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Infraestructura comun de todas las pruebas.
 *
 * <ul>
 *   <li>Un navegador Chrome limpio por prueba (independencia entre casos y ejecucion en paralelo).</li>
 *   <li>Capturas de evidencia en {@code target/evidencias/<Clase>/}.</li>
 *   <li>Captura automatica cuando una prueba falla, tomada antes de cerrar el navegador.</li>
 * </ul>
 */
public abstract class BaseTest {

    private static final Path EVIDENCIAS = Path.of("target", "evidencias");
    private static final AtomicInteger CONSECUTIVO = new AtomicInteger();
    private static final Logger LOG_SELENIUM = silenciar("org.openqa.selenium");
    private static final Pattern INDICE = Pattern.compile("^\\[(\\d+)\\]");

    protected WebDriver driver;
    private String clase;
    private String metodo;

    /**
     * AfterTestExecutionCallback se ejecuta justo despues del metodo de prueba y ANTES
     * de los @AfterEach, cuando el navegador sigue abierto: permite capturar la falla.
     */
    @RegisterExtension
    final AfterTestExecutionCallback capturaEnFallo = contexto -> {
        if (contexto.getExecutionException().isPresent() && driver != null) {
            try {
                driver.switchTo().alert().dismiss(); // una alerta abierta impide la captura
            } catch (NoAlertPresentException sinAlerta) {
                // caso normal
            }
            capturar("FALLO");
        }
    };

    @BeforeEach
    void abrirNavegador(TestInfo info) {
        clase = info.getTestClass().map(Class::getSimpleName).orElse("Prueba");
        metodo = info.getTestMethod().map(m -> m.getName()).orElse("prueba");
        // En pruebas parametrizadas el nombre visible empieza por "[n]": se agrega el indice
        Matcher indice = INDICE.matcher(info.getDisplayName());
        if (indice.find()) {
            metodo += "_" + indice.group(1);
        }
        ChromeOptions opciones = new ChromeOptions();
        if (Config.headless()) {
            opciones.addArguments("--headless=new");
        }
        opciones.addArguments("--window-size=1280,900", "--lang=es", "--disable-features=PasswordLeakDetection");
        // Evita el aviso de "contrasena filtrada" de Chrome, que bloquea la pagina tras el login
        opciones.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        driver = new ChromeDriver(opciones); // Selenium Manager resuelve chromedriver
    }

    @AfterEach
    void cerrarNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Guarda una captura numerada: target/evidencias/Clase/NN_metodo_etiqueta.png */
    protected void capturar(String etiqueta) {
        try {
            Path carpeta = Files.createDirectories(EVIDENCIAS.resolve(clase));
            Path destino = carpeta.resolve(String.format("%02d_%s_%s.png",
                    CONSECUTIVO.incrementAndGet(), metodo, etiqueta));
            Path temporal = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la captura " + etiqueta, e);
        }
    }

    private static Logger silenciar(String nombre) {
        Logger logger = Logger.getLogger(nombre);
        logger.setLevel(Level.SEVERE); // oculta avisos de version de CDP, que no se usa
        return logger;
    }
}

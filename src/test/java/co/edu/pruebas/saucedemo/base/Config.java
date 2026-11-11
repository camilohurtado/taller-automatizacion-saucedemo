package co.edu.pruebas.saucedemo.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/** Lee config.properties; una propiedad del sistema (-Dclave=valor) tiene prioridad. */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
            PROPS.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer config.properties", e);
        }
    }

    private Config() {
    }

    private static String valor(String clave) {
        return System.getProperty(clave, PROPS.getProperty(clave));
    }

    public static String urlBase() {
        return valor("base.url");
    }

    public static boolean headless() {
        return !"false".equalsIgnoreCase(valor("headless"));
    }

    public static Duration espera() {
        return Duration.ofSeconds(Long.parseLong(valor("espera.segundos")));
    }
}

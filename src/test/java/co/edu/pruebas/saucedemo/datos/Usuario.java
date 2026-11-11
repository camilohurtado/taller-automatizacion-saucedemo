package co.edu.pruebas.saucedemo.datos;

/** Usuarios publicos de SauceDemo; cada uno tiene un comportamiento distinto. */
public enum Usuario {
    ESTANDAR("standard_user"),
    BLOQUEADO("locked_out_user"),
    CON_PROBLEMAS("problem_user"),
    LENTO("performance_glitch_user"),
    CON_ERRORES("error_user"),
    VISUAL("visual_user");

    /** Clave comun publicada en la pagina de ingreso de SauceDemo. */
    public static final String CLAVE = "secret_sauce";

    private final String nombre;

    Usuario(String nombre) {
        this.nombre = nombre;
    }

    public String nombre() {
        return nombre;
    }
}

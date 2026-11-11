package co.edu.pruebas.saucedemo.datos;

/** Opciones del selector de orden del catalogo (atributo value del option). */
public enum Orden {
    NOMBRE_A_Z("az"),
    NOMBRE_Z_A("za"),
    PRECIO_MENOR_A_MAYOR("lohi"),
    PRECIO_MAYOR_A_MENOR("hilo");

    private final String valor;

    Orden(String valor) {
        this.valor = valor;
    }

    public String valor() {
        return valor;
    }
}

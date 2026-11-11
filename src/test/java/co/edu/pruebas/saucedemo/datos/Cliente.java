package co.edu.pruebas.saucedemo.datos;

/** Datos de envio del checkout. */
public record Cliente(String nombre, String apellido, String codigoPostal) {

    public static Cliente valido() {
        return new Cliente("Camilo", "Hurtado", "110111");
    }
}

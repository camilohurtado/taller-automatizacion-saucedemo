package co.edu.pruebas.saucedemo.datos;

import java.math.BigDecimal;

/** Catalogo esperado (oraculo de prueba): nombre y precio de cada producto. */
public enum Producto {
    MORRAL("Sauce Labs Backpack", "29.99"),
    LUZ_BICICLETA("Sauce Labs Bike Light", "9.99"),
    CAMISETA_BOLT("Sauce Labs Bolt T-Shirt", "15.99"),
    CHAQUETA("Sauce Labs Fleece Jacket", "49.99"),
    MAMELUCO("Sauce Labs Onesie", "7.99"),
    CAMISETA_ROJA("Test.allTheThings() T-Shirt (Red)", "15.99");

    private final String nombre;
    private final BigDecimal precio;

    Producto(String nombre, String precio) {
        this.nombre = nombre;
        this.precio = new BigDecimal(precio);
    }

    public String nombre() {
        return nombre;
    }

    public BigDecimal precio() {
        return precio;
    }
}

# Taller AA2 – Automatización de pruebas funcionales (SauceDemo)

Materia: **Pruebas de Calidad de Software** · Actividad AA2 – Taller de automatización.

Suite de pruebas funcionales automatizadas con **Selenium WebDriver + JUnit 5** sobre la tienda
de práctica pública [SauceDemo / Swag Labs](https://www.saucedemo.com) (entorno real).

## Requisitos

- JDK 21 y Maven 3.9+
- Google Chrome (Selenium Manager descarga el `chromedriver` compatible)
- Conexión a internet

## Ejecución

```bash
mvn test                    # suite funcional: 18 pruebas (usuario estándar)
mvn test -Pdefectos         # regresión por perfil de usuario: expone defectos sembrados
mvn test -Dheadless=false   # ver el navegador durante la ejecución
mvn test -Dtest=CompraTest  # una sola clase
```

Evidencias: `target/evidencias/<Clase>/NN_metodo_etiqueta.png` (incluye captura automática
`*_FALLO.png` de cada prueba fallida). Reportes: `target/surefire-reports/`.

## Estructura

```
src/test/java/co/edu/pruebas/saucedemo/
├── base/      BaseTest (navegador por prueba, capturas), Config
├── datos/     Usuario, Producto (oráculo de precios), Cliente, Orden
├── paginas/   Page Objects: Login, Inventario, Carrito, CheckoutInfo, CheckoutResumen, CompraCompleta
└── pruebas/   LoginTest, CatalogoTest, CompraTest, RegresionPorUsuarioTest (@Tag "defectos")
src/test/resources/config.properties   URL base, headless, tiempo de espera
```

## Convenciones

- Un caso = un id (`CP-LOG-01`, `DEF-03`…) en el `@DisplayName`.
- Localizadores por atributo `data-test`; esperas explícitas, nunca `Thread.sleep`.
- Ramas `feature/<nombre>` y revisión por pull request antes de integrar a `main`.

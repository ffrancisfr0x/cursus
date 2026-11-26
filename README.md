# Cursus

Un gestor de candidaturas de empleo.

Sirve para llevar el control de los sitios donde has enviado el CV: en qué empresa, para qué puesto, en qué estado está cada proceso, cuándo lo enviaste y qué entrevistas has tenido.

Proyecto del **Ejercicio Integrador del 1.er trimestre** de 2.º DAM (Acceso a Datos y Desarrollo de Interfaces).

**Autores:** Francis Romero Flores y Antonio Tienda Díaz

## Versiones

| Versión | Contenido | Etiqueta |
|---|---|---|
| v1.0 | Archivo de texto + línea de comandos + prototipo en Figma y manual de estilo | `v1.0` |
| v2.0 | Base de datos MySQL con JDBC + interfaz gráfica JavaFX diseñada con Scene Builder | `v2.0` |

## Tecnologías

- Java 27 y Maven
- MySQL 8.4 (en Docker) con JDBC
- JavaFX con pantallas FXML diseñadas en Scene Builder
- JUnit 5, Mockito y H2 (base de datos en memoria para las pruebas)
- IntelliJ IDEA, Docker Desktop y Figma

---

## v2.0 · Base de datos + JavaFX

### Cómo ejecutarlo

Necesitas IntelliJ IDEA, el JDK 27 y Docker Desktop.

1. **Arranca MySQL.** En la carpeta del proyecto, ejecuta:
   ```
   docker compose up -d
   ```
   Crea el contenedor `cursus-mysql` con MySQL 8.4 en el puerto **3308**. La primera vez ejecuta solo [`sql/schema.sql`](sql/schema.sql) y crea las tablas `empresa`, `oferta` y `entrevista`.
2. **Conexión.** El repositorio incluye `config.properties` con los datos del contenedor (puerto 3308, usuario y contraseña `cursus`), así que funciona sin tocar nada. Si usas otro MySQL, cambia ahí la URL, el usuario y la contraseña.
3. **Abre el proyecto** en IntelliJ (**File → Open**) y deja que Maven descargue las dependencias.
4. **Ejecuta** la clase `com.cursus.Main`.
5. En la pantalla **Candidaturas**, pulsa **Importar CSV** y elige [`data/candidaturas-prueba.csv`](data/candidaturas-prueba.csv). Se cargan las 15 candidaturas de prueba y se crean sus empresas.

Para empezar de cero, vuelve a ejecutar `sql/schema.sql` desde la pestaña **Database** de IntelliJ, o recrea el contenedor:
```
docker compose down -v
docker compose up -d
```

### Qué puedes hacer

| HU | Qué hace | Dónde |
|---|---|---|
| HU-09 | Ver las candidaturas en una tabla; se ordena pulsando la cabecera de cada columna | Candidaturas |
| HU-10 | Gestionar empresas: tabla y formulario de alta y edición en la misma pantalla | Empresas |
| HU-11 | Ver y gestionar las entrevistas de cada candidatura en un panel desplegable | Detalle de la candidatura |
| HU-12 | Cambiar el estado desde un desplegable en la propia tabla (doble clic en el estado) | Candidaturas |
| HU-13 | Filtrar combinando puesto, empresa, estado, salario mínimo y máximo y fechas | Candidaturas |

Además:

- Alta y edición de candidaturas, empresas y entrevistas con **validación**: los campos obligatorios vacíos se marcan en rojo y los datos no válidos (fecha futura, salario negativo, enlace sin `http`…) muestran un mensaje.
- Borrado con confirmación. Al borrar una empresa se borran también sus candidaturas y sus entrevistas.
- Al añadir una entrevista a una candidatura «pendiente», pasa sola a «entrevista».
- Si la base de datos falla, la aplicación avisa con un mensaje y no se cierra.

### Base de datos

```
EMPRESA    (id, nombre, sector, web, fechaRegistro)
OFERTA     (id, id_empresa → EMPRESA, titulo, descripcion, estado, fechaPostulacion, enlace, salario, notas)
ENTREVISTA (id, id_oferta → OFERTA, fecha, tipo, notas, resultado)
```

- Las consultas de candidaturas hacen **JOIN** con `empresa` para traer los datos de su empresa.
- Los filtros se combinan como se quiera: solo se añaden a la consulta los criterios que se rellenan.
- Usan **transacciones** las operaciones que tocan varias tablas:
    - importar el CSV (crea las empresas que faltan y las candidaturas);
    - borrar una candidatura (sus entrevistas y la candidatura);
    - borrar una empresa (las entrevistas de sus candidaturas, sus candidaturas y la empresa);
    - añadir una entrevista (si la candidatura estaba pendiente, cambia su estado).
- La conexión está **externalizada** en `config.properties`, fuera del código.

### Estructura

```
src/main/java/com/cursus/
├── Main.java          arranca la aplicación JavaFX
├── model/             Empresa, Oferta, Entrevista, Estado, FiltroOferta
├── db/                ConexionBD             lee config.properties y abre conexiones
├── dao/               EmpresaDAO, OfertaDAO, EntrevistaDAO   consultas JDBC
├── service/           lógica, validaciones y transacciones
└── ui/                controladores JavaFX, navegación y avisos

src/main/resources/com/cursus/ui/
├── principal.fxml, ofertas.fxml, detalle-oferta.fxml, empresas.fxml
├── formulario-oferta.fxml, formulario-entrevista.fxml
└── estilos.css        paleta y tipografía del manual de estilo

src/test/java/com/cursus/service/
├── OfertaServiceTest      reglas del servicio con Mockito
└── BaseDatosH2Test        consultas y transacciones con H2 en memoria

sql/schema.sql             creación del esquema
docker-compose.yml         MySQL para desarrollo
```

Para lanzar las pruebas: clic derecho en `src/test/java` → **Run 'All Tests'**. No necesitan MySQL: usan H2 en memoria.

---

## v1.0 · Archivo de texto + línea de comandos

La v1.0 está guardada en la etiqueta `v1.0`. Para verla:
```
git checkout v1.0
```

### Cómo ejecutarla

1. Abre el proyecto en IntelliJ. Si lo pide, recarga Maven.
2. Arriba a la derecha, en el desplegable de configuraciones, elige **01 importar CSV** y pulsa ▶. Así se cargan las 15 candidaturas de prueba.
3. Prueba el resto de configuraciones en orden (`02 listar`, `03 añadir`…). Cada una ejecuta un comando.
4. Para probar otro comando, ve a **Edit Configurations…** y cambia los **Program arguments**.

La clase principal es `com.cursus.Main`. El programa no tiene menú: recibe el comando como argumento, lo ejecuta y termina. Para empezar de cero, borra `data/candidaturas.txt` y vuelve a importar el CSV.

### Comandos

| HU | Qué hace | Comando |
|---|---|---|
| HU-01 | Importar candidaturas desde un CSV | `-importar data/candidaturas-prueba.csv` |
| HU-02 | Listar todas las candidaturas | `-listar` |
| HU-03 | Filtrar por estado y/o empresa | `-listar --estado pendiente --empresa Indra` |
| HU-04 | Añadir una candidatura | `-añadir --titulo "Dev Java" --empresa "Indra" --estado pendiente` |
| HU-05 | Ver el detalle de una candidatura | `-ver 13` |
| HU-06 | Eliminar una candidatura | `-borrar 13` |

- Los estados válidos son `pendiente`, `entrevista`, `rechazado` y `aceptado`.
- En `-listar`, `--estado` y `--empresa` son opcionales. La empresa se busca sin distinguir mayúsculas y basta con parte del nombre (`--empresa tech` encuentra TechCorp).
- `-añadir` admite además `--fecha dd/MM/yyyy` (si no se indica, se usa la de hoy), `--enlace` y `--notas`.
- Si un texto lleva espacios, va entre comillas: `--titulo "Dev Java"`.
- Si la consola no muestra bien la ñ, `-anadir` funciona igual que `-añadir`.

Si falta un argumento, la fecha no existe, el ID no existe o no se encuentra el archivo, el programa muestra un mensaje de error y termina sin romperse:
```
Error: No existe ninguna candidatura con el ID 99.
```

### Datos

Las candidaturas se guardan en `data/candidaturas.txt`, una por línea y con los campos separados por punto y coma:
```
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
2;Ingeniero de Software;Innovatech;entrevista;20/08/2026;https://innovatech.io/jobs/2;CV enviado por portal web
```

El archivo se crea solo la primera vez que se guarda algo y no se sube al repositorio.

---

## Datos de prueba

[`data/candidaturas-prueba.csv`](data/candidaturas-prueba.csv): 15 candidaturas de 5 empresas. Sirve para las dos versiones: en la v1.0 con `-importar` y en la v2.0 con el botón **Importar CSV**.

## Diseño

El prototipo de Figma (HU-07), el PDF de las pantallas y el manual de estilo (HU-08) se entregan en Teams. En la v2.0, la paleta, la tipografía y los componentes del manual se aplican en `estilos.css`.

## Quién ha hecho qué

### v1.0

#### Francis Romero Flores
HU-01, HU-02, HU-03 y HU-07

- Proyecto Maven, `model`, `dao` y `Main`.
- Estructura de `service`.
- Comando `-importar` CSV.
- Javadoc de su parte.
- Prototipo en Figma.
- Manual de estilo.

#### Antonio Tienda Díaz
HU-04, HU-05, HU-06 y HU-08

- Comandos `-añadir`, `-ver` y `-borrar`.
- Pruebas JUnit y CSV de prueba.
- Configuraciones de ejecución.
- Estructura de `controller`.
- Javadoc de su parte.
- README.
- Prototipo en Figma.

### v2.0

#### Francis Romero Flores
Acceso a Datos (parte de datos de HU-09 a HU-13)

- Esquema MySQL (`schema.sql`) y `docker-compose.yml`.
- Conexión con `config.properties`.
- Modelo: `Empresa`, `Oferta`, `Entrevista` y `FiltroOferta`.
- DAO con JDBC: consultas con JOIN y filtros combinados.
- Servicios con validaciones y transacciones.
- Importación del CSV a la base de datos.
- Pruebas con Mockito y con H2.
- Javadoc de su parte.
- README.

#### Antonio Tienda Díaz
Desarrollo de Interfaces (parte visual de HU-09 a HU-13)

- Pantallas FXML con Scene Builder: principal, candidaturas, detalle, empresas y formularios.
- Hoja de estilos con el manual de estilo.
- Arranque de JavaFX y navegación entre pantallas.
- Controladores: tablas, filtros, cambio de estado y panel de entrevistas.
- Validación de los formularios.
- Gestión de empresas.
- Javadoc de su parte.
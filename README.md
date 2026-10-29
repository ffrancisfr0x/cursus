# Cursus

Un gestor de candidaturas de empleo que se usa desde la línea de comandos.

Sirve para llevar el control de los sitios donde has enviado el CV: en qué empresa, para qué puesto, en qué estado está cada proceso y cuándo lo enviaste. Todo queda guardado en un archivo de texto y se consulta con comandos sencillos.

Proyecto del **Ejercicio Integrador del 1.er trimestre** de 2.º DAM (Acceso a Datos y Desarrollo de Interfaces).

**Autores:** Francis Romero Flores y Antonio Tienda Díaz
**Versión:** v1.0 (archivos de texto, línea de comandos y diseño en Figma)

## Tecnologías

- Java 27
- Maven
- JUnit 5
- IntelliJ IDEA
- Figma

## Cómo ejecutarlo

Necesitas IntelliJ IDEA y el JDK 27.

1. Clona el repositorio y ábrelo en IntelliJ (**File → Open**). Si IntelliJ lo pide, recarga Maven.
2. Arriba a la derecha, en el desplegable de configuraciones, elige **01 importar CSV** y pulsa ▶. Así se cargan las 15 candidaturas de prueba.
3. Prueba el resto de configuraciones en orden (`02 listar`, `03 añadir`…). Cada una ejecuta un comando.
4. Para probar otro comando, ve a **Edit Configurations…** y cambia los **Program arguments**.

La clase principal es `com.cursus.Main`. El programa no tiene menú: recibe el comando como argumento, lo ejecuta y termina.

Para empezar de cero, borra `data/candidaturas.txt` y vuelve a importar el CSV.

## Comandos

| HU | Qué hace | Comando |
|---|---|---|
| HU-01 | Importar candidaturas desde un CSV | `-importar data/candidaturas-prueba.csv` |
| HU-02 | Listar todas las candidaturas | `-listar` |
| HU-03 | Filtrar por estado y/o empresa | `-listar --estado pendiente --empresa Indra` |
| HU-04 | Añadir una candidatura | `-añadir --titulo "Dev Java" --empresa "Indra" --estado pendiente` |
| HU-05 | Ver el detalle de una candidatura | `-ver 13` |
| HU-06 | Eliminar una candidatura | `-borrar 13` |

Algunos detalles:

- Los estados válidos son `pendiente`, `entrevista`, `rechazado` y `aceptado`.
- En `-listar`, `--estado` y `--empresa` son opcionales. La empresa se busca sin distinguir mayúsculas y basta con escribir parte del nombre (`--empresa accen` encuentra Accenture).
- `-añadir` admite además `--fecha dd/MM/yyyy` (si no se indica, se usa la de hoy), `--enlace` y `--notas`.
- Si un texto lleva espacios, va entre comillas: `--titulo "Dev Java"`.
- Si la consola no muestra bien la ñ, `-anadir` funciona igual que `-añadir`.

Si falta un argumento, la fecha no existe, el ID no existe o no se encuentra el archivo, el programa muestra un mensaje de error y termina sin romperse. Por ejemplo:

```
Error: No existe ninguna candidatura con el ID 99.
```

## Datos

Las candidaturas se guardan en `data/candidaturas.txt`, una por línea y con los campos separados por punto y coma:

```
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
8;Analista Programador;Accenture;entrevista;09/09/2026;https://ofertas.example.com/8;Entrevista presencial en Madrid
```

El archivo se crea solo la primera vez que se guarda algo y no se sube al repositorio.

Datos de prueba: [`data/candidaturas-prueba.csv`](data/candidaturas-prueba.csv) (15 candidaturas).

## Estructura

```
src/main/java/com/cursus/
├── Main.java
├── controller/   CandidaturaController   lee los argumentos y muestra los resultados
├── service/      OfertaService           listar, añadir, ver, borrar e importar
├── dao/          OfertaDAO               lee y escribe el archivo de texto
└── model/        Oferta, Estado          los datos de una candidatura
 
src/test/java/com/cursus/service/
└── OfertaServiceTest                     pruebas unitarias con JUnit 5
```

Para lanzar las pruebas: clic derecho en `src/test/java` → **Run 'All Tests'**.

## Diseño

El prototipo de Figma (HU-07), el PDF de las pantallas y el manual de estilo (HU-08) se entregan en Teams.

## Quién ha hecho qué (v1.0)

### Francis Romero Flores
HU-01, HU-02, HU-03 y HU-07

- Proyecto Maven, `model`, `dao` y `Main`.
- Estructura de `service`.
- Comando `-importar` CSV.
- Javadoc de su parte.
- Prototipo en Figma.
- Manual de estilo

### Antonio Tienda Díaz
HU-04, HU-05, HU-06 y HU-08

- Comandos `-añadir`, `-ver` y `-borrar`.
- Pruebas JUnit y CSV de prueba.
- Configuraciones de ejecución.
- Estructura de `controller`
- Javadoc de su parte.
- README.
- Prototipo en Figma
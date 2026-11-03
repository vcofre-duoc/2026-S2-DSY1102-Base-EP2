# EcoPark — solución completa JavaFX + MVC + JSON

**DSY1102 · Desarrollo Orientado a Objetos · Java 21 · Maven + JavaFX + Gson**

Este proyecto es la **solución terminada** de la práctica EcoPark. Se construyó sobre el proyecto base, manteniendo sus modelos, cálculo de tarifas, DAO y distribución visual.

## Funcionalidades listas

- Seleccionar **Auto** o **Moto**.
- Registrar patente y horas de estadía.
- Aplicar convenio con **20 % de descuento solo a Moto**.
- Validar patente obligatoria y horas entre **1 y 24**.
- Rechazar patentes repetidas, sin distinguir mayúsculas/minúsculas.
- Calcular y mostrar el costo en un **TableView**.
- Eliminar una estadía seleccionada.
- Mantener registros mediante `datos/estadias.json` y cargarlos al reiniciar.
- Mostrar errores y confirmaciones sin cerrar la aplicación.

## Reglas del negocio

| Tipo | Costo |
|---|---|
| Auto | $2.000 por hora |
| Moto sin convenio | $1.000 por hora |
| Moto con convenio | Tarifa de moto con 20 % de descuento |

Ejemplos: Auto 2 horas = **$4.000**; Moto 3 horas = **$3.000**; Moto con convenio 3 horas = **$2.400**.

## Estructura

```text
EcoPark_Resuelto_IntelliJ/
├── pom.xml
├── README.md
├── SOLUCION_PASO_A_PASO.md
├── GUIA_DESARROLLO_BASE_PARA_ESTUDIANTES.md
├── ENUNCIADO_ECOPARK.pdf
├── datos/
│   └── estadias.json
└── src/main/
    ├── java/cl/duoc/ecopark/
    │   ├── Launcher.java
    │   ├── App.java
    │   ├── model/
    │   │   ├── Estadia.java
    │   │   ├── EstadiaAuto.java
    │   │   ├── EstadiaMoto.java
    │   │   └── ConDescuento.java
    │   ├── controller/MainController.java
    │   ├── repository/EstadiaRepository.java
    │   └── dao/
    │       ├── EstadiaDAO.java
    │       └── JsonEstadiaDAO.java
    └── resources/cl/duoc/ecopark/view/
        ├── main-view.fxml
        └── ecopark.css
```

## Arquitectura y flujo

```text
Usuario
  ↓
FXML (vista) --fx:id / onAction--> MainController
  ↓                                ↓
TableView ← ObservableList ← EstadiaRepository
                                   ↓
                            EstadiaDAO (JSON)
                                   ↓
                            datos/estadias.json
```

- **Model:** clase abstracta `Estadia`, subclases `EstadiaAuto` y `EstadiaMoto`, interfaz `ConDescuento`.
- **View:** `main-view.fxml` y `ecopark.css`.
- **Controller:** `MainController` valida entradas y coordina el flujo.
- **Repository:** administra, busca duplicados y delega persistencia.
- **DAO:** serializa/deserializa en el archivo JSON usando Gson.
- **ObservableList:** enlaza los datos del Repository con la tabla visual.

## Importar en IntelliJ IDEA

1. Descomprime el ZIP y abre **la carpeta que contiene `pom.xml`** mediante **File → Open**.
2. Selecciona **JDK 21** en Project SDK.
3. Recarga Maven para descargar `javafx-controls`, `javafx-fxml` y `gson` si todavía no están disponibles.
4. Ejecuta `cl.duoc.ecopark.Launcher` (no ejecutes `App` directamente).
5. Alternativamente, desde la terminal del proyecto ejecuta `mvn javafx:run`.

**Persistencia:** la ruta `datos/estadias.json` es relativa al directorio de trabajo. Usa la carpeta raíz del proyecto como *Working directory* en la configuración de ejecución.

## Pruebas manuales sugeridas

1. Registrar Auto, patente `ABCD12`, 2 horas: **$4.000**.
2. Registrar Moto, patente `MOTO11`, 3 horas sin convenio: **$3.000**.
3. Registrar Moto, patente `MOTO22`, 3 horas con convenio: **$2.400**.
4. Intentar registrar `abcd12` de nuevo: debe rechazar la patente duplicada.
5. Probar horas `abc`, `0`, `25` y patente vacía: debe mostrar error.
6. Cerrar y abrir: los registros deben seguir en la tabla.
7. Seleccionar una fila, eliminarla, reiniciar: el registro no debe reaparecer.

## Material docente

- `SOLUCION_PASO_A_PASO.md`: guía que describe qué se completó y por qué.
- `GUIA_DESARROLLO_BASE_PARA_ESTUDIANTES.md`: ejercicio original para desarrollar en clase.
- `ENUNCIADO_ECOPARK.pdf`: enunciado académico del caso.

# EcoPark — proyecto base de práctica JavaFX

**Asignatura:** DSY1102 · Desarrollo Orientado a Objetos  
**Tiempo objetivo:** 80 minutos (menos de 2 horas pedagógicas)  
**Nivel:** práctica guiada, menos extensa que FitPower  
**Java:** 21 · **Maven:** OpenJFX + Gson

## ¿Qué se quiere construir?

Una aplicación de escritorio para registrar estadías de **autos** y **motos** en el estacionamiento EcoPark. Debe registrar patente y horas, calcular costo, mostrar estadías en un `TableView`, eliminarlas y conservar los registros en `datos/estadias.json`.

| Tipo | Regla |
| --- | --- |
| Auto | $2.000 por hora |
| Moto | $1.000 por hora |
| Moto con convenio | Descuento del 20 % sobre la tarifa de moto |

Una patente no puede registrarse dos veces a la vez. Horas permitidas: de 1 a 24.

## Importante: esta es una **base incompleta, preparada intencionalmente**

El proyecto **abre y muestra la ventana** desde el principio. Los botones no hacen nada todavía porque el objetivo es que los estudiantes construyan la vinculación FXML-Controller y completen los métodos indicados con `TODO`.

La vista `main-view.fxml` viene con **toda su distribución visual**, pero **sin `fx:id`, sin `fx:controller` y sin `onAction`**. No hay que rediseñar la ventana ni empezar desde Scene Builder.

### Ya está resuelto (material proporcionado)

- `pom.xml`: dependencias JavaFX y Gson.
- `Launcher.java`, `App.java`: inicio de JavaFX, carga del FXML y CSS.
- `model/Estadia.java`: clase abstracta y validaciones.
- `model/EstadiaAuto.java`, `EstadiaMoto.java`: subclases y tarifas.
- `model/ConDescuento.java`: interfaz del beneficio para motos.
- `dao/EstadiaDAO.java`, `JsonEstadiaDAO.java`: lectura y escritura JSON listas para conectar.
- `view/main-view.fxml`: estructura visual terminada, aún sin vínculos.
- `view/ecopark.css`: estilo básico.
- `datos/estadias.json`: lista inicial vacía.

### El estudiante debe completar

- `view/main-view.fxml`: `fx:controller`, identificadores `fx:id` y eventos `onAction`.
- `controller/MainController.java`: variables `@FXML`, `initialize()`, `registrar()`, `eliminar()`.
- `repository/EstadiaRepository.java`: `cargar()`, `registrar()`, `eliminar()`.

## Estructura

```text
EcoPark_Base_IntelliJ/
├── pom.xml
├── README.md
├── GUIA_DESARROLLO.md
├── LEEME_INICIO.txt
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
    │   ├── dao/
    │   │   ├── EstadiaDAO.java
    │   │   └── JsonEstadiaDAO.java
    │   ├── repository/
    │   │   └── EstadiaRepository.java
    │   └── controller/
    │       └── MainController.java
    └── resources/cl/duoc/ecopark/view/
        ├── main-view.fxml
        └── ecopark.css
```

## Cómo importarlo en IntelliJ IDEA

1. Descomprimir el ZIP, manteniendo la estructura de carpetas.
2. **File → Open** → seleccionar la carpeta `EcoPark_Base_IntelliJ` (la que contiene `pom.xml`).
3. Confirmar **Trust Project** y permitir que Maven descargue JavaFX y Gson si es necesario.
4. Seleccionar **JDK 21** en Project Structure.
5. Ejecutar `cl.duoc.ecopark.Launcher` (no `App`). También puede usarse `mvn javafx:run`.
6. Se abrirá el formulario sin funcionamiento. Esto es correcto al iniciar la práctica.

> La ruta `datos/estadias.json` es relativa: ejecuta desde la raíz del proyecto para ver el archivo esperado. Si la ejecución configura otro *working directory*, el archivo puede crearse en otra carpeta.

## ¿Cómo seguir?

Leer **[GUIA_DESARROLLO.md](GUIA_DESARROLLO.md)**. Incluye fases, indicaciones y pistas de métodos (sin entregar la solución completa).

## Conceptos que se practican

**POO:** abstracción, herencia, encapsulamiento, interfaz, sobrescritura y polimorfismo.  
**JavaFX:** `FXMLLoader`, `Stage`, `Scene`, `@FXML`, `ComboBox`, `CheckBox`, `TextField`, `TableView`, `TableColumn`, eventos y `ObservableList`.  
**Persistencia:** `Repository`, `DAO`, Gson, JSON, serialización y deserialización.  
**Validación:** tipo obligatorio, patente no vacía, horas de 1 a 24, patente no duplicada y selección previa a eliminar.

**Alcance:** práctica guiada; no equivale a todos los requerimientos ni al tiempo de la evaluación formal FitPower.

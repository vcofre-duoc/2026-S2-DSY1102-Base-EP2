# Guía de desarrollo — EcoPark (80 minutos)

> **Guía de apoyo para estudiantes.** Los modelos y el DAO están completos. Debes conectar el formulario visual y realizar las operaciones principales.

## Distribución sugerida del tiempo

| Fase | Tiempo | Resultado |
|---|---:|---|
| 1. Reconocer proyecto y ejecutar | 10 min | La vista base abre en JavaFX. |
| 2. Conectar FXML y Controller | 12 min | Los controles se inyectan con `@FXML`. |
| 3. Completar Repository | 13 min | CRUD y JSON conectados. |
| 4. Completar Controller | 30 min | Registrar, validar, listar y eliminar. |
| 5. Probar y corregir | 15 min | Datos visibles y persistentes. |
| **Total** | **80 min** | **Menos de 2 horas pedagógicas** |

## 1. Importar, ejecutar y comprender la base

- Ejecuta `cl.duoc.ecopark.Launcher`.
- Observa la vista con formulario a la izquierda y tabla a la derecha.
- Revisa `Estadia`, `EstadiaAuto`, `EstadiaMoto` y `ConDescuento`: **ya están implementadas**.
- Revisa los dos métodos del DAO (`cargar`, `guardar`). No es necesario modificarlo.

El flujo que construirás es:

```text
FXML → MainController → EstadiaRepository → EstadiaDAO → estadias.json
                         ↓
                  ObservableList → TableView
```

## 2. Completar la vinculación en `main-view.fxml`

El FXML base trae la **estructura visual, sin identificadores ni eventos**. Agrega en el nodo raíz:

```xml
fx:controller="cl.duoc.ecopark.controller.MainController"
```

Luego agrega `fx:id` a los controles necesarios. Puedes utilizar esta correspondencia:

| Control en la vista | `fx:id` sugerido | Tipo Java |
|---|---|---|
| ComboBox Tipo | `tipoCombo` | `ComboBox<String>` |
| TextField Patente | `patenteField` | `TextField` |
| TextField Horas | `horasField` | `TextField` |
| CheckBox Convenio | `convenioCheck` | `CheckBox` |
| TableView | `estadiasTable` | `TableView<Estadia>` |
| Columna Tipo | `tipoCol` | `TableColumn<Estadia, String>` |
| Columna Patente | `patenteCol` | `TableColumn<Estadia, String>` |
| Columna Horas | `horasCol` | `TableColumn<Estadia, Number>` |
| Columna Costo | `costoCol` | `TableColumn<Estadia, String>` |
| Label inferior | `mensajeLabel` | `Label` |

En el botón **Registrar**, agrega `onAction="#registrar"`; en **Eliminar**, `onAction="#eliminar"`. Los métodos ya están declarados en el Controller, pero deben implementarse.

**Comprobación:** la vista debe seguir abriendo. Los botones todavía no harán nada hasta completar el código.

## 3. Completar `EstadiaRepository.java`

Los tres métodos contienen `TODO`:

- **`cargar()`**: invoca `dao.cargar()` y reemplaza el contenido de la lista interna.
- **`registrar(Estadia)`**: comprueba si ya existe una patente (ignorando mayúsculas/minúsculas), agrega a la lista y solicita `dao.guardar(estadias)`.
- **`eliminar(Estadia)`**: elimina de la colección y solicita al DAO guardar otra vez.

**Pistas:** `estadias.clear()`, `estadias.addAll(...)`, `estadias.add(...)`, `estadias.remove(...)`.

> Para una implementación más robusta, si `dao.guardar(...)` falla, conviene revertir el cambio realizado sobre la lista y mostrar el error. Puedes dejar esta mejora para después del ejercicio principal.

## 4. Completar `MainController.java`

### 4.1 Declarar los controles

Agrega los atributos `@FXML` del cuadro anterior con sus tipos Java. El nombre de cada atributo **debe coincidir** con el `fx:id` correspondiente del FXML.

### 4.2 Implementar `initialize()`

- Carga `"Auto"` y `"Moto"` en `tipoCombo`.
- Cuando el tipo sea `Moto`, habilita el checkbox de convenio; para `Auto`, deshabilítalo y desmárcalo.
- Configura las columnas con `setCellValueFactory(...)` para mostrar `getTipo()`, `getPatente()`, `getHoras()` y `calcularCosto()`.
- Usa wrappers sencillos: `ReadOnlyStringWrapper` y `ReadOnlyIntegerWrapper`.
- Conecta la colección gráfica usando `estadiasTable.setItems(filas)`.
- Llama a `repository.cargar()` y copia el resultado a la lista observable mediante `filas.setAll(repository.listar())`.
- En caso de error de lectura, muestra un mensaje rojo o de advertencia.

### 4.3 Implementar `registrar()`

1. Lee el tipo elegido, la patente y las horas.
2. Verifica el tipo obligatorio y que la patente no esté vacía.
3. Convierte las horas a `int` con `Integer.parseInt(...)` y controla `NumberFormatException`.
4. El modelo valida que las horas estén entre 1 y 24.
5. Crea `EstadiaAuto` o `EstadiaMoto` según el tipo. Para Moto pasa `convenioCheck.isSelected()` al constructor.
6. Invoca `repository.registrar(estadia)`.
7. Actualiza la tabla con `filas.setAll(repository.listar())`.
8. Limpia los campos y muestra confirmación.

**Pista de polimorfismo:** guarda la nueva instancia en una variable declarada `Estadia`, aunque el objeto real sea `EstadiaAuto` o `EstadiaMoto`.

### 4.4 Implementar `eliminar()`

- Obtén el elemento seleccionado mediante `estadiasTable.getSelectionModel().getSelectedItem()`.
- Si no hay selección, muestra un mensaje claro.
- Si existe, invoca `repository.eliminar(seleccionada)`.
- Actualiza la `ObservableList` y confirma la eliminación.

## 5. Pruebas de cierre

| Prueba | Resultado esperado |
|---|---|
| Auto, patente `ABCD12`, 2 horas | $4.000 |
| Moto, `MOTO11`, 3 horas, sin convenio | $3.000 |
| Moto, `MOTO22`, 3 horas, con convenio | $2.400 |
| Sin patente | Mensaje de campo obligatorio |
| Horas `abc`, `0` o `25` | Rechazo, aplicación sigue abierta |
| Repetir la misma patente | Debe impedir duplicidad |
| Registrar y cerrar | Registros aparecen al abrir otra vez |
| Eliminar sin seleccionar | Mensaje, sin interrupción |
| Eliminar y reiniciar | El registro eliminado no reaparece |

## Errores frecuentes

- **`Location is not set`**: revisa la ruta `main-view.fxml` en `App`.
- **`@FXML ... null`**: revisa coincidencia exacta entre `fx:id` y atributo Java.
- **`Error resolving onAction`**: confirma que el método exista con `@FXML`.
- **JavaFX runtime components are missing**: ejecuta `Launcher`, no `App`, o utiliza `mvn javafx:run`.
- **JSON no aparece donde esperabas**: revisa el directorio de trabajo de la configuración de ejecución.
- **Tabla vacía**: verifica `setItems`, las columnas y `filas.setAll(...)`.

## ¿Qué no se pide en esta práctica?

No se solicita crear otra ventana, editar registros desde la tabla, usar base de datos, agregar búsquedas/filtros ni construir toda la vista desde cero. El propósito es practicar las conexiones esenciales con una actividad más corta que FitPower.

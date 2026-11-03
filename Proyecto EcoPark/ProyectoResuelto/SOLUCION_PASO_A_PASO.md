# EcoPark — solución paso a paso para explicar en clase

La práctica base ya traía el **modelo**, el **DAO JSON** y el **diseño de la ventana**, pero el FXML no estaba vinculado al Controller y los métodos de Repository/Controller estaban pendientes.

## 1. Entender las responsabilidades

```text
FXML: lo que ve el usuario.
Controller: interpreta clics, lee datos y muestra resultados.
Model: EstadiaAuto / EstadiaMoto y las reglas de costo.
Repository: administra la lista y controla patentes duplicadas.
DAO: abre/guarda datos/estadias.json mediante Gson.
```

## 2. Vincular `main-view.fxml` con `MainController`

En el nodo raíz se agregó:

```xml
fx:controller="cl.duoc.ecopark.controller.MainController"
```

Se agregaron los siguientes `fx:id`, manteniendo intacta la estructura visual de la base:

| Control | fx:id |
|---|---|
| Tipo (ComboBox) | `tipoCombo` |
| Patente (TextField) | `patenteField` |
| Horas (TextField) | `horasField` |
| Convenio (CheckBox) | `convenioCheck` |
| Tabla (TableView) | `estadiasTable` |
| Columna tipo | `tipoCol` |
| Columna patente | `patenteCol` |
| Columna horas | `horasCol` |
| Columna costo | `costoCol` |
| Mensaje inferior | `mensajeLabel` |

También se conectaron los botones:

```xml
<Button text="Registrar" onAction="#registrar" />
<Button text="Eliminar" onAction="#eliminar" />
```

## 3. Declarar los controles en el Controller

Cada `fx:id` anterior tiene un atributo Java con `@FXML` y el mismo nombre. Ejemplo:

```java
@FXML private TextField patenteField;
@FXML private TableView<Estadia> estadiasTable;
```

Cuando `FXMLLoader` carga la vista, conecta automáticamente los controles a los atributos.

## 4. Implementar `initialize()`

Se inicializan los controles:

- Agregar `Auto` y `Moto` al `ComboBox`.
- Activar `convenioCheck` únicamente para motos.
- Asignar `ReadOnlyStringWrapper` y `ReadOnlyIntegerWrapper` a las columnas.
- Conectar `estadiasTable.setItems(filas)`.
- Llamar a `repository.cargar()` para leer el JSON y `filas.setAll(repository.listar())` para mostrar los registros.

**Ejemplo de polimorfismo:**

```java
costoCol.setCellValueFactory(dato ->
    new ReadOnlyStringWrapper(formatearCosto(dato.getValue().calcularCosto())));
```

Se llama a `calcularCosto()` sin comprobar si la fila corresponde a Auto o Moto. El subtipo real ejecuta su propia fórmula.

## 5. Implementar `EstadiaRepository.cargar()`

```java
List<Estadia> recuperadas = dao.cargar();
estadias.clear();
estadias.addAll(recuperadas);
```

La capa Repository no conoce el formato JSON: solicita los datos al DAO.

## 6. Implementar `EstadiaRepository.registrar()`

1. Recorrer las estadías existentes y comparar sus patentes con `equalsIgnoreCase()`.
2. Rechazar patentes duplicadas con `IllegalArgumentException`.
3. Agregar a la lista.
4. Llamar a `dao.guardar(estadias)`.
5. Si el archivo falla, quitar el objeto agregado para no desincronizar los datos.

## 7. Implementar `MainController.registrar()`

```text
Usuario completa formulario
  ↓
Controller lee tipo, patente y horas
  ↓
Valida campos y convierte horas a int
  ↓
Crea EstadiaAuto o EstadiaMoto
  ↓
Repository.registrar()
  ↓
DAO guarda JSON
  ↓
ObservableList actualiza TableView
  ↓
Muestra confirmación
```

Reglas de negocio ya implementadas en los modelos:

```java
// Auto
return getHoras() * 2000;

// Moto: precio base menos descuento
return getHoras() * 1000 - calcularDescuento();
```

## 8. Implementar `MainController.eliminar()`

1. Leer selección de `estadiasTable.getSelectionModel().getSelectedItem()`.
2. Informar error si no hay selección.
3. Invocar `repository.eliminar(seleccionada)`.
4. Actualizar la tabla y mostrar confirmación.
5. El DAO guardará la lista sin ese registro.

## 9. Manejar excepciones

- `NumberFormatException`: el usuario escribió horas no numéricas.
- `IllegalArgumentException`: patente vacía, rango inválido, patente repetida.
- `IOException`: fallo al cargar o guardar JSON.

Los errores se muestran en la etiqueta inferior sin cerrar la ventana.

## 10. CSS para mensajes

El archivo `ecopark.css` añade clases:

```css
.mensaje-error { -fx-text-fill: #BB2C2C; }
.mensaje-exito { -fx-text-fill: #237A46; }
```

## 11. Comprobar persistencia

1. Registrar Auto `ABCD12`, 2 h → `$4.000`.
2. Revisar `datos/estadias.json`: el objeto aparece en el archivo.
3. Cerrar la aplicación, volver a abrir: la estadía se recupera.
4. Eliminar la fila y reiniciar: la estadía no vuelve.

## 12. Resumen de la conexión

```text
main-view.fxml
   ↓ @FXML y eventos
MainController
   ↓ registrar / eliminar
EstadiaRepository
   ↓ cargar / guardar
JsonEstadiaDAO
   ↓ Gson
estadias.json

EstadiaRepository → ObservableList<Estadia> → TableView<Estadia>
```

Se mantuvo el proyecto sencillo: una ventana, dos tipos de estadía y dos acciones CRUD principales.

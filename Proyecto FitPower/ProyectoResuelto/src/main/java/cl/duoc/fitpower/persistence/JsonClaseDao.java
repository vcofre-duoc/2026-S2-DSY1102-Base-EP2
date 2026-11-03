package cl.duoc.fitpower.persistence;

import cl.duoc.fitpower.model.*;
import com.google.gson.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Lee ambos subtipos y sustituye atómicamente el archivo del subtipo modificado. */
public final class JsonClaseDao implements ClaseDao {
    private final Path directorio;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonClaseDao(Path directorio) { this.directorio = directorio.toAbsolutePath(); }

    private Path archivo(Class<? extends Clase> tipo) {
        if (tipo == ClasePersonal.class) return directorio.resolve("clases_personales.json");
        if (tipo == ClaseSemipersonal.class) return directorio.resolve("clases_semipersonales.json");
        throw new IllegalArgumentException("Tipo de clase no soportado.");
    }

    @Override public List<Clase> leer() throws IOException {
        List<Clase> resultado = new ArrayList<>();
        // La carga es completa: si un archivo está dañado, no se publica una lista parcial.
        resultado.addAll(leerTipo(ClasePersonal.class));
        resultado.addAll(leerTipo(ClaseSemipersonal.class));
        return resultado;
    }

    private <T extends Clase> List<Clase> leerTipo(Class<T> tipo) throws IOException {
        Path ruta = archivo(tipo);
        if (Files.notExists(ruta)) return new ArrayList<>();
        try {
            JsonElement raiz = JsonParser.parseString(Files.readString(ruta, StandardCharsets.UTF_8));
            if (!raiz.isJsonArray()) throw new IllegalArgumentException("Se esperaba un arreglo JSON.");
            List<Clase> lista = new ArrayList<>();
            for (JsonElement elemento : raiz.getAsJsonArray()) {
                if (!elemento.isJsonObject()) throw new IllegalArgumentException("Registro inválido.");
                JsonObject o = elemento.getAsJsonObject();
                String nombre = texto(o, "nombre");
                int cupo = entero(o, "cupoMaximo"), duracion = entero(o, "duracionMinutos");
                // Reconstruir con constructores evita que Gson omita validaciones del modelo.
                if (tipo == ClasePersonal.class) {
                    ClasePersonal c = new ClasePersonal(nombre, cupo, duracion,
                        texto(o, "instructor"), booleano(o, "evaluacionPrevia"));
                    if (booleano(o, "cancelacionActiva")) c.activarCancelacion();
                    lista.add(c);
                } else {
                    int participantes = entero(o, "participantes");
                    if (participantes <= 0) throw new IllegalArgumentException("Participantes debe ser mayor que cero.");
                    lista.add(new ClaseSemipersonal(nombre, cupo, duracion, participantes));
                }
            }
            return lista;
        } catch (RuntimeException | IOException ex) {
            throw new IOException("No se pudo leer " + ruta.getFileName() + ": " + ex.getMessage(), ex);
        }
    }

    private static JsonPrimitive campo(JsonObject o, String nombre) {
        JsonElement e = o.get(nombre);
        if (e == null || !e.isJsonPrimitive()) throw new IllegalArgumentException("Campo inválido: " + nombre);
        return e.getAsJsonPrimitive();
    }
    private static String texto(JsonObject o, String nombre) {
        JsonPrimitive e = campo(o, nombre);
        if (!e.isString() || e.getAsString().isBlank()) throw new IllegalArgumentException("Texto requerido: " + nombre);
        return e.getAsString().trim();
    }
    private static int entero(JsonObject o, String nombre) {
        JsonPrimitive e = campo(o, nombre);
        if (!e.isNumber()) throw new IllegalArgumentException("Número requerido: " + nombre);
        try { return e.getAsBigDecimal().intValueExact(); }
        catch (ArithmeticException ex) { throw new IllegalArgumentException("Entero fuera de rango: " + nombre); }
    }
    private static boolean booleano(JsonObject o, String nombre) {
        JsonPrimitive e = campo(o, nombre);
        if (!e.isBoolean()) throw new IllegalArgumentException("Booleano requerido: " + nombre);
        return e.getAsBoolean();
    }

    @Override public void guardarTipo(List<Clase> clases, Class<? extends Clase> tipo) throws IOException {
        Path destino = archivo(tipo);
        Path temporal = null;
        try {
            List<Clase> filtradas = clases.stream().filter(tipo::isInstance).toList();
            temporal = Files.createTempFile(directorio, "fitpower-", ".tmp");
            Files.writeString(temporal, gson.toJson(filtradas), StandardCharsets.UTF_8);
            // Si el sistema no soporta ATOMIC_MOVE, se rechaza el cambio para conservar el original.
            Files.move(temporal, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException ex) {
            throw new IOException("No se pudo guardar " + destino.getFileName() + ": " + ex.getMessage(), ex);
        } finally {
            if (temporal != null) {
                try { Files.deleteIfExists(temporal); } catch (IOException ignored) {
                    // Un temporal residual no altera el archivo válido ni el resultado de la operación.
                }
            }
        }
    }
}

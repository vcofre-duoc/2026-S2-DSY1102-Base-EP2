package cl.duoc.fitpower;

import cl.duoc.fitpower.model.*;
import cl.duoc.fitpower.persistence.*;
import cl.duoc.fitpower.repository.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Pruebas ejecutables sin JUnit; todos los archivos se crean en una carpeta temporal. */
public class Verificacion {
    private static int comprobaciones;
    private static void verificar(boolean condicion, String caso) {
        if (!condicion) throw new AssertionError(caso);
        comprobaciones++;
    }
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("fitpower-test-");
        try {
            ClaseRepository r = new ClaseRepository(new JsonClaseDao(dir));
            r.cargar(); verificar(r.listar().isEmpty(), "Primera ejecución sin archivos");
            ClasePersonal yoga = new ClasePersonal("Yoga", 1, 60, "Camila Rojas", false);
            yoga.activarCancelacion();
            ClasePersonal pilates = new ClasePersonal("Pilates", 1, 50, "Diego Soto", true);
            ClaseSemipersonal grupo = new ClaseSemipersonal("Yoga", 6, 60, 5);
            ClaseSemipersonal spinning = new ClaseSemipersonal("Spinning", 3, 45, 2);
            verificar(yoga.calcularCosto() == 42000, "Personal sin evaluación");
            verificar(pilates.calcularCosto() == 35000, "Personal con evaluación");
            verificar(grupo.calcularCosto() == 19800, "Semipersonal con recargo");
            verificar(spinning.calcularCosto() == 18000, "Semipersonal sin recargo");
            verificar(new ClaseSemipersonal("Límite", 3, 1, 3).calcularCosto() == 18000, "Límite tres participantes");
            for (Clase c : List.of(yoga, pilates, grupo, spinning)) r.registrar(c);
            verificar(r.listar().size() == 4, "Cuatro registros en memoria");
            r = new ClaseRepository(new JsonClaseDao(dir)); r.cargar();
            verificar(r.listar().size() == 4, "Reinicio conserva ambos subtipos");
            ClasePersonal recargada = (ClasePersonal) r.listar().get(0);
            verificar(recargada.tieneCancelacionActiva(), "Cancelación persistida");
            verificar(recargada.getInstructor().equals("Camila Rojas"), "Instructor persistido");
            verificar(!recargada.isEvaluacionPrevia(), "Evaluación persistida");
            r.eliminar(r.listar().stream().filter(c -> c.getNombre().equals("Pilates")).findFirst().orElseThrow());
            ClaseRepository reinicio = new ClaseRepository(new JsonClaseDao(dir)); reinicio.cargar();
            verificar(reinicio.listar().size() == 3, "Eliminación persistida");
            verificar(reinicio.listar().stream().noneMatch(c -> c.getNombre().equals("Pilates")), "Pilates ausente");
            r.registrar(new ClasePersonal("Yoga", 1, 60, "Otra persona", true));
            r.eliminar(recargada);
            verificar(r.listar().stream().filter(c -> c instanceof ClasePersonal && c.getNombre().equals("Yoga")).count() == 1,
                "Eliminar por objeto conserva otra clase del mismo nombre");
            byte[] valido = Files.readAllBytes(dir.resolve("clases_personales.json"));
            Files.writeString(dir.resolve("clases_personales.json"), "{roto");
            ClaseRepository roto = new ClaseRepository(new JsonClaseDao(dir));
            try { roto.cargar(); throw new AssertionError("JSON inválido aceptado"); }
            catch (IOException expected) { verificar(true, "JSON inválido informado"); }
            try { roto.registrar(yoga); throw new AssertionError("Sobrescritura tras carga fallida"); }
            catch (IOException expected) { verificar(true, "Carga fallida bloquea cambios"); }
            Files.write(dir.resolve("clases_personales.json"), valido);
            // DAO inyectable: simula un fallo de escritura sin depender de permisos del sistema.
            ClaseDao falla = new ClaseDao() {
                public List<Clase> leer() { return List.of(yoga); }
                public void guardarTipo(List<Clase> datos, Class<? extends Clase> tipo) throws IOException {
                    throw new IOException("Disco simulado no disponible");
                }
            };
            ClaseRepository seguro = new ClaseRepository(falla); seguro.cargar();
            try { seguro.registrar(pilates); throw new AssertionError("Registro debía fallar"); }
            catch (IOException expected) { verificar(seguro.listar().equals(List.of(yoga)), "Registro fallido conserva memoria"); }
            try { seguro.eliminar(yoga); throw new AssertionError("Eliminación debía fallar"); }
            catch (IOException expected) { verificar(seguro.listar().equals(List.of(yoga)), "Eliminación fallida conserva memoria"); }
            Files.writeString(dir.resolve("clases_semipersonales.json"), "[{\"nombre\":\"X\",\"cupoMaximo\":1,\"duracionMinutos\":1,\"participantes\":0}]");
            try { new JsonClaseDao(dir).leer(); throw new AssertionError("Dominio inválido aceptado"); }
            catch (IOException expected) { verificar(true, "Datos persistidos inválidos rechazados"); }
            System.out.println("OK: " + comprobaciones + " comprobaciones.");
        } finally {
            try (var paths = Files.walk(dir)) {
                for (Path f : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(f);
            }
        }
    }
}

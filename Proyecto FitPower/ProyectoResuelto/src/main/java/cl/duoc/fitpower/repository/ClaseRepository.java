package cl.duoc.fitpower.repository;

import cl.duoc.fitpower.model.Clase;
import cl.duoc.fitpower.persistence.ClaseDao;
import java.io.IOException;
import java.util.*;

/** Mantiene una colección de dominio. Solo confirma cambios después de persistirlos. */
public final class ClaseRepository {
    private final ClaseDao dao;
    private final List<Clase> clases = new ArrayList<>();
    private boolean cargado;
    public ClaseRepository(ClaseDao dao) { this.dao = Objects.requireNonNull(dao); }

    public void cargar() throws IOException {
        List<Clase> leidas = dao.leer();
        clases.clear();
        clases.addAll(leidas);
        cargado = true;
    }
    public List<Clase> listar() { return List.copyOf(clases); }

    public void registrar(Clase clase) throws IOException {
        verificarCarga();
        List<Clase> propuesta = new ArrayList<>(clases);
        propuesta.add(Objects.requireNonNull(clase));
        dao.guardarTipo(propuesta, clase.getClass());
        clases.add(clase);
    }
    public void eliminar(Clase clase) throws IOException {
        verificarCarga();
        List<Clase> propuesta = new ArrayList<>(clases);
        if (!propuesta.remove(clase)) throw new IllegalArgumentException("La clase seleccionada ya no existe.");
        dao.guardarTipo(propuesta, clase.getClass());
        clases.remove(clase);
    }
    private void verificarCarga() throws IOException {
        if (!cargado) throw new IOException("La carga inicial falló. Corrija los archivos y reinicie antes de modificar datos.");
    }
}

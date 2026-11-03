package cl.duoc.ecopark.repository;

import cl.duoc.ecopark.dao.EstadiaDAO;
import cl.duoc.ecopark.model.Estadia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORY - A COMPLETAR POR EL ESTUDIANTE.
 * Administra la colección en memoria y solicita al DAO leer/guardar JSON.
 */
public class EstadiaRepository {

    private final EstadiaDAO dao;
    private final List<Estadia> estadias = new ArrayList<>();

    public EstadiaRepository(EstadiaDAO dao) {
        this.dao = dao;
    }

    /** TODO 1: recuperar datos desde dao.cargar() y agregarlos a la lista. */
    public void cargar() throws IOException {
        // TODO: cargar desde el DAO.
    }

    /** Retorna una copia de los registros sin exponer la lista interna. */
    public List<Estadia> listar() {
        return List.copyOf(estadias);
    }

    /** TODO 2: agregar, persistir con DAO y evitar registros duplicados. */
    public void registrar(Estadia estadia) throws IOException {
        // TODO: validar que no exista la misma patente en la lista.
        // TODO: agregar y solicitar dao.guardar(estadias).
    }

    /** TODO 3: eliminar seleccionado y persistir nuevamente. */
    public void eliminar(Estadia estadia) throws IOException {
        // TODO: eliminar de la lista y solicitar dao.guardar(estadias).
    }
}

package cl.duoc.ecopark.repository;

import cl.duoc.ecopark.dao.EstadiaDAO;
import cl.duoc.ecopark.model.Estadia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORY: mantiene la lista de estadías y coordina con el DAO.
 * La clase no sabe escribir JSON; esa es la tarea del DAO.
 */
public class EstadiaRepository {

    // DAO recibe la responsabilidad de leer y guardar en el archivo.
    private final EstadiaDAO dao;

    // Colección interna para guardar las estadías mientras usamos la aplicación.
    private final List<Estadia> estadias = new ArrayList<>();

    public EstadiaRepository(EstadiaDAO dao) {
        this.dao = dao;
    }

    /**
     * CARGAR: al abrir la aplicación recuperamos los registros del JSON.
     * Si hay un error, conservamos la lista anterior sin modificarla.
     */
    public void cargar() throws IOException {
        List<Estadia> recuperadas = dao.cargar();
        // Solo reemplazamos los datos después de leer correctamente el archivo.
        estadias.clear();
        estadias.addAll(recuperadas);
    }

    /** LISTAR: devolvemos una copia para proteger nuestra lista interna. */
    public List<Estadia> listar() {
        return List.copyOf(estadias);
    }

    /**
     * REGISTRAR: no permite dos estadías con la misma patente.
     * Después de agregar, solicita al DAO actualizar el JSON.
     */
    public void registrar(Estadia estadia) throws IOException {
        if (estadia == null) {
            throw new IllegalArgumentException("Debe indicar una estadía válida.");
        }

        for (Estadia existente : estadias) {
            if (existente.getPatente().equalsIgnoreCase(estadia.getPatente())) {
                throw new IllegalArgumentException(
                        "La patente " + estadia.getPatente() + " ya está registrada.");
            }
        }

        estadias.add(estadia);
        try {
            dao.guardar(estadias);
        } catch (IOException e) {
            // Si falla el archivo, revertimos el cambio en memoria.
            estadias.remove(estadia);
            throw e;
        }
    }

    /**
     * ELIMINAR: quita la estadía elegida y actualiza el archivo JSON.
     * Si falla el guardado, recupera el registro en su posición original.
     */
    public void eliminar(Estadia estadia) throws IOException {
        if (estadia == null) {
            throw new IllegalArgumentException("Seleccione una estadía para eliminar.");
        }

        int posicion = estadias.indexOf(estadia);
        if (posicion < 0) {
            throw new IllegalArgumentException("La estadía ya no existe en la lista.");
        }

        estadias.remove(posicion);
        try {
            dao.guardar(estadias);
        } catch (IOException e) {
            estadias.add(posicion, estadia);
            throw e;
        }
    }
}

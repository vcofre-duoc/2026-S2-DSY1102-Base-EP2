package cl.duoc.ecopark.dao;

import cl.duoc.ecopark.model.Estadia;
import java.io.IOException;
import java.util.List;

/**
 * DAO: contrato de acceso físico a los datos.
 * En este ejercicio se utilizará un archivo JSON.
 */
public interface EstadiaDAO {

    List<Estadia> cargar() throws IOException;

    void guardar(List<Estadia> estadias) throws IOException;
}

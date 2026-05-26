package es.upm.etsisi.iwsim21.grupo5.poo.controlador;

import es.upm.etsisi.iwsim21.grupo5.poo.modelo.Actividad;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.Cine;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.Teatro;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.actividades.ActividadesException;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.actividades.ActividadesExistenteException;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.actividades.ActividadesTipoException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Esta clase es la controladora de actividades, donde se almacenan todas las actividades en una lista, y se realizan diferentes métodos de búsqueda, agregación o eliminación de actividades.
 */
public class CActividad {
    private final List<Actividad> listActividades;

    public CActividad() {
        listActividades = new LinkedList<>();
    }

    public List<Actividad> getListActividades() {
        return listActividades;
    }

    /**
     * Este método crea una actividad nueva teniendo en cuenta el aforo de la misma siempre y cuando no exista ya dicha actividad. También se comprobará que el tipo de actividad sea válido.
     *
     * @param tipoActividad
     * @param nombreActividad
     * @param descripcion
     * @param duracion
     * @param coste
     * @param aforo
     * @return String
     */
    public String crearActividadAforoLimitado(String tipoActividad, String nombreActividad, String descripcion, int duracion, double coste, int aforo) throws ActividadesException {
        if (encontrarActividadPorNombre(nombreActividad) != null)
            throw new ActividadesExistenteException("Error: Actividad repetida");
        Actividad nueva = crearActividadSegunTipo(tipoActividad, nombreActividad, descripcion, duracion, coste, aforo);
        listActividades.add(nueva);
        return "Actividad creada: " + nueva;
    }

    /**
     * Este método crea una nueva actividad sin tener en cuenta el aforo(ya que es ilimitado). Al igual que en el método con aforo, se comprobará que la actividad no esté repetida y que el tipo de actividad sea válido.
     *
     * @param tipoActividad
     * @param nombreActividad
     * @param descripcion
     * @param duracion
     * @param coste
     * @return String
     */
    public String crearActividadSinAforo(String tipoActividad, String nombreActividad, String descripcion, int duracion, double coste) throws ActividadesException {
        return crearActividadAforoLimitado(tipoActividad, nombreActividad, descripcion, duracion, coste, -1);
    }

    /**
     * Método auxiliar que comprueba si el tipo de actividad es válido. Si lo es, devuelve una actividad.
     *
     * @param tipoActividad
     * @param nombreActividad
     * @param descripcion
     * @param duracion
     * @param coste
     * @param aforo
     * @return Actividad
     */
    private Actividad crearActividadSegunTipo(String tipoActividad, String nombreActividad, String descripcion, int duracion, double coste, int aforo) throws ActividadesTipoException {
        return switch (tipoActividad) {
            case "Cinema" -> new Cine(listActividades.size() + 1, nombreActividad, descripcion, duracion, coste, aforo);
            case "Generic" ->
                    new Actividad(listActividades.size() + 1, nombreActividad, descripcion, duracion, coste, aforo);
            case "Theatre" ->
                    new Teatro(listActividades.size() + 1, nombreActividad, descripcion, duracion, coste, aforo);
            default -> throw new ActividadesTipoException("Error: tipo de actividad incorrecto");
        };
    }


    /**
     * Método auxiliar que recibe un nombre por parámetro y comprueba si en la lista de actividades existe una actividad con ese nombre. Si se cumple la condición, se devuelve una actividad.
     *
     * @param nombreActividad
     * @return Actividad
     */
    private Actividad encontrarActividadPorNombre(String nombreActividad) {
        for (Actividad a : listActividades) {
            if (a.getNombre().equals(nombreActividad)) return a;
        }
        return null;
    }

    /**
     * Método auxiliar que recibe un id de actividad por parámetro y comprueba si en la lista de actividades existe una actividad con el mismo id. Si se cumple la condición, devuelve una actividad.
     *
     * @param idActividad
     * @return Actividad
     */
    public Actividad getActividad(int idActividad) {
        for (Actividad actividad : listActividades) {
            if (actividad.getId() == idActividad) return actividad;
        }
        return null;
    }

    /**
     * Este metodo es la ultima consulta, donde se busca que actividades tienen un aforo mayor y un coste inferiro a los parametros que introduce el usuario.
     *
     * @param coste
     * @param aforo
     * @return String
     */
    public String actividadAforoMayorCosteInferior(double coste, int aforo) {
        List<Actividad> listaNueva = new ArrayList<>();

        for (Actividad actividad : listActividades) {
            if ((actividad.getAforo() > aforo || actividad.getAforo() == -1) && actividad.getCoste() < coste) {
                listaNueva.add(actividad);
            }
        }
        if (!listaNueva.isEmpty())
            return "Actividades con un aforo mayor a " + aforo + " y coste menor a " + coste + ":\n " + listaNueva;
        else return "No hay Actividades que cumplan los parametros";
    }
}

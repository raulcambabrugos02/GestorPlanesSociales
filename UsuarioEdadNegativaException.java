package es.upm.etsisi.iwsim21.grupo5.poo.modelo;

import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.planes.PlanesAtributosException;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Una clase principal del proyecto, donde se contienen todos los Atributos y Metodos especificos de un Plan.
 */
public class Plan {
    private int idPlan;
    private String nombrePlan;
    private LocalDateTime fechaHora;
    private String lugar;
    private int capacidadMax;
    private Usuario propietario;
    private List<Actividad> actividades;
    private List<Usuario> participantes;
    private int calificacion;
    private List<Integer> calificaciones;

    /**
     *
     * @param id
     * @param nombre
     * @param fechaHora
     * @param lugar
     * @param capacidadMax
     * @param propietario
     */
    public Plan(int id, String nombre, LocalDateTime fechaHora, String lugar, int capacidadMax, Usuario propietario) {
        if (LocalDateTime.now().isAfter(fechaHora)) throw new PlanesAtributosException("Error: Ya ha pasado la fecha");
        if (capacidadMax <= 0 && capacidadMax != -1) throw new PlanesAtributosException("Error: Capacidad incorrecta");
        this.idPlan = id;
        this.nombrePlan = nombre;
        this.fechaHora = fechaHora;
        this.lugar = lugar;
        this.capacidadMax = capacidadMax;
        this.propietario = propietario;
        this.actividades = new LinkedList<>();
        this.participantes = new LinkedList<>();
        this.calificaciones = new LinkedList<>();
    }

    public Plan(int id, String nombre, LocalDateTime fechaHora, String lugar, Usuario propietario) {
        this(id, nombre, fechaHora, lugar, -1, propietario); //-1 para indicar aforo sin límite
    }

    public String getNombre() {
        return nombrePlan;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getLugar() {
        return lugar;
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    /**
     * Este metodo agrega la calificacion en un plan concreto, la calificacion sera la media de las calificaciones obtenidas.
     *
     * @param calificacion
     */
    public void agregarCalificacion(int calificacion) {
        calificaciones.add(calificacion);
        int suma = 0;
        for (int c : calificaciones) suma += c;
        this.calificacion = suma / calificaciones.size();
    }

    /**
     * En este metodo se intenta agragar una actividad al plan, se mira si hay capacidad maxima o no y en el caso que se pueda se agrega la actividad y devuelves true.
     *
     * @param actividad
     * @return boolean
     */
    public boolean agregarActividad(Actividad actividad) {
        if (capacidadMax == -1 || capacidadMax > actividad.getAforo() || participantes.size() > actividad.getAforo())
            capacidadMax = capacidadMax == -1 ? actividad.getAforo() : Math.min(capacidadMax, actividad.getAforo());
        else return false;
        actividades.add(actividad);
        return true;
    }


    /**
     * En este metodo, se añade un participante a la lista de participantes del plan.
     *
     * @param participante
     */
    public void agregarParticipante(Usuario participante) {
        participantes.add(participante);
    }

    @Override
    public String toString() {
        StringBuilder actividadesSB = new StringBuilder(" ");
        StringBuilder participantesSB = new StringBuilder(" ");
        actividades.forEach(a -> actividadesSB.append(a.getNombre()).append(";").append(a.getDescripcion()).append("\n"));
        participantes.forEach(p -> participantesSB.append(p.getNombreUsuario()).append("; "));
        return String.format("id:%d; propietario:%s; nombre:%s; fecha:%s; lugar:%s; plazas:%d;participantes:%s;%nactividades:%s\n",
                idPlan, propietario.getNombreUsuario(), nombrePlan, fechaHora, lugar, capacidadMax - participantes.size(), participantesSB, actividadesSB);
    }
}

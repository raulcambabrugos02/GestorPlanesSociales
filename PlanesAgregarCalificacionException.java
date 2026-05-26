package es.upm.etsisi.iwsim21.grupo5.poo.controlador;

import es.upm.etsisi.iwsim21.grupo5.poo.modelo.Actividad;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.Plan;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.Usuario;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.planes.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Este es el controlador de Plan, donde se contienen todos los métodos que principalmente afectan a los planes de los usuarios
 */
public class CPlan {
    private final List<Plan> listaPlan;

    public CPlan() {
        listaPlan = new LinkedList<>();
    }

    public List<Plan> getListaPlan() {
        return listaPlan;
    }

    /**
     * Este método crea un Plan en el cual hay una capacidad limitada, el usuario que crea el plan se establece como propietario. También se comprueba que el plan no exista y que la hora a la que se pretende realizar el plan sea posterior a la hora actual.
     *
     * @param nombrePlan
     * @param fechaHora
     * @param lugar
     * @param capacidadMax
     * @param propietario
     * @return String
     */
    public String crearPlanConCapacidad(String nombrePlan, LocalDateTime fechaHora, String lugar, int capacidadMax, Usuario propietario) throws PlanesException {
        if (buscarPlan(nombrePlan) != null)
            throw new PlanesExistenteException("Error: ya existe un plan con este nombre");
        Plan plan = new Plan(listaPlan.size() + 1, nombrePlan, fechaHora, lugar, capacidadMax, propietario);
        listaPlan.add(plan);
        return "Plan creado: " + plan;
    }

    /**
     * Aquí al igual que en el método anterior de "crearPlanConCapacidad", un usuario creara un plan, pero en este caso NO tendrá una capacidad máxima.
     *
     * @param nombrePlan
     * @param fechaHora
     * @param lugar
     * @param propietario
     * @return String
     */
    public String crearPlanIlimitado(String nombrePlan, LocalDateTime fechaHora, String lugar, Usuario propietario) throws PlanesException {
        return crearPlanConCapacidad(nombrePlan, fechaHora, lugar, -1, propietario);
    }

    /**
     * En este método el usuario que se pasa por parametro agregará una actividad al plan, se buscará el plan con su id y solo lo podrá agregar si ese usuario es el propietario del plan.
     * Además, se comprobará que el plan al que se pretende agregar la actividad exista y que esa actividad no este ya en el plan, por último se comprueba que la capacidad maxima del plan es menor que el aforo de la actividad y que la fecha de este plan no haya expirado.
     *
     * @param actividad
     * @param idPlan
     * @param usuario
     * @return String
     */
    public String agregarActividad(Actividad actividad, int idPlan, Usuario usuario) throws PlanesException {
        if (idPlan > listaPlan.size()) throw new PlanesAgregarActividadException("Error: No existe un plan con ese id");
        Plan plan = listaPlan.get(idPlan - 1);
        if (LocalDateTime.now().isAfter(plan.getFechaHora()))
            throw new PlanesAgregarActividadException("Error: Ya ha pasado la fecha de este plan");
        if (!esPropietario(usuario, plan))
            throw new PlanesAgregarActividadException("Error: El usuario no es propietario de este plan");
        if (plan.getActividades().contains(actividad))
            throw new PlanesAgregarActividadException("Error: El plan ya contiene esa actividad");
        if (plan.agregarActividad(actividad)) return "Actividad añadida: " + plan;
        throw new PlanesAgregarActividadException("La capacidad maxima del plan es menor que el aforo de la actividad");
    }

    /**
     * Aquí se mira si el usuario es el propietario del plan, esto sucede cuando el usuario ha creado el plan.
     *
     * @param usuario
     * @param plan
     * @return boolean
     */
    public boolean esPropietario(Usuario usuario, Plan plan) {
        return plan.getPropietario().equals(usuario);
    }

    /**
     * Con este método se calcula el tiempo total transcurrido, tras realizar todas las actividades de un plan. Se añaden 20 min que es el tiempo que transcurre entre actividades.
     *
     * @param plan
     * @return int
     */
    public int calcularTiempoTotal(Plan plan) {
        int tiempo = 0;
        List<Actividad> actividades = plan.getActividades();
        for (int i = 0; i < actividades.size(); i++) {
            tiempo += actividades.get(i).getDuracion();
            if (i < actividades.size() - 1) tiempo += 20;
        }
        // tiempo en minutos
        return tiempo;
    }

    /**
     * Para este método se calculará el coste del plan para un usuario, se iran sumando los costes de cada actividad dentro del plan. En algunas actividades específicas puede haber descuentos si el usuario cumple ciertos requisitos.
     *
     * @param nombrePlan
     * @param usuario
     * @return double
     */
    public double calcularCosteParticipante(String nombrePlan, Usuario usuario) throws PlanesException {
        Plan plan = buscarPlan(nombrePlan);
        if (plan == null) throw new PlanesException("Error: No existe un plan con ese nombre");
        double coste = 0;
        for (Actividad actividad : plan.getActividades())
            coste += actividad.getCoste() * (1 - actividad.getDescuento(usuario));
        // getDescuento te devuelve el porcentaje de descuento y se usa para restárselo al coste
        return coste;
    }

    /**
     * Aquí simplemente se mira si se puede incorporar un participante al plan, se comprueba que la capacidad maxima sea inferior a la lista de participantes del plan.
     *
     * @param plan
     * @return boolean
     */
    public boolean hayCapacidadDisponible(Plan plan) {
        return plan.getCapacidadMax() == -1 || plan.getParticipantes().size() < plan.getCapacidadMax();
    }

    /**
     * En este método, se pretende unir un usuario a un plan. Se comprueba que el plan exista, también que el plan sea posterior al momento de unirse y que el usuario no esté ya subscrito, y por último se mira que el plan no este lleno.
     *
     * @param idPlan
     * @param usuario
     * @return String
     */
    public String unirseAPlan(int idPlan, Usuario usuario) throws PlanesException {
        if (idPlan > listaPlan.size()) throw new PlanesUnirseException("Error: No existe un plan con ese id");
        Plan plan = listaPlan.get(idPlan - 1);
        if (LocalDateTime.now().isAfter(plan.getFechaHora()))
            throw new PlanesUnirseException("Error: Ya ha pasado la fecha de este plan");
        if (plan.getParticipantes().contains(usuario))
            throw new PlanesUnirseException("Error: No se puede subscribir dos veces al mismo plan");
        if (!hayCapacidadDisponible(plan))
            throw new PlanesUnirseException("Error: El plan no tiene capacidad suficiente");
        plan.agregarParticipante(usuario);
        usuario.suscribirPlan(plan);
        return "Unido al plan: " + plan;
    }

    /**
     * En este método se agregará una calificacion a un plan, pero, solo pueden añadir calificacion aquellos usuarios que hayan participado en el mismo, en caso contrario no se permitirá que puntúen.
     *
     * @param calificacion
     * @param idPlan
     * @param usuario
     */
    public void agregarCalificacion(int calificacion, int idPlan, Usuario usuario) throws PlanesAgregarCalificacionException {
        for (Map.Entry<Plan, Boolean> entry : usuario.getPlanesSuscritos().entrySet()) {
            Plan plan = entry.getKey();
            if (plan.getIdPlan() == idPlan) {
                if (entry.getValue())
                    throw new PlanesAgregarCalificacionException("El usuario ya ha puntuado este plan");
                else if (plan.getFechaHora().plusMinutes(calcularTiempoTotal(plan)).isBefore(LocalDateTime.now())) {
                    entry.setValue(true);
                    plan.agregarCalificacion(calificacion);
                    return;
                }
            }
        }
        throw new PlanesAgregarCalificacionException("El usuario no a participado en el plan y no puede puntuarlo");
    }

    /**
     * En este método, un usuario pretende abandonar un plan, para ello se comprueba si existe el plan y si pertenece al mismo, también se revisa si el plan se realizara en el futuro, ya que no tiene sentido desapuntarte de un plan pasado.
     *
     * @param idPlan
     * @param usuario
     * @return String
     */
    public String abandonarPlan(int idPlan, Usuario usuario) throws PlanesException {
        if (idPlan > listaPlan.size()) throw new PlanesAbandonarPlanException("Error: No existe un plan con ese id");
        Plan plan = listaPlan.get(idPlan - 1);
        if (LocalDateTime.now().isAfter(plan.getFechaHora()))
            throw new PlanesAbandonarPlanException("Error: Ya ha pasado la fecha de este plan");
        if (!plan.getParticipantes().contains(usuario))
            throw new PlanesAbandonarPlanException("El usuario no está registrado en el plan");
        plan.getParticipantes().remove(usuario);
        usuario.getPlanesSuscritos().remove(plan);
        return "El usuario ha abandonado el plan con éxito";
    }

    public String eliminarPlan(String nombrePlan, Usuario usuario) throws PlanesException {
        Plan plan = buscarPlan(nombrePlan);
        if (plan == null) throw new PlanesException("Error: No existe un plan con ese nombre");
        if (!plan.getPropietario().equals(usuario))
            throw new PlanesException("Error: El usuario no es propietario de este plan");
        listaPlan.remove(plan);
        return "Plan eliminado con exito";
    }

    /**
     * Aquí se ordenan las actividades por la hora a la que se van a realizar, poniendo primero las que empiezan antes.
     */
    public String ordenarActividadesPorHora() {
        List<Plan> listaOrdenada = listaPlan;
        listaOrdenada.sort(Comparator.comparing(Plan::getFechaHora));
        return "Planes: " + listaOrdenada;
    }

    /**
     * Aquí se ordenan las actividades por calificacion, poniendo primero las que peor calificacion tienen.
     */
    public String ordenarActividadesPorCalificacion() {
        List<Plan> listaOrdenada = listaPlan;
        listaOrdenada.sort(Comparator.comparingInt(Plan::getCalificacion));
        return "Planes: " + listaOrdenada;
    }

    /**
     * Este método privado que solo se usa en esta clase se utiliza para buscar un Plan concreto con el nombre del plan.
     *
     * @param nombrePlan
     * @return Plan
     */
    private Plan buscarPlan(String nombrePlan) {
        for (Plan plan : listaPlan) {
            if (plan.getNombre().equals(nombrePlan)) return plan;
        }
        return null;
    }

    public Plan getPlan(int idPlan) {
        for (Plan plan : listaPlan) {
            if (plan.getIdPlan() == idPlan) return plan;
        }
        return null;
    }

    /**
     * Este metodo es la primera consulta donde se busca que planes hay antes de cierta fecha pasada como parametro.
     *
     * @param date
     * @return String
     */
    public String planesAntesDeFecha(LocalDateTime date) {
        List<Plan> listaAnterior = new ArrayList<>();
        for (Plan plan : listaPlan) {
            if (plan.getFechaHora().isBefore(date))
                listaAnterior.add(plan);
        }
        if (!listaAnterior.isEmpty()) return "Planes Anteriores a " + date + ":\n" + listaAnterior;
        return "No hay ningún plan anterior a esa fecha";
    }

    /**
     * Esta sera la segunda consulta donde el usuario puede ver si alguien que conoce esta en algun plan del sistema.
     *
     * @param nombrePersona
     * @return String
     */
    public String planesConPersonaIndicada(String nombrePersona) {
        List<Plan> listaNueva = new ArrayList<>();
        for (Plan plan : listaPlan) {
            for (Usuario usuario : plan.getParticipantes())
                if (usuario.getNombreUsuario().equals(nombrePersona))
                    listaNueva.add(plan);
        }
        if (!listaNueva.isEmpty()) return "Planes con " + nombrePersona + ": \n" + listaNueva;
        return "Esta persona no está en ningún plan";
    }
}

package es.upm.etsisi.iwsim21.grupo5.poo.modelo;

import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.usuarios.UsuarioEdadNegativaException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Una clase principal del proyecto, donde se contienen todos los Atributos y Metodos especificos de un Usuario.
 */
public class Usuario {
    private String nombreUsuario;
    private int edad;
    private String movil;
    private String password;
    private Map<Plan, Boolean> planesSuscritos; // Se usa un Map para saber qué planes han sido puntuados
    private List<Plan> planesCreados;
    private int id;


    public Usuario(int id, String nombreUsuario, int edad, String movil, String contrasenia) throws UsuarioEdadNegativaException {
        if (edad <= 0) throw new UsuarioEdadNegativaException("Error: la edad no puede ser negativa ni cero.");
        this.nombreUsuario = nombreUsuario;
        this.edad = edad;
        this.movil = movil;
        this.password = contrasenia;
        this.planesSuscritos = new HashMap<>();
        this.planesCreados = new LinkedList<>();
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getEdad() {
        return edad;
    }

    public String getMovil() {
        return movil;
    }

    public String getPassword() {
        return password;
    }

    public Map<Plan, Boolean> getPlanesSuscritos() {
        return planesSuscritos;
    }

    public void suscribirPlan(Plan plan) {
        planesSuscritos.put(plan, false);
    }

    public List<Plan> getPlanesCreados() {
        return planesCreados;
    }

    @Override
    public String toString() {
        return nombreUsuario;
    }

    public int getId() {
        return id;
    }

    public String planesSuscritosToString() {
        StringBuilder planes = new StringBuilder();
        for (Map.Entry<Plan, Boolean> plan : planesSuscritos.entrySet()) {
            planes.append(plan.getKey()).append("\n");
        }
        int length = planes.length();
        planes.delete(length - 1, length); //quita el ultimo \n
        return planes.toString();
    }
}

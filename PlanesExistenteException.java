package es.upm.etsisi.iwsim21.grupo5.poo.modelo;

import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.actividades.ActividadesAtributosIncorrectosException;

/**
 * Clase principal del modelo, donde se añaden atributos y métodos que caracterizan a las actividades.
 */
public class Actividad {
    protected String nombreActividad;
    protected String descripcion;
    protected int duracion;
    protected double coste;
    protected int aforo;
    protected int idActividad;


    public Actividad(int id, String nombre, String descripcion, int duracion, double coste, int aforo) {
        if (duracion <= 0) throw new ActividadesAtributosIncorrectosException("Error: Duracion no puede ser negativa");
        if (coste < 0) throw new ActividadesAtributosIncorrectosException("Error: Coste no puede ser negativo");
        if (aforo <= 0 && aforo != -1)
            throw new ActividadesAtributosIncorrectosException("Error: Aforo no puede ser negativo");
        this.idActividad = id;
        this.nombreActividad = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.coste = coste;
        this.aforo = aforo;
    }

    public Actividad(int id, String nombre, String descripcion, int duracion, double coste) {
        this(id, nombre, descripcion, duracion, coste, -1); //-1 para indicar aforo sin límite
    }

    public String getNombre() {
        return nombreActividad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracion() {
        return duracion;
    }

    public double getCoste() {
        return coste;
    }

    public int getAforo() {
        return aforo;
    }

    public double getDescuento(Usuario u) {
        return 0;
    }

    public int getId() {
        return idActividad;
    }

    @Override
    public String toString() {
        return String.format("id:%d; nombre:%s; descripcion:%s; duracion:%d; coste:%s; aforo:%s\n",
                idActividad, nombreActividad, descripcion, duracion, coste, aforo == -1 ? "sin limite" : aforo);
    }

}



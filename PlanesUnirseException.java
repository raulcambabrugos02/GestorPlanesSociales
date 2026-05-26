package es.upm.etsisi.iwsim21.grupo5.poo.modelo;

/**
 * Esta clase cine es una clase heredada de la clase actividad, donde hereda todos sus atributos y métodos, añadiendo unos atributos finales de descuento.
 */
public class Cine extends Actividad {
    private static final float DESCUENTO_JOVENES = 0.5f;
    private static final int EDAD_JOVEN = 21;

    public Cine(int id, String nombre, String descripcion, int duracion, double coste, int aforo) {
        super(id, nombre, descripcion, duracion, coste, aforo);
    }

    public Cine(int id, String nombre, String descripcion, int duracion, double coste) {
        super(id, nombre, descripcion, duracion, coste);
    }

    /** Este método que recibe un usuario por parámetro, comprueba si el usuario cumple con los requisitos de edad para obtener alguno de los descuentos. Devuelve un número double.
     * @param u
     * @return double
     */
    @Override
    public double getDescuento(Usuario u) {
        return u.getEdad() <= EDAD_JOVEN ? DESCUENTO_JOVENES : 0;
    }

}

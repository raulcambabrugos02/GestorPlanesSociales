package es.upm.etsisi.iwsim21.grupo5.poo.modelo;

/**
 * Esta clase teatro es una clase heredada de la clase actividad, donde hereda todos sus atributos y métodos, añadiendo unos atributos finales de descuento y edades.
 */
public class Teatro extends Actividad {
    private static final float DESCUENTO_JOVENES = 0.5f;
    private static final int EDAD_JOVEN = 25;
    private static final float DESCUENTO_PENSIONISTAS = 0.7f;
    private static final int EDAD_PENSIONISTA = 65;

    public Teatro(int id, String nombre, String descripcion, int duracion, double coste, int aforo) {
        super(id, nombre, descripcion, duracion, coste, aforo);
    }

    public Teatro(int id, String nombre, String descripcion, int duracion, double coste) {
        super(id, nombre, descripcion, duracion, coste);
    }

    /** Este método que recibe un usuario por parámetro, comprueba si el usuario cumple los requisitos de edad para obtener alguno de los descuentos. Devuelve un número double.
     * @param u
     * @return double
     */
    @Override
    public double getDescuento(Usuario u) {
        int edad = u.getEdad();
        return edad <= EDAD_JOVEN ? DESCUENTO_JOVENES : edad >= EDAD_PENSIONISTA ? DESCUENTO_PENSIONISTAS : 0;
    }
}

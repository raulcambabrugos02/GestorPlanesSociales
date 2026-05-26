package es.upm.etsisi.iwsim21.grupo5.poo.controlador;

import es.upm.etsisi.iwsim21.grupo5.poo.modelo.Usuario;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.usuarios.UsuarioException;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.exceptions.usuarios.UsuarioExistenteException;

import java.util.LinkedList;
import java.util.List;

/**
 * Esta clase es la controladora de usuarios, donde se almacenan todos los usuarios en una lista, y se realizan diferentes métodos de búsqueda, agregación o eliminación de usuarios.
 */
public class CUsuario {
    private final List<Usuario> listUsuarios;

    public CUsuario() {
        listUsuarios = new LinkedList<>();
    }

    public List<Usuario> getListUsuarios() {
        return listUsuarios;
    }

    /**
     * Este método comprueba si el usuario pasado por parámetro se encuentra registrado, si lo está, comprueba si la contraseña pasada por parámetro es correcta, si ambas condiciones se cumplen, se devuelve el usuario.
     *
     * @param nombreUsuario
     * @param password
     * @return Usuario
     */
    public Usuario iniciarSesion(String nombreUsuario, String password) {
        for (Usuario usuario : listUsuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario) && usuario.getPassword().equals(password))
                return usuario;
        }
        return null;
    }

    /**
     * Este método registra un usuario en el sistema de planes teniendo en cuenta que la edad sea > 0 y que el usuario no exista previamente.
     *
     * @param nombreUsuario
     * @param edad
     * @param movil
     * @param password
     * @return String
     */
    public String registrarUsuario(String nombreUsuario, int edad, String movil, String password) throws UsuarioException {
        if (buscarUsuario(nombreUsuario) != null) throw new UsuarioExistenteException("Error: Nombre ya existente");
        listUsuarios.add(new Usuario(listUsuarios.size() + 1, nombreUsuario, edad, movil, password));
        return String.format("Usuario creado: id:%s; nombre:%s; edad%d; movil:%s; clave:%s",
                listUsuarios.size() + 1, nombreUsuario, edad, movil, password);
    }


    /**
     * Este método muestra un mensaje por pantalla que indica que se ha cerrado sesión correctamente.
     *
     * @return String
     */
    public String cerrarSesion() {
        return "Logout correcto";
    }

    /**
     * Este método devuelve por pantalla todos los planes a los que está suscrito un usuario.
     *
     * @param usuario
     */
    public String listarPlanesSuscritos(Usuario usuario) {
        if (!usuario.getPlanesSuscritos().isEmpty())
            return "Usted esta suscrito a los siguientes planes: \n" + usuario.planesSuscritosToString();
        else return "Usted no esta suscrito a ningún plan.";
    }

    /**
     * Este método recibe un nombre de usuario por parámetro y comprueba si coincide con algún nombre de la lista de usuarios. Si se cumple la condición, devuelve un usuario.
     *
     * @param nombreUsuario
     * @return
     */
    private Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario usuario : listUsuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario)) return usuario;
        }
        return null;
    }

    public Usuario getUsuario(int idUsuario) {
        if (!(idUsuario > 0 && idUsuario <= listUsuarios.size())) return null;
        return listUsuarios.get(idUsuario - 1);
    }


}

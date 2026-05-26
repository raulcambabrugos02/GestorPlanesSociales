package es.upm.etsisi.iwsim21.grupo5.poo;

import es.upm.etsisi.iwsim21.grupo5.poo.controlador.CActividad;
import es.upm.etsisi.iwsim21.grupo5.poo.controlador.CPlan;
import es.upm.etsisi.iwsim21.grupo5.poo.controlador.CUsuario;
import es.upm.etsisi.iwsim21.grupo5.poo.modelo.*;
import es.upm.etsisi.iwsim21.grupo5.poo.vista.VistaCLI;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;

public class GPlanesSociales {
    private static final String FICHERO_USUARIOS = "src/main/resources/usuarios.csv";
    private static final String FICHERO_PLAN = "src/main/resources/plan.csv";
    private static final String FICHERO_ACTIVIDAD = "src/main/resources/actividad.csv";
    private static final String FICHERO_TIENE = "src/main/resources/tiene.csv";
    private static final String FICHERO_PARTICIPA = "src/main/resources/participa.csv";
    CActividad cActividad;
    CPlan cPlan;
    CUsuario cUsuario;
    VistaCLI vista;

    public static void main(String[] args) {
        GPlanesSociales main = new GPlanesSociales();
        try {
            main.init();
            main.start();
        } catch (ParseException e) {
            main.vista.mensajeSalida(e.getMessage());
        }
        main.end();
    }

    private void init() {
        cActividad = new CActividad();
        cPlan = new CPlan();
        cUsuario = new CUsuario();
        vista = new VistaCLI();
        vista.welcome();
        poblador();
    }

    private void poblador() {
        try {
            leerCSVUsuario();
            leerCSVPlan();
            leerCSVParticipa();
            leerCSVActividad();
            leerCSVTiene();
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }

    private void end() {
        vista.adios();
    }


    private void start() throws ParseException, RuntimeException {
        String opcion = "";
        Usuario usuarioLogueado = null;
        vista.mostrarOpciones();
        while (!opcion.equals("exit")) {
            try {
                System.out.print(usuarioLogueado == null ? "gps> " : "gps-" + usuarioLogueado.getNombreUsuario() + "> ");
                String[] segmentacion = vista.seleccionarOpcion().split(":", 2);
                opcion = segmentacion[0].trim();
                String[] parametros = new String[0];
                if (segmentacion.length > 1) {
                    parametros = segmentacion[1].split(";");
                    for (int i = 0; i < parametros.length; i++) parametros[i] = parametros[i].trim();
                }
                if (opcion.equals("login")) {
                    usuarioLogueado = cUsuario.iniciarSesion(parametros[0], parametros[1]);
                    vista.mensajeSalida(usuarioLogueado == null ? "login incorrecto" : "login correcto");
                } else if (opcion.equals("create-user")) {
                    vista.mensajeSalida(cUsuario.registrarUsuario(parametros[0], Integer.parseInt(parametros[1]), parametros[2], parametros[3]));
                    guardarUsuarioCSV(cUsuario.getUsuario(cUsuario.getListUsuarios().size()));
                } else if (usuarioLogueado != null) {
                    usuarioLogueado = opcionesULogueado(opcion, usuarioLogueado, parametros);
                } else if (!opcion.equals("exit")) vista.mensajeSalida("Usuario sin loguear");
            } catch (ArrayIndexOutOfBoundsException e) {
                vista.mensajeSalida("Error: formato de comando introducido incorrecto");
            } catch (RuntimeException | IOException e) {
                vista.mensajeSalida(e.getMessage());
            }
        }
    }

    private Usuario opcionesULogueado(String opcion, Usuario usuarioLogueado, String[] parametros) throws ArrayIndexOutOfBoundsException, IOException {
        switch (opcion) {
            case "logout" -> {
                usuarioLogueado = null;
                vista.mensajeSalida(cUsuario.cerrarSesion());
            }
            case "create-activity" -> {
                if (parametros.length == 6) {
                    vista.mensajeSalida(cActividad.crearActividadAforoLimitado(parametros[0], parametros[1], parametros[2], Integer.parseInt(parametros[3]), Double.parseDouble(parametros[4]), Integer.parseInt(parametros[5])));
                } else if (parametros.length == 5) {
                    vista.mensajeSalida(cActividad.crearActividadSinAforo(parametros[0], parametros[1], parametros[2], Integer.parseInt(parametros[3]), Double.parseDouble(parametros[4])));
                } else throw new RuntimeException("Nº de parámetros incorrecto");
                guardarActividadCSV(cActividad.getActividad(cActividad.getListActividades().size()));
            }
            case "create-event" -> {
                LocalDateTime date = LocalDateTime.parse(parametros[1]);
                if (parametros.length == 4) {
                    vista.mensajeSalida(cPlan.crearPlanConCapacidad(parametros[0], date, parametros[2], Integer.parseInt(parametros[3]), usuarioLogueado));
                } else if (parametros.length == 3) {
                    vista.mensajeSalida(cPlan.crearPlanIlimitado(parametros[0], date, parametros[2], usuarioLogueado));
                } else throw new RuntimeException("Nº de parámetros incorrecto");
                guardarPlanCSV(cPlan.getPlan(cPlan.getListaPlan().size()));
            }
            case "delete-event" -> {
                if (parametros.length == 1) {
                    vista.mensajeSalida(cPlan.eliminarPlan(parametros[0], usuarioLogueado));
                    eliminarPlanCSV(parametros[0]);
                } else throw new RuntimeException("Nº de parámetros incorrecto");
            }
            case "add-activity-plan" -> {
                int idActividad = Integer.parseInt(parametros[1]);
                int idPlan = Integer.parseInt(parametros[0]);
                vista.mensajeSalida(cPlan.agregarActividad(cActividad.getActividad(idActividad), idPlan, usuarioLogueado));
                aniadirActividadPlanCSV(idPlan, idActividad, cPlan.getPlan(idPlan).getPropietario().getId());
            }
            case "list-events" -> {
                if (parametros[0].equals("fecha")) vista.mensajeSalida(cPlan.ordenarActividadesPorHora());
                else if (parametros[0].equals("puntuacion"))
                    vista.mensajeSalida(cPlan.ordenarActividadesPorCalificacion());
                else throw new RuntimeException("Parametros incorrectos");
            }
            case "join-event" -> {
                int idPlan = Integer.parseInt(parametros[0]);
                vista.mensajeSalida(cPlan.unirseAPlan(idPlan, usuarioLogueado));
                aniadirParticipantePlanCSV(usuarioLogueado.getId(), idPlan);
            }
            case "exit-event" -> {
                vista.mensajeSalida(cPlan.abandonarPlan(Integer.parseInt(parametros[0]), usuarioLogueado));
                abandonarPlanCSV(usuarioLogueado.getId(), Integer.parseInt(parametros[0]));
            }
            case "rate" ->
                    cPlan.agregarCalificacion(Integer.parseInt(parametros[0]), Integer.parseInt(parametros[1]), usuarioLogueado);
            case "list-events-subscribed" -> vista.mensajeSalida(cUsuario.listarPlanesSuscritos(usuarioLogueado));
            case "cost-event-subscribed" -> {
                if (parametros.length == 1)
                    vista.mensajeSalida("El coste total del plan sera: " + cPlan.calcularCosteParticipante(parametros[0], usuarioLogueado));
                else throw new RuntimeException("Numero de parametros incorrectos");
            }
            case "search-events-before-date" -> {
                if (parametros.length == 1) {
                    LocalDateTime date = LocalDateTime.parse(parametros[0]);
                    vista.mensajeSalida(cPlan.planesAntesDeFecha(date));
                } else throw new RuntimeException("Numero de parametros incorrectos");
            }
            case "search-events-with-friend" -> {
                if (parametros.length == 1) {
                    vista.mensajeSalida(cPlan.planesConPersonaIndicada(parametros[0].trim()));
                } else throw new RuntimeException("Numero de parametros incorrectos");
            }
            case "search-activity-coste-aforo" -> {
                if (parametros.length == 2) {
                    vista.mensajeSalida(cActividad.actividadAforoMayorCosteInferior(Double.parseDouble(parametros[0]), Integer.parseInt(parametros[1])));
                } else throw new RuntimeException("Numero de parametros incorrectos");
            }
            case "exit" -> {
            }
            default -> throw new RuntimeException("No existe esa opción");
        }
        return usuarioLogueado;
    }

    private void leerCSVUsuario() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(GPlanesSociales.FICHERO_USUARIOS));
        CSVFormat format = CSVFormat.DEFAULT.withHeader();
        CSVParser csvParser = new CSVParser(reader, format);
        for (CSVRecord csvRecord : csvParser) {
            cUsuario.registrarUsuario(csvRecord.get("nombreUsuario"), Integer.parseInt(csvRecord.get("edad")), csvRecord.get("movil"), csvRecord.get("contrasenia"));
        }
        csvParser.close();
        reader.close();
    }

    private void leerCSVPlan() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(GPlanesSociales.FICHERO_PLAN));
        CSVFormat format = CSVFormat.DEFAULT.withHeader();
        CSVParser csvParser = new CSVParser(reader, format);
        for (CSVRecord csvRecord : csvParser) {
            LocalDateTime fechaHora = LocalDateTime.parse(csvRecord.get("fechaHora"));
            String capacidadMax = csvRecord.get("capacidadMax");
            if ("".equals(capacidadMax)) {
                cPlan.crearPlanIlimitado(csvRecord.get("nombrePlan"), fechaHora, csvRecord.get("lugar"), cUsuario.getUsuario(Integer.parseInt(csvRecord.get("idPropietario"))));
            } else {
                cPlan.crearPlanConCapacidad(csvRecord.get("nombrePlan"), fechaHora, csvRecord.get("lugar"), Integer.parseInt(capacidadMax), cUsuario.getUsuario(Integer.parseInt(csvRecord.get("idPropietario"))));
            }
        }
        csvParser.close();
        reader.close();
    }

    private void leerCSVActividad() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(GPlanesSociales.FICHERO_ACTIVIDAD));
        CSVFormat format = CSVFormat.DEFAULT.withHeader();
        CSVParser csvParser = new CSVParser(reader, format);
        for (CSVRecord csvRecord : csvParser) {
            String aforo = csvRecord.get("aforo");
            if ("".equals(aforo)) {
                cActividad.crearActividadSinAforo(csvRecord.get("tipoActividad"), csvRecord.get("nombreActividad"), csvRecord.get("descripcion"), Integer.parseInt(csvRecord.get("duracion")), Double.parseDouble(csvRecord.get("coste")));
            } else {
                cActividad.crearActividadAforoLimitado(csvRecord.get("tipoActividad"), csvRecord.get("nombreActividad"), csvRecord.get("descripcion"), Integer.parseInt(csvRecord.get("duracion")), Double.parseDouble(csvRecord.get("coste")), Integer.parseInt(aforo));
            }
        }
        csvParser.close();
        reader.close();
    }

    private void leerCSVParticipa() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(GPlanesSociales.FICHERO_PARTICIPA));
        CSVFormat format = CSVFormat.DEFAULT.withHeader();
        CSVParser csvParser = new CSVParser(reader, format);
        for (CSVRecord csvRecord : csvParser) {
            cPlan.unirseAPlan(Integer.parseInt(csvRecord.get("idPlan")), cUsuario.getUsuario(Integer.parseInt(csvRecord.get("idUsuario"))));
        }
    }

    private void leerCSVTiene() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(GPlanesSociales.FICHERO_TIENE));
        CSVFormat format = CSVFormat.DEFAULT.withHeader();
        CSVParser csvParser = new CSVParser(reader, format);
        for (CSVRecord csvRecord : csvParser) {
            cPlan.agregarActividad(cActividad.getActividad(Integer.parseInt(csvRecord.get("idActividad"))), Integer.parseInt(csvRecord.get("idPlan")), cUsuario.getUsuario(Integer.parseInt(csvRecord.get("idPropietarioPlan"))));
        }
    }

    private void guardarPlanCSV(Plan plan) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(FICHERO_PLAN, true));
        String capacidadMax = plan.getCapacidadMax() == -1 ? "" : String.valueOf(plan.getCapacidadMax());
        out.println(plan.getNombre() + "," + plan.getFechaHora() + "," + plan.getLugar() + "," + capacidadMax + "," + plan.getPropietario().getId());
        out.close();
    }

    private void guardarUsuarioCSV(Usuario usuario) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(FICHERO_USUARIOS, true));
        out.println(usuario.getNombreUsuario() + "," + usuario.getEdad() + "," + usuario.getMovil() + "," + usuario.getEdad());
        out.close();
    }

    private void guardarActividadCSV(Actividad actividad) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(FICHERO_ACTIVIDAD, true));
        String tipo = "Generic";
        if (actividad instanceof Cine) tipo = "Cinema";
        else if (actividad instanceof Teatro) tipo = "Theatre";
        out.println(tipo + "," + actividad.getNombre() + "," + actividad.getDescripcion() + "," + actividad.getDuracion() + "," + actividad.getCoste() + "," + actividad.getAforo());
        out.close();
    }

    private void aniadirActividadPlanCSV(int idPlan, int idActividad, int idPropietario) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(FICHERO_TIENE, true));
        out.println(idPlan + "," + idActividad + "," + idPropietario);
        out.close();
    }

    private void aniadirParticipantePlanCSV(int idUsuario, int idPlan) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(FICHERO_PARTICIPA, true));
        out.println(idUsuario + "," + idPlan);
        out.close();
    }

    private void eliminarPlanCSV(String nombrePlan) throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader(FICHERO_PLAN));
        PrintWriter escritor = new PrintWriter(new FileWriter(FICHERO_PLAN + "_temp"));
        String linea;
        while ((linea = lector.readLine()) != null) {
            String nombreActual = linea.split(",")[0];
            if (!nombreActual.equals(nombrePlan)) {
                escritor.println(linea);
            }
        }
        lector.close();
        escritor.close();
        new File(FICHERO_PLAN).delete();
        new File(FICHERO_PLAN + "_temp").renameTo(new File(FICHERO_PLAN));
    }

    private void abandonarPlanCSV(int idUsuario, int idPlan) throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader(FICHERO_PARTICIPA));
        PrintWriter escritor = new PrintWriter(new FileWriter(FICHERO_PARTICIPA + "_temp"));
        escritor.println(lector.readLine()); //lee la primera línea y la escribe para que no de error el parseInt
        String linea;
        while ((linea = lector.readLine()) != null) {
            String[] campos = linea.split(",");
            if (!(Integer.parseInt(campos[0]) == idUsuario && Integer.parseInt(campos[1]) == idPlan)) {
                escritor.println(linea);
            }
        }
        lector.close();
        escritor.close();
        new File(FICHERO_PARTICIPA).delete();
        new File(FICHERO_PARTICIPA + "_temp").renameTo(new File(FICHERO_PARTICIPA));
    }
}

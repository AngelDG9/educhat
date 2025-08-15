package clientes;

import interfaces.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class ClienteAcademico {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 54355);
            FabricaAcademica fabrica =
                (FabricaAcademica) registry.lookup("FabricaAcademica");
            NotificacionService notifSvc =
                (NotificacionService) registry.lookup("NotificacionService");

            Scanner sc = new Scanner(System.in);
            System.out.println("\n=====================================");
            System.out.println("       === Cliente Académico ===      ");
            System.out.println("=====================================");
            System.out.println("Seleccione su rol:");
            System.out.println("1. Administrador");
            System.out.println("2. Profesor");
            System.out.println("3. Alumno");
            System.out.print("\nOpción: ");
            int rol = Integer.parseInt(sc.nextLine());

            // Pedir DNI una sola vez
            System.out.print("\nIngrese DNI: ");
            String dni = sc.nextLine().trim();

            boolean existe;
            switch (rol) {
                case 1: existe = fabrica.existeAdministrador(dni); break;
                case 2: existe = fabrica.existeProfesor(dni);     break;
                case 3: existe = fabrica.existeAlumno(dni);       break;
                default:
                    System.out.println("\n⚠️ Rol inválido. Terminando.");
                    sc.close();
                    return;
            }

            String nombre;
            if (!existe) {
                nombre = JOptionPane.showInputDialog(
                    null,
                    "DNI no encontrado. Introduzca su nombre:",
                    "Registro Nuevo",
                    JOptionPane.QUESTION_MESSAGE
                );
                if (nombre == null || nombre.trim().isEmpty()) {
                    System.out.println("\n⚠️ Nombre no proporcionado. Saliendo.");
                    sc.close();
                    return;
                }
                switch (rol) {
                    case 1: fabrica.crearAdministrador(dni, nombre); break;
                    case 2: fabrica.crearProfesor(dni, nombre);     break;
                    case 3: fabrica.crearAlumno(dni, nombre);       break;
                }
            } else {
                switch (rol) {
                    case 1: nombre = fabrica.obtenerNombreAdministrador(dni); break;
                    case 2: nombre = fabrica.obtenerNombreProfesor(dni);     break;
                    default: nombre = fabrica.obtenerNombreAlumno(dni);       break;
                }
            }

            System.out.println("\n👋 Bienvenido, " + nombre + " (DNI: " + dni + ")");

            ClienteNotificationListener listener = null;
            if (rol == 3) {
                listener = new ClienteNotificationListener();
                Alumno stub = fabrica.getAlumno(dni);
                for (Asignatura a : stub.verAsignaturasMatriculadas()) {
                    notifSvc.subscribe(a.getId(), listener);
                }
            }

            switch (rol) {
                case 1: ejecutarAdministrador(fabrica, dni, sc); break;
                case 2: ejecutarProfesor(fabrica, dni, sc);     break;
                case 3: ejecutarAlumno(fabrica, dni, sc, notifSvc, listener); break;
            }

            sc.close();
            if (rol == 3) {
                synchronized (ClienteAcademico.class) {
                    ClienteAcademico.class.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* === ADMINISTRADOR === */
    private static void ejecutarAdministrador(FabricaAcademica fabrica,
                                              String dni,
                                              Scanner sc) throws Exception {
        Administrador admin = fabrica.getAdministrador(dni);

        int op;
        do {
            System.out.println("\n=====================================");
            System.out.println("         Menú Administrador         ");
            System.out.println("=====================================");
            System.out.println("1. Ver datos de administrador");
            System.out.println("2. Asignar profesor a asignatura");
            System.out.println("3. Crear asignatura");
            System.out.println("4. Borrar asignatura");
            System.out.println("5. Ver asignaturas existentes");
            System.out.println("0. Salir");
            System.out.print("\nSeleccione una opción: ");
            op = Integer.parseInt(sc.nextLine());
            switch (op) {
                case 1:
                    System.out.println("\nNombre: " + admin.getNombre());
                    System.out.println("DNI:    " + admin.getDni());
                    break;
                case 2:
                    System.out.print("\nIngrese DNI del profesor: ");
                    String dp = sc.nextLine();
                    System.out.print("ID asignatura: ");
                    String ia = sc.nextLine();
                    admin.asignarProfesor(dp, ia);
                    break;
                case 3:
                    System.out.println("\n📋 Asignaturas actuales:");
                    for (Asignatura a : admin.verAsignaturas()) {
                        System.out.println("  - " + a.getId() + ": " + a.getNombre());
                    }
                    System.out.print("\nIngrese nuevo ID de asignatura: ");
                    String idNueva = sc.nextLine();
                    System.out.print("Ingrese nombre de la nueva asignatura: ");
                    String nombreNueva = sc.nextLine();
                    admin.crearAsignatura(idNueva, nombreNueva);
                    System.out.println("✅ Asignatura creada: " + idNueva + " - " + nombreNueva);
                    break;
                case 4:
                    System.out.print("\nID a borrar: ");
                    String ib = sc.nextLine();
                    admin.borrarAsignatura(ib);
                    System.out.println("🗑️ Asignatura " + ib + " borrada.");
                    break;
                case 5:
                    System.out.println("\n📚 Asignaturas existentes:");
                    for (Asignatura a : admin.verAsignaturas()) {
                        System.out.println("  - " + a.getId()
                            + ": " + a.getNombre()
                            + " (Prof: " + a.getDniProfesor() + ")");
                    }
                    break;
                case 0:
                    System.out.println("\n👋 Saliendo Admin...");
                    break;
                default:
                    System.out.println("\n⚠️ Opción inválida.");
            }
        } while (op != 0);
    }

    /* === PROFESOR === */
    private static void ejecutarProfesor(FabricaAcademica fabrica,
                                         String dni,
                                         Scanner sc) throws Exception {
        Profesor prof = fabrica.getProfesor(dni);

        int op;
        do {
            System.out.println("\n=====================================");
            System.out.println("           Menú Profesor            ");
            System.out.println("=====================================");
            System.out.println("1. Ver datos de profesor");
            System.out.println("2. Enviar notificación");
            System.out.println("3. Subir contenido (en desarrollo)");
            System.out.println("4. Ver mis asignaturas");
            System.out.println("0. Salir");
            System.out.print("\nSeleccione una opción: ");
            op = Integer.parseInt(sc.nextLine());
            switch (op) {
                case 1:
                    System.out.println("\nNombre: " + prof.getNombre());
                    System.out.println("DNI:    " + prof.getDni());
                    break;
                case 2:
                    System.out.println("\n📋 Tus asignaturas:");
                    for (Asignatura a : prof.verAsignaturas()) {
                        System.out.println("  - " + a.getId() + ": " + a.getNombre());
                    }
                    System.out.print("\nID asignatura: ");
                    String i2 = sc.nextLine();
                    System.out.print("Asunto: ");
                    String s2 = sc.nextLine();
                    System.out.print("Mensaje: ");
                    String m2 = sc.nextLine();
                    prof.enviarNotificacion(i2, s2, m2);
                    break;
                case 3:
					System.out.print("\nFuncion en desarrollo..");
					/*
                    System.out.print("\nID asignatura: ");
                    String i3 = sc.nextLine();
                    System.out.print("Contenido (texto): ");
                    byte[] d3 = sc.nextLine().getBytes();
                    Contenido c3 = new servidor.ContenidoImpl(0, i3, d3);
                    prof.subirContenido(i3, c3);
                    System.out.println("✅ Contenido subido a " + i3);*/
                    break;
                case 4:
                    System.out.println("\n📚 Mis asignaturas:");
                    for (Asignatura a : prof.verAsignaturas()) {
                        System.out.println("  - " + a.getId() + ": " + a.getNombre());
                    }
                    break;
                case 0:
                    System.out.println("\n👋 Saliendo Prof...");
                    break;
                default:
                    System.out.println("\n⚠️ Opción inválida.");
            }
        } while (op != 0);
    }

    /* === ALUMNO === */
    private static void ejecutarAlumno(FabricaAcademica fabrica,
                                       String dni,
                                       Scanner sc,
                                       NotificacionService notifSvc,
                                       ClienteNotificationListener listener)
            throws Exception {
        Alumno alumno = fabrica.getAlumno(dni);

        int op;
        do {
            System.out.println("\n=====================================");
            System.out.println("            Menú Alumno            ");
            System.out.println("=====================================");
            System.out.println("1. Ver datos");
            System.out.println("2. Matricularse");
            System.out.println("3. Ver matriculadas");
            System.out.println("4. Ver disponibles");
            System.out.println("5. Leer notificaciones");
            System.out.println("6. Descargar contenido (en desarrollo)");
            System.out.println("7. Notif. al profe (en desarrollo)");
            System.out.println("0. Salir");
            System.out.print("\nSeleccione una opción: ");
            op = Integer.parseInt(sc.nextLine());
            switch (op) {
                case 1:
                    System.out.println("\nNombre: " + alumno.getNombre());
                    System.out.println("DNI:    " + alumno.getDni());
                    break;
                case 2:
                    System.out.println("\n📚 Asignaturas disponibles:");
                    for (Asignatura a : alumno.verAsignaturasDisponibles()) {
                        System.out.println("  - " + a.getId() + ": " + a.getNombre());
                    }
                    System.out.print("\nID a matricular: ");
                    String m2 = sc.nextLine();
                    alumno.matricular(m2);
                    notifSvc.subscribe(m2, listener);
                    System.out.println("✅ Suscrito a " + m2);
                    break;
                case 3:
                    System.out.println("\n✅ Matriculadas:");
                    for (Asignatura a : alumno.verAsignaturasMatriculadas()) {
                        System.out.println("  - " + a.getId() + ": " + a.getNombre());
                    }
                    break;
                case 4:
                    System.out.println("\n📚 Disponibles:");
                    for (Asignatura a : alumno.verAsignaturasDisponibles()) {
                        System.out.println("  - " + a.getId() + ": " + a.getNombre());
                    }
                    break;
                case 5:
                    System.out.println("\n📨 Notificaciones:");
                    for (String n : alumno.recibirNotificaciones()) {
                        System.out.println("  - " + n);
                    }
                    break;
                case 6:
					System.out.print("\nFuncion en desarrollo..");
					/*
                    System.out.print("\nID contenido: ");
                    String c6 = sc.nextLine();
                    Contenido cd = alumno.descargarContenido(c6);
                    System.out.println("📥 Bytes descargados: " + cd.getContenido().length); */
                    break;
                case 7:
					System.out.print("\nFuncion en desarrollo..");
					/*
                    System.out.print("\nID asignatura: ");
                    String i7 = sc.nextLine();
                    System.out.print("Mensaje: ");
                    String m7 = sc.nextLine();
                    alumno.enviarNotificacionProfesor(i7, m7); */
                    break;
                case 0:
                    System.out.println("\n👋 Adiós Alumno...");
                    break;
                default:
                    System.out.println("\n⚠️ Opción inválida.");
            }
        } while (op != 0);
    }
}


package servidor;

import interfaces.Alumno;
import interfaces.Asignatura;
import interfaces.Contenido;
import interfaces.NotificacionService;
import interfaces.NotificacionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoImpl extends UnicastRemoteObject
        implements Alumno, NotificacionListener {

    private static final NotificacionService notifSvc;
    static {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 54355);
            notifSvc = (NotificacionService) registry.lookup("NotificacionService");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final String dni;
    private final String nombre;

    public AlumnoImpl(String dni, String nombre) throws RemoteException {
        super();
        this.dni = dni;
        this.nombre = nombre;
    }

    @Override
    public String getNombre() throws RemoteException {
        return nombre;
    }

    @Override
    public String getDni() throws RemoteException {
        return dni;
    }

    @Override
    public void matricular(String idAsignatura) throws RemoteException {
        // Persistir matrícula
        String sql = "INSERT INTO matriculas (dni_alumno, id_asignatura) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, idAsignatura);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error matriculando al alumno", e);
        }
        // Suscribir al canal
        notifSvc.subscribe(idAsignatura, this);
        System.out.println("Suscrito a notificaciones de " + idAsignatura);
    }

    @Override
    public List<Asignatura> verAsignaturasMatriculadas() throws RemoteException {
        List<Asignatura> lista = new ArrayList<>();
        String sql =
            "SELECT a.id, a.nombre, a.dni_profesor " +
            "FROM asignaturas a JOIN matriculas m " +
            "  ON a.id = m.id_asignatura " +
            "WHERE m.dni_alumno = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AsignaturaImpl(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("dni_profesor")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Error obteniendo asignaturas matriculadas", e);
        }
        return lista;
    }

    @Override
    public List<Asignatura> verAsignaturasDisponibles() throws RemoteException {
        List<Asignatura> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, dni_profesor FROM asignaturas";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new AsignaturaImpl(
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("dni_profesor")
                ));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error obteniendo asignaturas disponibles", e);
        }
        return lista;
    }

    @Override
    public List<String> recibirNotificaciones() throws RemoteException {
        List<String> notifs = new ArrayList<>();
        String sql =
            "SELECT asunto, mensaje, fecha FROM notificaciones " +
            "WHERE id_asignatura IN (" +
            "  SELECT id_asignatura FROM matriculas WHERE dni_alumno = ?" +
            ") ORDER BY fecha DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifs.add(
                        "[" + rs.getTimestamp("fecha") + "] " +
                        rs.getString("asunto") + ": " +
                        rs.getString("mensaje")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Error recibiendo notificaciones", e);
        }
        return notifs;
    }

    @Override
    public void enviarNotificacionProfesor(String idAsignatura, String mensaje)
            throws RemoteException {
        String sql =
            "INSERT INTO notificaciones " +
            "(origen, dni_origen, id_asignatura, mensaje) " +
            "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "alumno");
            ps.setString(2, dni);
            ps.setString(3, idAsignatura);
            ps.setString(4, mensaje);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error enviando notificación al profesor", e);
        }
    }

    @Override
    public Contenido descargarContenido(String idContenido) throws RemoteException {
        String sql =
            "SELECT id_asignatura, path FROM contenidos WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(idContenido));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RemoteException("Contenido no encontrado");
                }
                String idAsig = rs.getString("id_asignatura");
                String path  = rs.getString("path");
                File f = new File(path);
                byte[] data = new byte[(int) f.length()];
                try (FileInputStream fis = new FileInputStream(f)) {
                    fis.read(data);
                }
                return new ContenidoImpl(
                    Integer.parseInt(idContenido),
                    idAsig,
                    data
                );
            }
        } catch (Exception e) {
            throw new RemoteException("Error descargando contenido", e);
        }
    }

    // === CALLBACK RMI ===
    @Override
    public void onNotificacion(String idAsignatura, String asunto, String mensaje)
            throws RemoteException {
        System.out.println(
            "\n Nuevo aviso en " + idAsignatura +
            " -- " + asunto + ": " + mensaje + "\n> "
        );
    }
}


package servidor;

import interfaces.Profesor;
import interfaces.Asignatura;
import interfaces.Contenido;
import interfaces.NotificacionService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesorImpl extends UnicastRemoteObject implements Profesor {
    private static final NotificacionService notifSvc;
    static {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 54355);
            notifSvc = (NotificacionService) registry.lookup("NotificacionService");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("No se pudo conectar al NotificacionService: " + e);
        }
    }

    private final String dni;
    private final String nombre;

    public ProfesorImpl(String dni, String nombre) throws RemoteException {
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
    public void enviarNotificacion(String idAsignatura, String asunto, String mensaje) throws RemoteException {
        // 1) Verificar que el profesor está asignado a esa asignatura
        boolean tieneAsignada = false;
        for (Asignatura a : verAsignaturas()) {
            if (a.getId().equals(idAsignatura)) {
                tieneAsignada = true;
                break;
            }
        }
        if (!tieneAsignada) {
            throw new RemoteException("No tienes asignada la asignatura " + idAsignatura);
        }

        // 2) Persistir en la base de datos
        String sql = "INSERT INTO notificaciones (origen, dni_origen, id_asignatura, asunto, mensaje) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "profesor");
            ps.setString(2, dni);
            ps.setString(3, idAsignatura);
            ps.setString(4, asunto);
            ps.setString(5, mensaje);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error guardando notificación en BD", e);
        }

        // 3) Publicar vía RMI para que todos los alumnos suscritos la reciban
        notifSvc.publish(idAsignatura, asunto, mensaje);

        System.out.println("Profesor " + dni + " envió notificación a " + idAsignatura);
    }

    @Override
    public void subirContenido(String idAsignatura, Contenido contenido) throws RemoteException {
        int contentId = -1;
        try {
            // Guardar el binario en disco
            String filePath = "servidor_contenidos/" + System.currentTimeMillis() + "_" + idAsignatura + ".bin";
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(contenido.getContenido());
            }

            // Insertar registro en BD
            String sql = "INSERT INTO contenidos (id_asignatura, path) VALUES (?, ?) RETURNING id";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, idAsignatura);
                ps.setString(2, filePath);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) contentId = rs.getInt("id");
                }
            }
            System.out.println("Contenido subido, id: " + contentId);
        } catch (IOException | SQLException e) {
            throw new RemoteException("Error subiendo contenido", e);
        }
    }

    @Override
    public List<Asignatura> verAsignaturas() throws RemoteException {
        List<Asignatura> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, dni_profesor FROM asignaturas WHERE dni_profesor = ?";
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
            throw new RemoteException("Error obteniendo asignaturas", e);
        }
        return lista;
    }
}


package servidor;

import interfaces.Administrador;
import interfaces.Asignatura;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdministradorImpl extends UnicastRemoteObject implements Administrador {
    private String dni;
    private String nombre;

    public AdministradorImpl(String dni, String nombre) throws RemoteException {
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
    public void asignarProfesor(String dniProfesor, String idAsignatura) throws RemoteException {
        String sql = "UPDATE asignaturas SET dni_profesor = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dniProfesor);
            ps.setString(2, idAsignatura);
            if (ps.executeUpdate() == 0) {
                throw new RemoteException("Asignatura no encontrada para asignar profesor");
            }
            System.out.println("Profesor " + dniProfesor + " asignado a la asignatura " + idAsignatura);
        } catch (SQLException e) {
            throw new RemoteException("Error asignando profesor", e);
        }
    }

    @Override
    public void crearAsignatura(String idAsignatura, String nombreAsignatura) throws RemoteException {
        String sql = "INSERT INTO asignaturas (id, nombre) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idAsignatura);
            ps.setString(2, nombreAsignatura);
            ps.executeUpdate();
            System.out.println("Asignatura creada: " + idAsignatura + " - " + nombreAsignatura);
        } catch (SQLException e) {
            throw new RemoteException("Error creando asignatura", e);
        }
    }

    @Override
    public void borrarAsignatura(String idAsignatura) throws RemoteException {
        // Sentencias para limpieza de dependencias
        String delMatriculas    = "DELETE FROM matriculas     WHERE id_asignatura = ?";
        String delNotificaciones= "DELETE FROM notificaciones WHERE id_asignatura = ?";
        String delContenidos    = "DELETE FROM contenidos     WHERE id_asignatura = ?";
        String clearProfesor    = "UPDATE asignaturas         SET dni_profesor = NULL WHERE id = ?";
        String delAsignatura    = "DELETE FROM asignaturas    WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1) Eliminar matriculas
            try (PreparedStatement ps = conn.prepareStatement(delMatriculas)) {
                ps.setString(1, idAsignatura);
                ps.executeUpdate();
            }

            // 2) Eliminar notificaciones
            try (PreparedStatement ps = conn.prepareStatement(delNotificaciones)) {
                ps.setString(1, idAsignatura);
                ps.executeUpdate();
            }

            // 3) Eliminar contenidos
            try (PreparedStatement ps = conn.prepareStatement(delContenidos)) {
                ps.setString(1, idAsignatura);
                ps.executeUpdate();
            }

            // 4) Desvincular profesor
            try (PreparedStatement ps = conn.prepareStatement(clearProfesor)) {
                ps.setString(1, idAsignatura);
                ps.executeUpdate();
            }

            // 5) Borrar la fila de la asignatura
            int updated;
            try (PreparedStatement ps = conn.prepareStatement(delAsignatura)) {
                ps.setString(1, idAsignatura);
                updated = ps.executeUpdate();
            }
            if (updated == 0) {
                conn.rollback();
                throw new RemoteException("Asignatura no encontrada para borrar");
            }

            conn.commit();
            System.out.println("Asignatura y dependencias borradas: " + idAsignatura);
        } catch (SQLException e) {
            throw new RemoteException("Error borrando asignatura y sus dependencias", e);
        }
    }

    @Override
    public List<Asignatura> verAsignaturas() throws RemoteException {
        List<Asignatura> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, dni_profesor FROM asignaturas";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id      = rs.getString("id");
                String nom     = rs.getString("nombre");
                String dniProf = rs.getString("dni_profesor");
                lista.add(new AsignaturaImpl(id, nom, dniProf));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error obteniendo asignaturas", e);
        }
        return lista;
    }
}


package servidor;

import interfaces.FabricaAcademica;
import interfaces.Administrador;
import interfaces.Profesor;
import interfaces.Alumno;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class FabricaAcademicaImpl extends UnicastRemoteObject implements FabricaAcademica {

    public FabricaAcademicaImpl() throws RemoteException {
        super();  // UnicastRemoteObject se exporta en el constructor
    }

    // Verifica si un DNI ya existe en la tabla indicada
    private boolean existeEnTabla(String tabla, String dni) throws RemoteException {
        String sql = "SELECT 1 FROM " + tabla + " WHERE dni = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RemoteException("Error verificando existencia en " + tabla, e);
        }
    }

    @Override
    public boolean existeAdministrador(String dni) throws RemoteException {
        return existeEnTabla("administradores", dni);
    }

    @Override
    public boolean existeProfesor(String dni) throws RemoteException {
        return existeEnTabla("profesores", dni);
    }

    @Override
    public boolean existeAlumno(String dni) throws RemoteException {
        return existeEnTabla("alumnos", dni);
    }

    // Inserta un nuevo registro en la tabla indicada
    private void insertarEnTabla(String tabla, String dni, String nombre) throws RemoteException {
        String sql = "INSERT INTO " + tabla + " (dni, nombre) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error insertando en " + tabla, e);
        }
    }

    @Override
    public void crearAdministrador(String dni, String nombre) throws RemoteException {
        insertarEnTabla("administradores", dni, nombre);
    }

    @Override
    public void crearProfesor(String dni, String nombre) throws RemoteException {
        insertarEnTabla("profesores", dni, nombre);
    }

    @Override
    public void crearAlumno(String dni, String nombre) throws RemoteException {
        insertarEnTabla("alumnos", dni, nombre);
    }

    // Recupera el nombre asociado a un DNI en la tabla indicada
    private String obtenerNombreDeTabla(String tabla, String dni) throws RemoteException {
        String sql = "SELECT nombre FROM " + tabla + " WHERE dni = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            } else {
                throw new RemoteException("No existe " + tabla + " con DNI " + dni);
            }
        } catch (SQLException e) {
            throw new RemoteException("Error obteniendo nombre de " + tabla, e);
        }
    }

    @Override
    public String obtenerNombreAdministrador(String dni) throws RemoteException {
        return obtenerNombreDeTabla("administradores", dni);
    }

    @Override
    public String obtenerNombreProfesor(String dni) throws RemoteException {
        return obtenerNombreDeTabla("profesores", dni);
    }

    @Override
    public String obtenerNombreAlumno(String dni) throws RemoteException {
        return obtenerNombreDeTabla("alumnos", dni);
    }

    // Devuelven el stub remoto correspondiente, cargando el nombre desde la BD
    @Override
    public Administrador getAdministrador(String dni) throws RemoteException {
        String nombre = obtenerNombreAdministrador(dni);
        return new AdministradorImpl(dni, nombre);
    }

    @Override
    public Profesor getProfesor(String dni) throws RemoteException {
        String nombre = obtenerNombreProfesor(dni);
        return new ProfesorImpl(dni, nombre);
    }

    @Override
    public Alumno getAlumno(String dni) throws RemoteException {
        String nombre = obtenerNombreAlumno(dni);
        return new AlumnoImpl(dni, nombre);
    }
}


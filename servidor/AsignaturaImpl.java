package servidor;

import interfaces.Asignatura;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignaturaImpl extends UnicastRemoteObject implements Asignatura {
    private String id;
    private String nombre;
    private String dniProfesor;

    public AsignaturaImpl(String id, String nombre, String dniProfesor) throws RemoteException {
        super();
        this.id = id;
        this.nombre = nombre;
        this.dniProfesor = dniProfesor;
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public String getNombre() throws RemoteException {
        return nombre;
    }

    @Override
    public List<String> getAlumnosMatriculados() throws RemoteException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT dni_alumno FROM matriculas WHERE id_asignatura = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                lista.add(rs.getString("dni_alumno"));
            }
        } catch(SQLException e){
            throw new RemoteException("Error obteniendo alumnos matriculados", e);
        }
        return lista;
    }

    @Override
    public String getDniProfesor() throws RemoteException {
        return dniProfesor;
    }
}


package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Administrador extends Remote {
    String getNombre() throws RemoteException;
    String getDni() throws RemoteException;
    void asignarProfesor(String dniProfesor, String idAsignatura) throws RemoteException;
    void crearAsignatura(String idAsignatura, String nombreAsignatura) throws RemoteException;
    void borrarAsignatura(String idAsignatura) throws RemoteException;
    List<Asignatura> verAsignaturas() throws RemoteException;
}


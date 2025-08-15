package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Asignatura extends Remote {
    String getId() throws RemoteException;
    String getNombre() throws RemoteException;
    List<String> getAlumnosMatriculados() throws RemoteException;
    String getDniProfesor() throws RemoteException;
}


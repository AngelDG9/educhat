package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Profesor extends Remote {
    String getNombre() throws RemoteException;
    String getDni() throws RemoteException;
    void enviarNotificacion(String idAsignatura, String asunto, String mensaje) throws RemoteException;
    void subirContenido(String idAsignatura, Contenido contenido) throws RemoteException;
    List<Asignatura> verAsignaturas() throws RemoteException;
}


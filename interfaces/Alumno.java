package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Alumno extends Remote {
    String getNombre() throws RemoteException;
    String getDni() throws RemoteException;
    void matricular(String idAsignatura) throws RemoteException;
    List<Asignatura> verAsignaturasMatriculadas() throws RemoteException;
    List<Asignatura> verAsignaturasDisponibles() throws RemoteException;
    List<String> recibirNotificaciones() throws RemoteException; // Mensajes con fecha y hora
    void enviarNotificacionProfesor(String idAsignatura, String mensaje) throws RemoteException;
    Contenido descargarContenido(String idContenido) throws RemoteException;
}


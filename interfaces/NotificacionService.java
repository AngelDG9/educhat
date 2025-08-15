package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificacionService extends Remote {
    // Profesor publica un mensaje
    void publish(String idAsignatura, String asunto, String mensaje) throws RemoteException;
    // Alumno se suscribe para recibir notificaciones de una asignatura
    void subscribe(String idAsignatura, NotificacionListener listener) throws RemoteException;
    // Alumno se desuscribe de una asignatura
    void unsubscribe(String idAsignatura, NotificacionListener listener) throws RemoteException;
}


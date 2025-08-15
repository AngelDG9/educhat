package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificacionListener extends Remote {
    void onNotificacion(String idAsignatura, String asunto, String mensaje) throws RemoteException;
}


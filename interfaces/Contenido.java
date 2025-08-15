package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Contenido extends Remote {
    int getId() throws RemoteException;
    byte[] getContenido() throws RemoteException;
    String getIdAsignatura() throws RemoteException;
}


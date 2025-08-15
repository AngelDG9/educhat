package clientes;

import interfaces.NotificacionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClienteNotificationListener extends UnicastRemoteObject 
                                           implements NotificacionListener {
    public ClienteNotificationListener() throws RemoteException {
        super();
    }

    @Override
    public void onNotificacion(String idAsignatura, String asunto, String mensaje)
            throws RemoteException {
        System.out.println("\n🔔 Notificación recibida:");
        System.out.println("   Asignatura: " + idAsignatura);
        System.out.println("   Asunto:     " + asunto);
        System.out.println("   Mensaje:    " + mensaje);
        System.out.print("> ");
    }
}


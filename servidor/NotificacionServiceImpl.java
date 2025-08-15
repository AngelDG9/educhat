package servidor;

import interfaces.NotificacionListener;
import interfaces.NotificacionService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class NotificacionServiceImpl extends UnicastRemoteObject implements NotificacionService {
    // Map<idAsignatura, lista de listeners>
    private final Map<String, List<NotificacionListener>> subs = new HashMap<>();

    public NotificacionServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void subscribe(String idAsignatura, NotificacionListener listener) throws RemoteException {
        subs.computeIfAbsent(idAsignatura, k -> new ArrayList<>()).add(listener);
        System.out.println("Listener suscrito a " + idAsignatura);
    }

    @Override
    public synchronized void unsubscribe(String idAsignatura, NotificacionListener listener) throws RemoteException {
        List<NotificacionListener> lst = subs.get(idAsignatura);
        if (lst != null) {
            lst.remove(listener);
            System.out.println("Listener desuscrito de " + idAsignatura);
        }
    }

    @Override
    public synchronized void publish(String idAsignatura, String asunto, String mensaje) throws RemoteException {
        List<NotificacionListener> lst = subs.get(idAsignatura);
        if (lst == null || lst.isEmpty()) {
            System.out.println("No hay listeners para " + idAsignatura);
            return;
        }
        // Notificar en bucle
        for (NotificacionListener listener : new ArrayList<>(lst)) {
            try {
                listener.onNotificacion(idAsignatura, asunto, mensaje);
            } catch (RemoteException e) {
                // eliminar listener desconectado
                lst.remove(listener);
            }
        }
        System.out.println("Publicado en " + idAsignatura + ": " + asunto);
    }
}


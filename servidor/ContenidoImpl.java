package servidor;

import interfaces.Contenido;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ContenidoImpl extends UnicastRemoteObject implements Contenido {
    private int id;
    private String idAsignatura;
    private byte[] contenido;

    public ContenidoImpl(int id, String idAsignatura, byte[] contenido) throws RemoteException {
        super();
        this.id = id;
        this.idAsignatura = idAsignatura;
        this.contenido = contenido;
    }

    @Override
    public int getId() throws RemoteException {
        return id;
    }

    @Override
    public byte[] getContenido() throws RemoteException {
        return contenido;
    }

    @Override
    public String getIdAsignatura() throws RemoteException {
        return idAsignatura;
    }
}


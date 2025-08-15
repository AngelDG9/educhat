package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FabricaAcademica extends Remote {
    // Obtiene el objeto Administrador; asume que ya existe o se creó antes
    Administrador getAdministrador(String dni) throws RemoteException;
    // Verifica si existe un administrador con ese DNI
    boolean existeAdministrador(String dni) throws RemoteException;
    // Crea un nuevo administrador (sólo si antes no existía)
    void crearAdministrador(String dni, String nombre) throws RemoteException;
    // Recupera el nombre guardado para ese DNI
    String obtenerNombreAdministrador(String dni) throws RemoteException;

    // Mismas tres operaciones para Profesor
    Profesor getProfesor(String dni) throws RemoteException;
    boolean existeProfesor(String dni) throws RemoteException;
    void crearProfesor(String dni, String nombre) throws RemoteException;
    String obtenerNombreProfesor(String dni) throws RemoteException;

    // Mismas tres operaciones para Alumno
    Alumno getAlumno(String dni) throws RemoteException;
    boolean existeAlumno(String dni) throws RemoteException;
    void crearAlumno(String dni, String nombre) throws RemoteException;
    String obtenerNombreAlumno(String dni) throws RemoteException;
}


package servidor;

import interfaces.FabricaAcademica;
import interfaces.NotificacionService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorAcademico {
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando el servidor RMI...");

            // 1) Crear (o conectarse) al RMI Registry en el puerto 54355
            Registry registry = LocateRegistry.createRegistry(54355);

            // 2) Instanciar y publicar la fábrica académica
            //    (FabricaAcademicaImpl extiende UnicastRemoteObject,
            //     así que se exporta automáticamente en su constructor)
            FabricaAcademica fabrica = new FabricaAcademicaImpl();
            registry.rebind("FabricaAcademica", fabrica);

            // 3) Instanciar y publicar el servicio de notificaciones
            //    (NotificacionServiceImpl extiende UnicastRemoteObject,
            //     por lo que también se exporta en su constructor)
            NotificacionService notifSvc = new NotificacionServiceImpl();
            registry.rebind("NotificacionService", notifSvc);

            System.out.println("Servidor RMI arrancado en puerto 54355");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


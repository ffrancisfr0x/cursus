package com.cursus.ui;
import com.cursus.dao.EmpresaDAO;
import com.cursus.dao.EntrevistaDAO;
import com.cursus.dao.OfertaDAO;
import com.cursus.db.ConexionBD;
import com.cursus.service.EmpresaService;
import com.cursus.service.EntrevistaService;
import com.cursus.service.OfertaService;

/**
 * Clase de utilidad que crea una sola vez los servicios de la aplicación y los
 * ofrece al resto de controladores de la interfaz.
 */
public final class Servicios {
    private static OfertaService ofertas;
    private static EmpresaService empresas;
    private static EntrevistaService entrevistas;

    /**
     * Constructor privado para impedir que se creen instancias de la clase.
     */
    private Servicios() {
    }

    /**
     * Crea los DAO y los servicios de candidaturas, empresas y entrevistas a
     * partir de la conexión indicada. Debe llamarse una vez al arrancar la
     * aplicación, antes de usar los métodos de acceso.
     *
     * @param conexion acceso a la base de datos que usarán los servicios
     */
    public static void iniciar(ConexionBD conexion) {
        EmpresaDAO empresaDAO = new EmpresaDAO();
        OfertaDAO ofertaDAO = new OfertaDAO();
        EntrevistaDAO entrevistaDAO = new EntrevistaDAO();
        ofertas = new OfertaService(conexion, ofertaDAO, empresaDAO, entrevistaDAO);
        empresas = new EmpresaService(conexion, empresaDAO, ofertaDAO, entrevistaDAO);
        entrevistas = new EntrevistaService(conexion, entrevistaDAO, ofertaDAO);
    }

    /**
     * Obtiene el servicio de candidaturas.
     *
     * @return el servicio de candidaturas
     */
    public static OfertaService ofertas() {
        return ofertas;
    }

    /**
     * Obtiene el servicio de empresas.
     *
     * @return el servicio de empresas
     */
    public static EmpresaService empresas() {
        return empresas;
    }

    /**
     * Obtiene el servicio de entrevistas.
     *
     * @return el servicio de entrevistas
     */
    public static EntrevistaService entrevistas() {
        return entrevistas;
    }
}
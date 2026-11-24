package com.cursus.ui;
import com.cursus.dao.EmpresaDAO;
import com.cursus.dao.EntrevistaDAO;
import com.cursus.dao.OfertaDAO;
import com.cursus.db.ConexionBD;
import com.cursus.service.EmpresaService;
import com.cursus.service.EntrevistaService;
import com.cursus.service.OfertaService;
public final class Servicios {
    private static OfertaService ofertas;
    private static EmpresaService empresas;
    private static EntrevistaService entrevistas;
    private Servicios() {
    }
    public static void iniciar(ConexionBD conexion) {
        EmpresaDAO empresaDAO = new EmpresaDAO();
        OfertaDAO ofertaDAO = new OfertaDAO();
        EntrevistaDAO entrevistaDAO = new EntrevistaDAO();
        ofertas = new OfertaService(conexion, ofertaDAO, empresaDAO, entrevistaDAO);
        empresas = new EmpresaService(conexion, empresaDAO, ofertaDAO, entrevistaDAO);
        entrevistas = new EntrevistaService(conexion, entrevistaDAO, ofertaDAO);
    }
    public static OfertaService ofertas() {
        return ofertas;
    }
    public static EmpresaService empresas() {
        return empresas;
    }
    public static EntrevistaService entrevistas() {
        return entrevistas;
    }
}
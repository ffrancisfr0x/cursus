package com.cursus.service;

import com.cursus.dao.EmpresaDAO;
import com.cursus.dao.EntrevistaDAO;
import com.cursus.dao.OfertaDAO;
import com.cursus.db.ConexionBD;
import com.cursus.model.Empresa;
import com.cursus.model.Estado;
import com.cursus.model.FiltroOferta;
import com.cursus.model.Oferta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de gestionar las candidaturas y aplicar las
 * reglas de negocio relacionadas con las ofertas de empleo.
 */
public class OfertaService {

    private final ConexionBD conexion;
    private final OfertaDAO ofertaDAO;
    private final EmpresaDAO empresaDAO;
    private final EntrevistaDAO entrevistaDAO;

    /**
     * Crea una instancia del servicio con las dependencias necesarias.
     *
     * @param conexion gestor de conexiones a la base de datos
     * @param ofertaDAO acceso a datos de ofertas
     * @param empresaDAO acceso a datos de empresas
     * @param entrevistaDAO acceso a datos de entrevistas
     */
    public OfertaService(
            ConexionBD conexion,
            OfertaDAO ofertaDAO,
            EmpresaDAO empresaDAO,
            EntrevistaDAO entrevistaDAO) {

        this.conexion = conexion;
        this.ofertaDAO = ofertaDAO;
        this.empresaDAO = empresaDAO;
        this.entrevistaDAO = entrevistaDAO;
    }

    /**
     * Busca ofertas aplicando los criterios indicados en el filtro.
     *
     * @param filtro criterios de búsqueda
     * @return lista de ofertas que cumplen el filtro
     * @throws SQLException si se produce un error de acceso a datos
     * @throws IllegalArgumentException si los criterios son incoherentes
     */
    public List<Oferta> buscar(FiltroOferta filtro) throws SQLException {

        if (filtro.getSalarioMinimo() != null
                && filtro.getSalarioMaximo() != null
                && filtro.getSalarioMinimo() > filtro.getSalarioMaximo()) {

            throw new IllegalArgumentException(
                    "El salario mínimo no puede ser mayor que el máximo.");
        }

        if (filtro.getFechaDesde() != null
                && filtro.getFechaHasta() != null
                && filtro.getFechaDesde().isAfter(filtro.getFechaHasta())) {

            throw new IllegalArgumentException(
                    "La fecha «desde» no puede ser posterior a la fecha «hasta».");
        }

        filtro.setTitulo(Validacion.limpiar(filtro.getTitulo()));
        filtro.setEmpresa(Validacion.limpiar(filtro.getEmpresa()));

        try (Connection con = conexion.obtener()) {
            return ofertaDAO.buscar(con, filtro);
        }
    }

    /**
     * Obtiene una oferta a partir de su identificador.
     *
     * @param id identificador de la oferta
     * @return oferta encontrada
     * @throws SQLException si se produce un error de acceso a datos
     * @throws IllegalArgumentException si la oferta no existe
     */
    public Oferta ver(int id) throws SQLException {

        try (Connection con = conexion.obtener()) {

            Oferta oferta = ofertaDAO.buscarPorId(con, id);

            if (oferta == null) {
                throw new IllegalArgumentException(
                        "No existe ninguna candidatura con el ID " + id + ".");
            }

            return oferta;
        }
    }

    /**
     * Inserta o actualiza una oferta después de validar sus datos.
     *
     * @param oferta oferta a guardar
     * @throws SQLException si se produce un error de acceso a datos
     * @throws IllegalArgumentException si la información no es válida
     */
    public void guardar(Oferta oferta) throws SQLException {

        validar(oferta);

        try (Connection con = conexion.obtener()) {

            if (oferta.getId() == 0) {

                ofertaDAO.insertar(con, oferta);

            } else if (ofertaDAO.actualizar(con, oferta) == 0) {

                throw new IllegalArgumentException(
                        "No existe ninguna candidatura con el ID "
                                + oferta.getId() + ".");
            }
        }
    }

    /**
     * Cambia el estado de una candidatura.
     *
     * @param id identificador de la oferta
     * @param estado nuevo estado
     * @throws SQLException si se produce un error de acceso a datos
     * @throws IllegalArgumentException si el estado es nulo o la oferta
     *                                  no existe
     */
    public void cambiarEstado(int id, Estado estado) throws SQLException {

        if (estado == null) {
            throw new IllegalArgumentException("Elige un estado.");
        }

        try (Connection con = conexion.obtener()) {

            if (ofertaDAO.cambiarEstado(con, id, estado) == 0) {
                throw new IllegalArgumentException(
                        "No existe ninguna candidatura con el ID " + id + ".");
            }
        }
    }

    /**
     * Elimina una candidatura y todas las entrevistas asociadas.
     *
     * @param id identificador de la oferta
     * @throws SQLException si se produce un error durante la operación
     * @throws IllegalArgumentException si la oferta no existe
     */
    public void borrar(int id) throws SQLException {

        try (Connection con = conexion.obtener()) {

            con.setAutoCommit(false);

            try {

                entrevistaDAO.borrarPorOferta(con, id);

                if (ofertaDAO.borrar(con, id) == 0) {
                    throw new IllegalArgumentException(
                            "No existe ninguna candidatura con el ID " + id + ".");
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            }
        }
    }

    /**
     * Importa ofertas desde un archivo CSV.
     *
     * @param csv archivo CSV a importar
     * @param errores lista donde se almacenarán los errores detectados
     * @return número de ofertas importadas correctamente
     * @throws IOException si se produce un error al leer el archivo
     * @throws SQLException si se produce un error de acceso a datos
     */
    public int importar(File csv, List<String> errores)
            throws IOException, SQLException {

        List<Oferta> ofertas = leerCsv(csv, errores);

        try (Connection con = conexion.obtener()) {

            con.setAutoCommit(false);

            try {

                for (Oferta oferta : ofertas) {

                    String nombre = oferta.getEmpresa().getNombre();

                    Empresa empresa = empresaDAO.buscarPorNombre(con, nombre);

                    if (empresa == null) {

                        empresa = new Empresa(
                                0,
                                nombre,
                                null,
                                null,
                                LocalDate.now());

                        empresaDAO.insertar(con, empresa);
                    }

                    oferta.setEmpresa(empresa);
                    ofertaDAO.insertar(con, oferta);
                }

                con.commit();
                return ofertas.size();

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            }
        }
    }

    /**
     * Lee un archivo CSV y convierte sus líneas en objetos Oferta.
     *
     * @param csv archivo CSV de origen
     * @param errores colección donde se registran los errores encontrados
     * @return lista de ofertas válidas
     * @throws IOException si se produce un error de lectura
     */
    private List<Oferta> leerCsv(File csv, List<String> errores)
            throws IOException {

        if (!csv.exists()) {
            throw new IllegalArgumentException(
                    "No se encuentra el archivo " + csv.getPath() + ".");
        }

        List<Oferta> ofertas = new ArrayList<>();

        try (BufferedReader lector =
                     new BufferedReader(
                             new FileReader(csv, StandardCharsets.UTF_8))) {

            String linea;
            int numero = 0;

            while ((linea = lector.readLine()) != null) {

                numero++;

                if (linea.isBlank()
                        || linea.toLowerCase().startsWith("id;")) {
                    continue;
                }

                try {

                    ofertas.add(convertirLinea(linea));

                } catch (IllegalArgumentException e) {

                    errores.add(
                            "Línea " + numero + ": " + e.getMessage());
                }
            }
        }

        return ofertas;
    }

    /**
     * Convierte una línea CSV en un objeto Oferta.
     *
     * @param linea línea leída del archivo
     * @return oferta creada a partir de los datos de la línea
     * @throws IllegalArgumentException si la línea contiene datos inválidos
     */
    private Oferta convertirLinea(String linea) {

        String[] partes = linea.split(";", -1);

        if (partes.length < 7) {
            throw new IllegalArgumentException("Faltan campos.");
        }

        Oferta oferta = new Oferta();

        oferta.setTitulo(Validacion.limpiar(partes[1]));
        oferta.setEmpresa(
                new Empresa(
                        0,
                        Validacion.limpiar(partes[2]),
                        null,
                        null,
                        null));

        oferta.setEstado(Estado.desdeTexto(partes[3]));

        try {

            oferta.setFechaPostulacion(
                    LocalDate.parse(
                            partes[4].trim(),
                            Validacion.FORMATO_FECHA));

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "Fecha no válida: "
                            + partes[4].trim()
                            + ". Usa dd/MM/yyyy.");
        }

        oferta.setEnlace(Validacion.limpiar(partes[5]));
        oferta.setNotas(Validacion.limpiar(partes[6]));

        Validacion.obligatorio(
                oferta.getEmpresa().getNombre(),
                "empresa",
                100);

        validarCampos(oferta);

        return oferta;
    }

    /**
     * Valida una oferta antes de almacenarla en la base de datos.
     *
     * @param oferta oferta a validar
     * @throws IllegalArgumentException si algún dato no es válido
     */
    private void validar(Oferta oferta) {

        oferta.setTitulo(Validacion.limpiar(oferta.getTitulo()));
        oferta.setDescripcion(Validacion.limpiar(oferta.getDescripcion()));
        oferta.setEnlace(Validacion.limpiar(oferta.getEnlace()));
        oferta.setNotas(Validacion.limpiar(oferta.getNotas()));

        if (oferta.getEmpresa() == null
                || oferta.getEmpresa().getId() == 0) {

            throw new IllegalArgumentException(
                    "Elige una empresa.");
        }

        validarCampos(oferta);
    }

    /**
     * Valida los campos comunes de una oferta.
     *
     * @param oferta oferta a validar
     * @throws IllegalArgumentException si algún campo contiene valores inválidos
     */
    private void validarCampos(Oferta oferta) {

        Validacion.obligatorio(
                oferta.getTitulo(),
                "título",
                150);

        if (oferta.getEstado() == null) {
            throw new IllegalArgumentException("Elige un estado.");
        }

        if (oferta.getFechaPostulacion() == null) {
            throw new IllegalArgumentException(
                    "La fecha de postulación es obligatoria.");
        }

        if (oferta.getFechaPostulacion().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de postulación no puede ser futura.");
        }

        if (oferta.getSalario() != null
                && oferta.getSalario() < 0) {

            throw new IllegalArgumentException(
                    "El salario no puede ser negativo.");
        }

        Validacion.url(oferta.getEnlace(), "enlace");
    }
}
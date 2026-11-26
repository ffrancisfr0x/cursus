package com.cursus.ui;

import com.cursus.model.Entrevista;
import com.cursus.model.Oferta;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de la pantalla de detalle de una candidatura. Muestra los datos
 * de la oferta y de su empresa, y permite gestionar las entrevistas asociadas.
 */
public class DetalleOfertaController {

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubtitulo;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblSalario;
    @FXML
    private Label lblEnlace;
    @FXML
    private Label lblDescripcion;
    @FXML
    private Label lblNotas;
    @FXML
    private Label lblEmpresa;
    @FXML
    private Label lblSector;
    @FXML
    private Label lblWeb;
    @FXML
    private TitledPane panelEntrevistas;
    @FXML
    private TableView<Entrevista> tablaEntrevistas;
    @FXML
    private TableColumn<Entrevista, LocalDate> colFecha;
    @FXML
    private TableColumn<Entrevista, String> colTipo;
    @FXML
    private TableColumn<Entrevista, String> colResultado;
    @FXML
    private TableColumn<Entrevista, String> colNotas;
    @FXML
    private Button btnEditarEntrevista;
    @FXML
    private Button btnBorrarEntrevista;

    private Oferta oferta;

    /**
     * Inicializa la pantalla: configura las columnas de la tabla de entrevistas
     * y deshabilita los botones de editar y borrar mientras no haya una
     * entrevista seleccionada.
     */
    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colResultado.setCellValueFactory(new PropertyValueFactory<>("resultado"));
        colNotas.setCellValueFactory(new PropertyValueFactory<>("notas"));

        colFecha.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setText(vacia ? null : Campos.fecha(fecha));
            }
        });

        BooleanBinding sinSeleccion = tablaEntrevistas.getSelectionModel().selectedItemProperty().isNull();
        btnEditarEntrevista.disableProperty().bind(sinSeleccion);
        btnBorrarEntrevista.disableProperty().bind(sinSeleccion);
    }

    /**
     * Carga y muestra los datos de una candidatura junto con sus entrevistas.
     * Si la oferta no existe, avisa al usuario y vuelve al listado.
     *
     * @param idOferta identificador de la oferta que se desea mostrar
     */
    public void cargar(int idOferta) {
        try {
            oferta = Servicios.ofertas().ver(idOferta);
            List<Entrevista> entrevistas = Servicios.entrevistas().listarPorOferta(idOferta);
            mostrar(entrevistas);
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
            volver();
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }

    /**
     * Vuelca en la pantalla los datos de la oferta actual, los de su empresa
     * y la lista de entrevistas recibida.
     *
     * @param entrevistas entrevistas asociadas a la oferta
     */
    private void mostrar(List<Entrevista> entrevistas) {
        lblTitulo.setText(oferta.getTitulo());
        lblSubtitulo.setText(oferta.getEmpresa().getNombre() + " · Candidatura #" + oferta.getId());
        lblEstado.setText(oferta.getEstado().toString());
        lblEstado.getStyleClass().setAll("label", "pildora", "estado-" + oferta.getEstado());

        lblFecha.setText(Campos.fecha(oferta.getFechaPostulacion()));
        lblSalario.setText(Campos.salario(oferta.getSalario()));
        lblEnlace.setText(Campos.texto(oferta.getEnlace()));
        lblDescripcion.setText(Campos.texto(oferta.getDescripcion()));
        lblNotas.setText(Campos.texto(oferta.getNotas()));

        lblEmpresa.setText(oferta.getEmpresa().getNombre());
        lblSector.setText(Campos.texto(oferta.getEmpresa().getSector()));
        lblWeb.setText(Campos.texto(oferta.getEmpresa().getWeb()));

        tablaEntrevistas.getItems().setAll(entrevistas);
        panelEntrevistas.setText("Entrevistas (" + entrevistas.size() + ")");
    }

    /**
     * Vuelve a la pantalla del listado de candidaturas.
     */
    @FXML
    private void volver() {
        Navegador.mostrar("ofertas.fxml");
    }

    /**
     * Abre el formulario para editar la candidatura actual y, si se guardan
     * los cambios, recarga los datos de la pantalla.
     */
    @FXML
    private void editar() {
        FormularioOfertaController formulario = Navegador.abrirDialogo("formulario-oferta.fxml",
                "Editar candidatura", (FormularioOfertaController c) -> c.editar(oferta.getId()));

        if (formulario.isGuardado()) {
            cargar(oferta.getId());
        }
    }

    /**
     * Borra la candidatura actual y todas sus entrevistas tras pedir
     * confirmación, y vuelve al listado.
     */
    @FXML
    private void borrar() {
        if (!Avisos.confirmar("¿Borrar esta candidatura y sus entrevistas?", "Borrar")) {
            return;
        }

        try {
            Servicios.ofertas().borrar(oferta.getId());
            volver();
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }

    /**
     * Abre el formulario para añadir una entrevista a la candidatura actual
     * y, si se guarda, recarga los datos de la pantalla.
     */
    @FXML
    private void nuevaEntrevista() {
        FormularioEntrevistaController formulario = Navegador.abrirDialogo("formulario-entrevista.fxml",
                "Nueva entrevista", (FormularioEntrevistaController c) -> c.nueva(oferta.getId()));

        if (formulario.isGuardado()) {
            cargar(oferta.getId());
        }
    }

    /**
     * Abre el formulario para editar la entrevista seleccionada en la tabla
     * y recarga los datos de la pantalla al cerrarlo.
     */
    @FXML
    private void editarEntrevista() {
        Entrevista entrevista = tablaEntrevistas.getSelectionModel().getSelectedItem();

        Navegador.abrirDialogo("formulario-entrevista.fxml", "Editar entrevista",
                (FormularioEntrevistaController c) -> c.editar(entrevista));

        cargar(oferta.getId());
    }

    /**
     * Borra la entrevista seleccionada en la tabla tras pedir confirmación
     * y recarga los datos de la pantalla.
     */
    @FXML
    private void borrarEntrevista() {
        Entrevista entrevista = tablaEntrevistas.getSelectionModel().getSelectedItem();

        if (!Avisos.confirmar("¿Borrar la entrevista del " + Campos.fecha(entrevista.getFecha()) + "?",
                "Borrar")) {
            return;
        }

        try {
            Servicios.entrevistas().borrar(entrevista.getId());
            cargar(oferta.getId());
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }
}
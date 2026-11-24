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
    public void cargar(int idOferta) {
        try {
            oferta = Servicios.ofertas().ver(idOferta);
            List<Entrevista> entrevistas = Servicios.entrevistas().listarPorOferta(idOferta);
            mostrar(entrevistas);
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
            volver();
        } catch (SQLException e) {Avisos.errorBD(e);
        }
    }
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
    @FXML
    private void volver() {
        Navegador.mostrar("ofertas.fxml");
    }
    @FXML
    private void editar() {
        FormularioOfertaController formulario = Navegador.abrirDialogo("formulario-oferta.fxml", "Editar candidatura", (FormularioOfertaController c) -> c.editar(oferta.getId()));
        if (formulario.isGuardado()) {
            cargar(oferta.getId());
        }
    }
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
    @FXML
    private void nuevaEntrevista() {
        FormularioEntrevistaController formulario = Navegador.abrirDialogo("formulario-entrevista.fxml",
                "Nueva entrevista", (FormularioEntrevistaController c) -> c.nueva(oferta.getId()));
        if (formulario.isGuardado()) {
            cargar(oferta.getId());
        }
    }
    @FXML
    private void editarEntrevista() {
        Entrevista entrevista = tablaEntrevistas.getSelectionModel().getSelectedItem();
        Navegador.abrirDialogo("formulario-entrevista.fxml", "Editar entrevista",
                (FormularioEntrevistaController c) -> c.editar(entrevista));
        cargar(oferta.getId());
    }
    @FXML
    private void borrarEntrevista() {
        Entrevista entrevista = tablaEntrevistas.getSelectionModel().getSelectedItem();
        if (!Avisos.confirmar("¿Borrar la entrevista del " + Campos.fecha(entrevista.getFecha()) + "?",
                "Borrar")) {}
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

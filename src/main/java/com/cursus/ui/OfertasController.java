package com.cursus.ui;
import com.cursus.model.Estado;
import com.cursus.model.FiltroOferta;
import com.cursus.model.Oferta;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class OfertasController {
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtEmpresa;
    @FXML
    private ComboBox<Estado> cbEstado;
    @FXML
    private TextField txtSalarioMin;
    @FXML
    private TextField txtSalarioMax;
    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private TableView<Oferta> tabla;
    @FXML
    private TableColumn<Oferta, Integer> colId;
    @FXML
    private TableColumn<Oferta, String> colTitulo;
    @FXML
    private TableColumn<Oferta, String> colEmpresa;
    @FXML
    private TableColumn<Oferta, Estado> colEstado;
    @FXML
    private TableColumn<Oferta, LocalDate> colFecha;
    @FXML
    private TableColumn<Oferta, Double> colSalario;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnVer;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnBorrar;
    @FXML
    private void initialize() {
        cbEstado.getItems().setAll(Estado.values());
        Campos.formatoFecha(dpDesde);
        Campos.formatoFecha(dpHasta);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEmpresa.setCellValueFactory(fila -> new
                SimpleStringProperty(fila.getValue().getEmpresa().getNombre()));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
        colFecha.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setText(vacia ? null : Campos.fecha(fecha));
            }
        });
        colSalario.setCellFactory(columna -> new TableCell<>() {@Override
        protected void updateItem(Double salario, boolean vacia) {
            super.updateItem(salario, vacia);
            setText(vacia ? null : Campos.salario(salario));
        }
        });
// HU-12: el estado se cambia con un desplegable dentro de la tabla, coloreado según el manual.
        colEstado.setCellFactory(columna -> new ComboBoxTableCell<>(Estado.values()) {
            @Override
            public void updateItem(Estado estado, boolean vacia) {
                super.updateItem(estado, vacia);
                getStyleClass().removeAll("estado-pendiente", "estado-entrevista", "estado-rechazado", "estadoaceptado");
                if (!vacia && estado != null) {
                    getStyleClass().add("estado-" + estado);
                }
            }
        });
        colEstado.setOnEditCommit(evento -> cambiarEstado(evento.getRowValue(), evento.getNewValue()));
        BooleanBinding sinSeleccion = tabla.getSelectionModel().selectedItemProperty().isNull();
        btnVer.disableProperty().bind(sinSeleccion);
        btnEditar.disableProperty().bind(sinSeleccion);
        btnBorrar.disableProperty().bind(sinSeleccion);
        buscar();
    }
    @FXML
    private void buscar() {
        try {
            FiltroOferta filtro = new FiltroOferta();
            filtro.setTitulo(txtTitulo.getText());
            filtro.setEmpresa(txtEmpresa.getText());
            filtro.setEstado(cbEstado.getValue());
            filtro.setSalarioMinimo(Campos.leerNumero(txtSalarioMin, "salario mínimo"));
            filtro.setSalarioMaximo(Campos.leerNumero(txtSalarioMax, "salario máximo"));
            filtro.setFechaDesde(dpDesde.getValue());
            filtro.setFechaHasta(dpHasta.getValue());
            List<Oferta> ofertas = Servicios.ofertas().buscar(filtro);
            tabla.getItems().setAll(ofertas);
            lblTotal.setText(ofertas.size() + " candidatura(s)");
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }
    @FXML
    private void limpiar() {
        txtTitulo.clear();
        txtEmpresa.clear();
        cbEstado.setValue(null);
        txtSalarioMin.clear();
        txtSalarioMax.clear();
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        buscar();
    }
    private void cambiarEstado(Oferta oferta, Estado estado) {
        try {
            Servicios.ofertas().cambiarEstado(oferta.getId(), estado);
            oferta.setEstado(estado);
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
        tabla.refresh();
    }
    @FXML
    private void nueva() {
        FormularioOfertaController formulario = Navegador.abrirDialogo("formulario-oferta.fxml", "Nueva candidatura",
        (FormularioOfertaController c) -> c.nueva());
        if (formulario.isGuardado()) {
            buscar();
        }
    }
    @FXML
    private void editar() {
        int id = tabla.getSelectionModel().getSelectedItem().getId();
        FormularioOfertaController formulario = Navegador.abrirDialogo("formulario-oferta.fxml", "Editar candidatura",
        (FormularioOfertaController c) -> c.editar(id));
        if (formulario.isGuardado()) {
            buscar();
        }
    }
    @FXML
    private void verDetalle() {
        int id = tabla.getSelectionModel().getSelectedItem().getId();
        DetalleOfertaController detalle = Navegador.mostrar("detalle-oferta.fxml");
        detalle.cargar(id);
    }
    @FXML
    private void borrar() {
        Oferta oferta = tabla.getSelectionModel().getSelectedItem();
        String pregunta = "¿Borrar la candidatura «" + oferta.getTitulo() + "» de " +
                oferta.getEmpresa().getNombre()
                + "?\nTambién se borrarán sus entrevistas.";
        if (!Avisos.confirmar(pregunta, "Borrar")) {
            return;
        }
        try {
            Servicios.ofertas().borrar(oferta.getId());
            buscar();
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }
    @FXML
    private void importar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Importar candidaturas");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File carpetaDatos = new File("data");
        if (carpetaDatos.isDirectory()) {
            selector.setInitialDirectory(carpetaDatos);
        }
        File csv = selector.showOpenDialog(tabla.getScene().getWindow());
        if (csv == null) {
            return;
        }
        try {
            List<String> errores = new ArrayList<>();
            int importadas = Servicios.ofertas().importar(csv, errores);
            StringBuilder mensaje = new StringBuilder(importadas + " candidatura(s) importada(s).");
            for (String error : errores) {
                mensaje.append("\nNo importada. ").append(error);
            }
            Avisos.info(mensaje.toString());
            buscar();
        } catch (IllegalArgumentException | IOException e) {
            Avisos.error(e.getMessage());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }
}
package com.cursus.ui;
import com.cursus.model.Empresa;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
public class EmpresasController {
    @FXML
    private TableView<Empresa> tabla;
    @FXML
    private TableColumn<Empresa, String> colNombre;
    @FXML
    private TableColumn<Empresa, String> colSector;
    @FXML
    private TableColumn<Empresa, String> colWeb;
    @FXML
    private TableColumn<Empresa, LocalDate> colRegistro;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblFormulario;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cbSector;
    @FXML
    private TextField txtWeb;
    @FXML
    private Label lblMensaje;
    @FXML
    private Button btnBorrar;
    private Empresa actual;
    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSector.setCellValueFactory(new PropertyValueFactory<>("sector"));
        colWeb.setCellValueFactory(new PropertyValueFactory<>("web"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colRegistro.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setText(vacia ? null : Campos.fecha(fecha));
            }
        });
        cbSector.getItems().setAll("Tecnología", "Consultoría", "Banca", "Telecomunicaciones",
                "Administración pública", "Otro");
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                mostrarEnFormulario(seleccionada);
            }
        });
        cargar();
        nueva();
    }private void cargar() {
        try {
            List<Empresa> empresas = Servicios.empresas().listar();
            tabla.getItems().setAll(empresas);
            lblTotal.setText(empresas.size() + " empresa(s)");
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }
    @FXML
    private void nueva() {
        tabla.getSelectionModel().clearSelection();
        actual = new Empresa();
        lblFormulario.setText("Nueva empresa");
        txtNombre.clear();
        cbSector.getEditor().clear();
        cbSector.setValue(null);
        txtWeb.clear();
        btnBorrar.setDisable(true);
        mensaje("", false);
        txtNombre.requestFocus();
    }
    private void mostrarEnFormulario(Empresa empresa) {
        actual = empresa;
        lblFormulario.setText("Editar empresa");
        txtNombre.setText(empresa.getNombre());
        cbSector.setValue(empresa.getSector());
        txtWeb.setText(empresa.getWeb());
        btnBorrar.setDisable(false);
        mensaje("", false);
    }
    @FXML
    private void guardar() {
        if (!Campos.requerido(txtNombre)) {
            mensaje("El nombre es obligatorio.", true);
            return;
        }
        Empresa datos = new Empresa(actual.getId(), txtNombre.getText(), cbSector.getEditor().getText(),
                txtWeb.getText(), actual.getFechaRegistro());
        try {
            Servicios.empresas().guardar(datos);
            cargar();
            nueva();
            mensaje("Empresa «" + datos.getNombre() + "» guardada.", false);
        } catch (IllegalArgumentException e) {
            mensaje(e.getMessage(), true);
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }
    @FXML
    private void borrar() {
        String pregunta = "¿Borrar la empresa «" + actual.getNombre()
                + "»?\nTambién se borrarán todas sus candidaturas y sus entrevistas.";
        if (!Avisos.confirmar(pregunta, "Borrar")) {
            return;
        }
        try {
            int ofertas = Servicios.empresas().borrar(actual.getId());
            cargar();
            nueva();
            mensaje("Empresa borrada junto con " + ofertas + " candidatura(s).", false);
        } catch (IllegalArgumentException e) {
            mensaje(e.getMessage(), true);
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }
    private void mensaje(String texto, boolean error) {lblMensaje.setText(texto);
        lblMensaje.getStyleClass().setAll("label", error ? "texto-error" : "texto-ok");
    }
}

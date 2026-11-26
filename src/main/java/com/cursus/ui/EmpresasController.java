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

/**
 * Controlador de la pantalla de empresas. Muestra el listado en una tabla y
 * gestiona el formulario lateral para crear, editar y borrar empresas.
 */
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

    /**
     * Inicializa la pantalla: enlaza las columnas con las propiedades de la
     * empresa, da formato a la fecha de registro, rellena la lista de sectores,
     * registra el listener de selección de la tabla y carga los datos.
     */
    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSector.setCellValueFactory(new PropertyValueFactory<>("sector"));
        colWeb.setCellValueFactory(new PropertyValueFactory<>("web"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colRegistro.setCellFactory(columna -> new TableCell<>() {
            /**
             * Muestra la fecha con el formato de la aplicación o deja la celda vacía.
             *
             * @param fecha fecha de registro de la fila
             * @param vacia indica si la celda no tiene contenido
             */
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
    }

    /**
     * Obtiene las empresas de la base de datos, las muestra en la tabla y
     * actualiza el contador total. Si falla la consulta, muestra un aviso de error.
     */
    private void cargar() {
        try {
            List<Empresa> empresas = Servicios.empresas().listar();
            tabla.getItems().setAll(empresas);
            lblTotal.setText(empresas.size() + " empresa(s)");
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }

    /**
     * Prepara el formulario para registrar una empresa nueva: quita la selección
     * de la tabla, vacía los campos, desactiva el botón de borrar y coloca el
     * foco en el nombre.
     */
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

    /**
     * Carga los datos de una empresa existente en el formulario para poder
     * editarla y activa el botón de borrar.
     *
     * @param empresa empresa seleccionada en la tabla
     */
    private void mostrarEnFormulario(Empresa empresa) {
        actual = empresa;
        lblFormulario.setText("Editar empresa");
        txtNombre.setText(empresa.getNombre());
        cbSector.setValue(empresa.getSector());
        txtWeb.setText(empresa.getWeb());
        btnBorrar.setDisable(false);
        mensaje("", false);
    }

    /**
     * Valida el formulario y guarda la empresa, ya sea nueva o editada. Si todo
     * va bien, recarga la tabla, limpia el formulario y muestra un mensaje de
     * confirmación; si los datos no son válidos, muestra el motivo, y si falla
     * la base de datos, muestra un aviso de error.
     */
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

    /**
     * Borra la empresa seleccionada tras pedir confirmación. También se borran
     * sus candidaturas y entrevistas. Al terminar, recarga la tabla e informa de
     * cuántas candidaturas se han eliminado.
     */
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

    /**
     * Muestra un mensaje debajo del formulario con el estilo de éxito o de error.
     *
     * @param texto texto que se muestra; una cadena vacía borra el mensaje
     * @param error true para aplicar el estilo de error, false para el de éxito
     */
    private void mensaje(String texto, boolean error) {
        lblMensaje.setText(texto);
        lblMensaje.getStyleClass().setAll("label", error ? "texto-error" : "texto-ok");
    }
}
package com.cursus.ui;
import com.cursus.model.Empresa;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controlador del formulario modal para crear o editar una candidatura.
 * Valida los campos, guarda la oferta y cierra la ventana.
 */
public class FormularioOfertaController {
    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtTitulo;
    @FXML
    private ComboBox<Empresa> cbEmpresa;
    @FXML
    private ComboBox<Estado> cbEstado;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtSalario;
    @FXML
    private TextField txtEnlace;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextArea txtNotas;
    @FXML
    private Label lblError;
    private Oferta oferta;
    private boolean guardado;

    /**
     * Inicializa el formulario: rellena la lista de estados, configura el formato
     * de fecha y carga las empresas disponibles en su selector. Si falla la
     * consulta de empresas, muestra un aviso de error.
     */
    @FXML
    private void initialize() {
        cbEstado.getItems().setAll(Estado.values());
        Campos.formatoFecha(dpFecha);
        try {
            cbEmpresa.getItems().setAll(Servicios.empresas().listar());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }

    /**
     * Prepara el formulario para crear una candidatura nueva, con el estado
     * «pendiente» y la fecha de hoy por defecto.
     */
    public void nueva() {
        oferta = new Oferta();
        lblTitulo.setText("Nueva candidatura");
        cbEstado.setValue(Estado.PENDIENTE);
        dpFecha.setValue(LocalDate.now());
    }

    /**
     * Prepara el formulario para editar una candidatura existente: la obtiene de
     * la base de datos y carga sus datos en los campos. Si no existe o falla la
     * consulta, muestra un aviso de error y deja el formulario sin cargar.
     *
     * @param id identificador de la candidatura que se va a editar
     */
    public void editar(int id) {
        try {
            oferta = Servicios.ofertas().ver(id);
        } catch (IllegalArgumentException e) {
            Avisos.error(e.getMessage());
            return;
        } catch (SQLException e) {
            Avisos.errorBD(e);
            return;
        }
        lblTitulo.setText("Editar candidatura #" + id);
        txtTitulo.setText(oferta.getTitulo());
        for (Empresa empresa : cbEmpresa.getItems()) {
            if (empresa.getId() == oferta.getEmpresa().getId()) {
                cbEmpresa.setValue(empresa);
            }
        }
        cbEstado.setValue(oferta.getEstado());
        dpFecha.setValue(oferta.getFechaPostulacion());
        txtSalario.setText(oferta.getSalario() == null ? "" : String.format("%.0f", oferta.getSalario()));
        txtEnlace.setText(oferta.getEnlace());
        txtDescripcion.setText(oferta.getDescripcion());
        txtNotas.setText(oferta.getNotas());
    }

    /**
     * Valida los campos obligatorios y guarda la candidatura. Si todo es
     * correcto, marca el formulario como guardado y cierra la ventana; si faltan
     * datos o no son válidos, muestra el motivo, y si falla la base de datos,
     * muestra un aviso de error.
     */
    @FXML
    private void guardar() {
        boolean completo = Campos.requerido(txtTitulo) & Campos.requerido(cbEmpresa)
                & Campos.requerido(cbEstado) & Campos.requerido(dpFecha);
        if (!completo) {
            lblError.setText("Rellena los campos marcados en rojo.");
            return;
        }
        try {
            oferta.setTitulo(txtTitulo.getText());
            oferta.setEmpresa(cbEmpresa.getValue());
            oferta.setEstado(cbEstado.getValue());
            oferta.setFechaPostulacion(dpFecha.getValue());
            oferta.setSalario(Campos.leerNumero(txtSalario, "salario"));
            oferta.setEnlace(txtEnlace.getText());
            oferta.setDescripcion(txtDescripcion.getText());
            oferta.setNotas(txtNotas.getText());
            Servicios.ofertas().guardar(oferta);
            guardado = true;
            cerrar();
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        } catch (SQLException e) {
            Avisos.errorBD(e);
        }
    }

    /**
     * Cierra la ventana sin guardar los cambios.
     */
    @FXML
    private void cancelar() {
        cerrar();
    }

    /**
     * Indica si la candidatura se guardó antes de cerrar la ventana.
     *
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Cierra la ventana que contiene el formulario.
     */
    private void cerrar() {
        ((Stage) txtTitulo.getScene().getWindow()).close();
    }
}
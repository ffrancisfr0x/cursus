package com.cursus.ui;
import com.cursus.model.Entrevista;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controlador del formulario modal para crear o editar una entrevista de una
 * candidatura. Valida los campos, guarda la entrevista y cierra la ventana.
 */
public class FormularioEntrevistaController {
    @FXML
    private Label lblTitulo;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private ComboBox<String> cbResultado;
    @FXML
    private TextArea txtNotas;
    @FXML
    private Label lblError;
    private Entrevista entrevista;
    private boolean guardado;

    /**
     * Inicializa el formulario: rellena las listas de tipos y resultados con los
     * valores de {@link Entrevista} y configura el formato de fecha del selector.
     */
    @FXML
    private void initialize() {
        cbTipo.getItems().setAll(Entrevista.TIPOS);
        cbResultado.getItems().setAll(Entrevista.RESULTADOS);
        Campos.formatoFecha(dpFecha);
    }

    /**
     * Prepara el formulario para crear una entrevista nueva asociada a una
     * candidatura, con la fecha de hoy y el resultado «pendiente» por defecto.
     *
     * @param idOferta identificador de la candidatura a la que pertenece la entrevista
     */
    public void nueva(int idOferta) {
        entrevista = new Entrevista();
        entrevista.setIdOferta(idOferta);
        lblTitulo.setText("Nueva entrevista");
        dpFecha.setValue(LocalDate.now());
        cbResultado.setValue("pendiente");
    }

    /**
     * Prepara el formulario para editar una entrevista existente y carga sus
     * datos en los campos.
     *
     * @param existente entrevista que se va a modificar
     */
    public void editar(Entrevista existente) {
        entrevista = existente;
        lblTitulo.setText("Editar entrevista");
        dpFecha.setValue(existente.getFecha());
        cbTipo.setValue(existente.getTipo());
        cbResultado.setValue(existente.getResultado());
        txtNotas.setText(existente.getNotas());
    }

    /**
     * Valida los campos obligatorios y guarda la entrevista. Si todo es correcto,
     * marca el formulario como guardado y cierra la ventana; si faltan datos o
     * no son válidos, muestra el motivo, y si falla la base de datos, muestra un
     * aviso de error.
     */
    @FXML
    private void guardar() {
        boolean completo = Campos.requerido(dpFecha) & Campos.requerido(cbTipo) &
                Campos.requerido(cbResultado);
        if (!completo) {
            lblError.setText("Rellena los campos marcados en rojo.");
            return;
        }
        try {
            entrevista.setFecha(dpFecha.getValue());
            entrevista.setTipo(cbTipo.getValue());
            entrevista.setResultado(cbResultado.getValue());
            entrevista.setNotas(txtNotas.getText());
            Servicios.entrevistas().guardar(entrevista);
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
     * Indica si la entrevista se guardó antes de cerrar la ventana.
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
        ((Stage) dpFecha.getScene().getWindow()).close();
    }
}
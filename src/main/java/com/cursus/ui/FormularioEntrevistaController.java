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
    @FXML
    private void initialize() {
        cbTipo.getItems().setAll(Entrevista.TIPOS);
        cbResultado.getItems().setAll(Entrevista.RESULTADOS);
        Campos.formatoFecha(dpFecha);
    }
    public void nueva(int idOferta) {
        entrevista = new Entrevista();
        entrevista.setIdOferta(idOferta);
        lblTitulo.setText("Nueva entrevista");
        dpFecha.setValue(LocalDate.now());
        cbResultado.setValue("pendiente");
    }public void editar(Entrevista existente) {
        entrevista = existente;
        lblTitulo.setText("Editar entrevista");
        dpFecha.setValue(existente.getFecha());
        cbTipo.setValue(existente.getTipo());
        cbResultado.setValue(existente.getResultado());
        txtNotas.setText(existente.getNotas());
    }
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
    @FXML
    private void cancelar() {
        cerrar();
    }
    public boolean isGuardado() {
        return guardado;
    }
    private void cerrar() {
        ((Stage) dpFecha.getScene().getWindow()).close();
    }
}
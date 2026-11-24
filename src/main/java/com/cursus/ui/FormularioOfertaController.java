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
    public void nueva() {
        oferta = new Oferta();
        lblTitulo.setText("Nueva candidatura");
        cbEstado.setValue(Estado.PENDIENTE);
        dpFecha.setValue(LocalDate.now());
    }
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
    @FXML
    private void cancelar() {
        cerrar();
    }
    public boolean isGuardado() {
        return guardado;
    }
    private void cerrar() {
        ((Stage) txtTitulo.getScene().getWindow()).close();
    }
}

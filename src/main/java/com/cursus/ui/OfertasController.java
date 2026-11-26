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

/**
 * Controlador de la pantalla de candidaturas. Muestra el listado en una tabla,
 * permite filtrarlo, cambiar el estado desde la propia tabla, crear, editar,
 * ver y borrar candidaturas, e importarlas desde un archivo CSV.
 */
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

    /**
     * Inicializa la pantalla: rellena la lista de estados, configura el formato
     * de las fechas, enlaza las columnas con las propiedades de la candidatura,
     * habilita el cambio de estado con doble clic, desactiva los botones
     * mientras no haya una fila seleccionada y carga el listado inicial.
     */
    @FXML
    private void initialize() {
        cbEstado.getItems().setAll(Estado.values());
        Campos.formatoFecha(dpDesde);
        Campos.formatoFecha(dpHasta);

        // IMPORTANTE: sin esto la tabla no deja editar y el desplegable de estado nunca aparece.
        tabla.setEditable(true);
        colEstado.setEditable(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEmpresa.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().getEmpresa().getNombre()));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));

        colFecha.setCellFactory(columna -> new TableCell<>() {
            /**
             * Muestra la fecha con el formato de la aplicación o deja la celda vacía.
             *
             * @param fecha fecha de postulación de la fila
             * @param vacia indica si la celda no tiene contenido
             */
            @Override
            protected void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setText(vacia ? null : Campos.fecha(fecha));
            }
        });

        colSalario.setCellFactory(columna -> new TableCell<>() {
            /**
             * Muestra el salario con el formato de la aplicación o deja la celda vacía.
             *
             * @param salario salario de la fila, que puede ser null
             * @param vacia indica si la celda no tiene contenido
             */
            @Override
            protected void updateItem(Double salario, boolean vacia) {
                super.updateItem(salario, vacia);
                setText(vacia ? null : Campos.salario(salario));
            }
        });

        // HU-12: el estado se cambia con doble clic (aparece un desplegable dentro de la tabla),
        // y la celda se colorea según el estado.
        colEstado.setCellFactory(columna -> {
            ComboBoxTableCell<Oferta, Estado> celda = new ComboBoxTableCell<>(Estado.values()) {
                /**
                 * Muestra el estado y aplica a la celda la clase de estilo
                 * correspondiente para colorearla.
                 *
                 * @param estado estado de la candidatura de la fila
                 * @param vacia indica si la celda no tiene contenido
                 */
                @Override
                public void updateItem(Estado estado, boolean vacia) {
                    super.updateItem(estado, vacia);
                    getStyleClass().removeAll("estado-pendiente", "estado-entrevista",
                            "estado-rechazado", "estado-aceptado");
                    if (!vacia && estado != null) {
                        getStyleClass().add("estado-" + estado.name().toLowerCase());
                    }
                }
            };

            // Doble clic: entra en modo edición y muestra el desplegable.
            celda.setOnMouseClicked(evento -> {
                if (evento.getClickCount() == 2 && !celda.isEmpty() && !celda.isEditing()) {
                    celda.getTableView().edit(celda.getIndex(), colEstado);
                }
            });
            return celda;
        });
        colEstado.setOnEditCommit(evento -> cambiarEstado(evento.getRowValue(), evento.getNewValue()));

        BooleanBinding sinSeleccion = tabla.getSelectionModel().selectedItemProperty().isNull();
        btnVer.disableProperty().bind(sinSeleccion);
        btnEditar.disableProperty().bind(sinSeleccion);
        btnBorrar.disableProperty().bind(sinSeleccion);

        buscar();
    }

    /**
     * Construye un filtro con los valores del formulario de búsqueda, consulta
     * las candidaturas que lo cumplen y las muestra en la tabla junto con el
     * total. Si algún valor no es válido, muestra el motivo, y si falla la base
     * de datos, muestra un aviso de error.
     */
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

    /**
     * Vacía todos los campos de filtrado y vuelve a mostrar el listado completo.
     */
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

    /**
     * Guarda el nuevo estado de una candidatura editada desde la tabla y
     * actualiza la fila. Si el cambio no es válido o falla la base de datos,
     * muestra un aviso y la tabla se refresca con el valor anterior.
     *
     * @param oferta candidatura cuyo estado se ha modificado
     * @param estado nuevo estado elegido en el desplegable
     */
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

    /**
     * Abre el formulario modal para registrar una candidatura nueva y, si se
     * guarda, actualiza el listado.
     */
    @FXML
    private void nueva() {
        FormularioOfertaController formulario = Navegador.abrirDialogo("formulario-oferta.fxml", "Nueva candidatura",
                (FormularioOfertaController c) -> c.nueva());
        if (formulario.isGuardado()) {
            buscar();
        }
    }

    /**
     * Abre el formulario modal para editar la candidatura seleccionada y, si se
     * guardan los cambios, actualiza el listado.
     */
    @FXML
    private void editar() {
        int id = tabla.getSelectionModel().getSelectedItem().getId();
        FormularioOfertaController formulario = Navegador.abrirDialogo("formulario-oferta.fxml", "Editar candidatura",
                (FormularioOfertaController c) -> c.editar(id));
        if (formulario.isGuardado()) {
            buscar();
        }
    }

    /**
     * Sustituye la vista actual por el detalle de la candidatura seleccionada.
     */
    @FXML
    private void verDetalle() {
        int id = tabla.getSelectionModel().getSelectedItem().getId();
        DetalleOfertaController detalle = Navegador.mostrar("detalle-oferta.fxml");
        detalle.cargar(id);
    }

    /**
     * Borra la candidatura seleccionada tras pedir confirmación. También se
     * borran sus entrevistas. Al terminar, actualiza el listado; si falla el
     * borrado, muestra un aviso de error.
     */
    @FXML
    private void borrar() {
        Oferta oferta = tabla.getSelectionModel().getSelectedItem();
        String pregunta = "¿Borrar la candidatura «" + oferta.getTitulo() + "» de "
                + oferta.getEmpresa().getNombre()
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

    /**
     * Permite elegir un archivo CSV e importa las candidaturas que contiene.
     * Al terminar, informa de cuántas se han importado y de los motivos por los
     * que no se ha importado alguna, y actualiza el listado. Si el archivo no se
     * puede leer o no es válido, muestra un aviso de error.
     */
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
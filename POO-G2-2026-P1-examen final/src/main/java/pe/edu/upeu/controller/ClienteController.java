package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.model.Cliente;
import pe.edu.upeu.service.IClienteService;

public class ClienteController {

    @FXML private TableView<Cliente> tblCliente;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colCelular;

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCelular;

    private final IClienteService clienteService;
    private ObservableList<Cliente> lista = FXCollections.observableArrayList();

    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @FXML
    public void initialize() {
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));

        // Solo permite números en el DNI
        txtDni.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtDni.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Solo permite números en el Celular
        txtCelular.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCelular.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        cargarDatos();

        tblCliente.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                txtDni.setText(sel.getDni());
                txtNombre.setText(sel.getNombre());
                txtDireccion.setText(sel.getDireccion());
                txtCelular.setText(sel.getCelular());
            }
        });
    }

    private void cargarDatos() {
        lista.setAll(clienteService.findAll());
        tblCliente.setItems(lista);
        tblCliente.refresh();
    }

    @FXML
    private void guardar() {
        String dni       = txtDni.getText().trim();
        String nombre    = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String celular   = txtCelular.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || direccion.isEmpty() || celular.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios: DNI, Nombre, Dirección y Celular.");
            return;
        }


        if (!dni.matches("\\d{8}")) {
            mostrarAlerta("Error", "El DNI debe tener exactamente 8 dígitos.");
            return;
        }


        if (!celular.matches("\\d{9}")) {
            mostrarAlerta("Error", "El celular debe tener exactamente 9 dígitos.");
            return;
        }

        Cliente c = Cliente.builder()
                .dni(dni)
                .nombre(nombre)
                .direccion(direccion)
                .celular(celular)
                .build();

        if (clienteService.existsById(c.getDni())) {
            clienteService.update(c.getDni(), c);
        } else {
            clienteService.save(c);
        }

        limpiar();
        cargarDatos();
    }

    @FXML
    private void eliminar() {
        Cliente sel = tblCliente.getSelectionModel().getSelectedItem();

        if (sel == null) {
            mostrarAlerta("Error", "Seleccione un cliente de la tabla.");
            return;
        }

        clienteService.delete(sel.getDni());

        limpiar();
        cargarDatos();
    }

    @FXML
    private void limpiar() {
        txtDni.clear();
        txtNombre.clear();
        txtDireccion.clear();
        txtCelular.clear();
        tblCliente.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
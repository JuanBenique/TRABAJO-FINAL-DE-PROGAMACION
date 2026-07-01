package pe.edu.upeu.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pe.edu.upeu.components.comprob.ComprobantePdfService;
import pe.edu.upeu.components.comprob.ComprobanteView;
import pe.edu.upeu.dto.SessionManager;
import pe.edu.upeu.dto.comprobante.Comprobante;
import pe.edu.upeu.dto.comprobante.ItemComprobante;
import pe.edu.upeu.model.Cliente;
import pe.edu.upeu.model.Usuario;
import pe.edu.upeu.model.Venta;
import pe.edu.upeu.model.VentaDetalle;
import pe.edu.upeu.repository.VentaDetalleRepository;
import pe.edu.upeu.service.IClienteService;
import pe.edu.upeu.service.IUsuarioService;
import pe.edu.upeu.service.IVentaService;
import pe.edu.upeu.service.IVentaDetalleService;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import pe.edu.upeu.service.IProductoService;
import pe.edu.upeu.model.Producto;

public class VentaController {

    @FXML private TableColumn<Venta, String> colProducto;
    @FXML private TableColumn<Venta, String> colCantidad;
    @FXML private TableColumn<Venta, String> colPrecioUnitario;
    @FXML private ComboBox<Producto> cmbProducto;//seleccionar a base de opcioned
    @FXML private TextField txtPrecioUnitario;//agrega el precio unitario del producto sijn opciones de camviarlo
    @FXML private TextField txtCantidad;//permite selecionar o poner la cantidad que quieres
    @FXML private TableView<Venta> tblVenta;
    @FXML private TableColumn<Venta, String> colIdVenta;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, LocalDate> colFecha;
    @FXML private TableColumn<Venta, Double> colNetoTotal;
    @FXML private TableColumn<Venta, Double> colIgv;
    @FXML private TableColumn<Venta, Double> colPrecioTotal;

    @FXML private TextField txtIdVenta;
    @FXML private DatePicker dpFechaVenta;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private TextField txtNetoTotal;
    @FXML private TextField txtIgv;

    @FXML private TextField txtPrecioTotal;


    private final IVentaService ventaService;
    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;
    private final IVentaDetalleService ventaDetalleService;
    private final VentaDetalleRepository ventaDetalleRepository = new VentaDetalleRepository();
    private ObservableList<Venta> lista = FXCollections.observableArrayList();
    private final IProductoService productoService;//sasacr los productos del almacen

    public VentaController(IVentaService ventaService,
                           IClienteService clienteService,
                           IUsuarioService usuarioService,
                           IProductoService productoService,
                           IVentaDetalleService ventaDetalleService) {

        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.ventaDetalleService = ventaDetalleService;
    }

    @FXML
    public void initialize() {

        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        colNetoTotal.setCellValueFactory(new PropertyValueFactory<>("netoTotal"));
        colIgv.setCellValueFactory(new PropertyValueFactory<>("igv"));
        colPrecioTotal.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
        colProducto.setCellValueFactory(cellData -> {
            List<VentaDetalle> detalles = ventaDetalleRepository.findByIdVenta(cellData.getValue().getIdVenta());

            if (detalles.isEmpty()) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(detalles.get(0).getIdProducto().getNombre());
        });

        colCantidad.setCellValueFactory(cellData -> {
            List<VentaDetalle> detalles = ventaDetalleRepository.findByIdVenta(cellData.getValue().getIdVenta());

            if (detalles.isEmpty()) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(String.valueOf(detalles.get(0).getCantidad()));
        });

        colPrecioUnitario.setCellValueFactory(cellData -> {
            List<VentaDetalle> detalles = ventaDetalleRepository.findByIdVenta(cellData.getValue().getIdVenta());

            if (detalles.isEmpty()) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(String.valueOf(detalles.get(0).getPrecioUni()));
        });

        // Fecha automática ya no manual coomo antes
        // se cambio dpFechaVenta.setValue(null);
        dpFechaVenta.setValue(LocalDate.now());
        dpFechaVenta.setDisable(true);

        // Mostrar nombre del cliente en la tabla
        colCliente.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDniCliente().getNombre()));

        // Configurar ComboBox de Cliente
        cmbCliente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDni() + " - " + item.getNombre());
            }
        });
        cmbCliente.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDni() + " - " + item.getNombre());
            }
        });
        //los productos del almacen
        cmbProducto.setItems(FXCollections.observableArrayList(productoService.findAll()));

        cmbProducto.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getIdProducto() + " - " + item.getNombre());
            }
        });

        cmbProducto.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getIdProducto() + " - " + item.getNombre());
            }
        });
//para sacr el precio del producto seleccionado
        cmbProducto.valueProperty().addListener((obs, old, prod) -> {
            if (prod != null) {
                txtPrecioUnitario.setText(String.valueOf(prod.getPreUnitario()));
                calcularTotalPorProducto();
            }
        });

        txtCantidad.textProperty().addListener((obs, old, val) -> calcularTotalPorProducto());

        // Calcular IGV y Total automáticamente
        txtNetoTotal.textProperty().addListener((obs, old, val) -> calcularTotales(val));

        cargarDatos();

        // Rellenar formulario al seleccionar tabla
        tblVenta.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                txtIdVenta.setText(sel.getIdVenta());
                dpFechaVenta.setValue(sel.getFechaVenta());
                txtNetoTotal.setText(String.valueOf(sel.getNetoTotal()));
                txtIgv.setText(String.valueOf(sel.getIgv()));
                txtPrecioTotal.setText(String.valueOf(sel.getPrecioTotal()));

                cmbCliente.getItems().stream()
                        .filter(c -> c.getDni().equals(sel.getDniCliente().getDni()))
                        .findFirst()
                        .ifPresent(cmbCliente::setValue);
            }
        });
    }

    private void calcularTotales(String netoStr) {
        try {
            double neto = Double.parseDouble(netoStr);

            // Calcula el IGV solo para mostrarlo
            double igv = neto * 0.18;

            txtIgv.setText(String.format(java.util.Locale.US, "%.2f", igv));

            // El precio total permanece igual al neto
            txtPrecioTotal.setText(String.format(java.util.Locale.US, "%.2f", neto));

        } catch (NumberFormatException ignored) {
            txtIgv.clear();
            txtPrecioTotal.clear();
        }
    }
    private void calcularTotalPorProducto() {
        try {
            Producto producto = cmbProducto.getValue();

            if (producto == null || txtCantidad.getText().isEmpty()) {
                return;
            }

            double precio = producto.getPreUnitario();
            int cantidad = Integer.parseInt(txtCantidad.getText());

            if (cantidad <= 0) {
                txtNetoTotal.clear();
                txtIgv.clear();
                txtPrecioTotal.clear();
                return;
            }

            double total = precio * cantidad;

            txtNetoTotal.setText(String.format(java.util.Locale.US, "%.2f", total));

        } catch (Exception e) {
            txtNetoTotal.clear();
            txtIgv.clear();
            txtPrecioTotal.clear();
        }
    }

    private void cargarDatos() {
        cmbCliente.setItems(FXCollections.observableArrayList(clienteService.findAll()));
        //para cargar los nuevos datos del polo
        cmbProducto.setItems(FXCollections.observableArrayList(productoService.findAll()));
        lista.setAll(ventaService.findAll());
        tblVenta.setItems(lista);
        tblVenta.refresh();
    }

    @FXML
    private void guardar() {
        if (txtIdVenta.getText().isEmpty()
                || cmbCliente.getValue() == null
                || dpFechaVenta.getValue() == null
                || cmbProducto.getValue() == null
                || txtCantidad.getText().isEmpty()) {

            mostrarAlerta("Error", "Complete ID, Cliente, Fecha, Producto y Cantidad.");
            return;
        }

        try {
            int id = Integer.parseInt(txtIdVenta.getText());

            if (id <= 0) {
                mostrarAlerta("Error", "El ID de Venta debe ser mayor que 0.");
                return;
            }

            int cantidad = Integer.parseInt(txtCantidad.getText());

            if (cantidad <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser mayor que 0.");
                return;
            }

            Producto producto = cmbProducto.getValue();
            double precioUnitario = producto.getPreUnitario();
            double netoTotal = precioUnitario * cantidad;
            double igv = netoTotal * 0.18;
            double precioTotal = netoTotal;

            Usuario usuarioResponsable;
            Optional<Usuario> optUsu = usuarioService.loginUsuario("juan123", "juan123456");

            if (optUsu.isPresent()) {
                usuarioResponsable = optUsu.get();
            } else {
                usuarioResponsable = new Usuario();
                usuarioResponsable.setIdUsuario("1");
            }

            Venta venta = Venta.builder()
                    .idVenta(txtIdVenta.getText())
                    .dniCliente(cmbCliente.getValue())
                    .fechaVenta(dpFechaVenta.getValue())
                    .netoTotal(netoTotal)
                    .igv(igv)
                    .precioTotal(precioTotal)
                    .idUsuario(usuarioResponsable)
                    .build();

            if (ventaService.existsById(venta.getIdVenta())) {
                mostrarAlerta("Error", "El ID de Venta ya existe. Ingrese otro ID.");
                return;
            }

            ventaService.save(venta);

            VentaDetalle detalle = VentaDetalle.builder()
                    .idVentaDetalle("VD" + venta.getIdVenta())
                    .idVenta(venta)
                    .idProducto(producto)
                    .precioUni(precioUnitario)
                    .porceUtil(producto.getPorceUtil())
                    .cantidad(cantidad)
                    .netoTotal(netoTotal)
                    .build();

            ventaDetalleService.save(detalle);

            mostrarAlerta("Éxito", "Venta registrada correctamente.");

            limpiar();
            cargarDatos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID y cantidad deben ser números válidos.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar la venta: " + e.getMessage());
        }
    }
    @FXML
    private void eliminar() {
        Venta sel = tblVenta.getSelectionModel().getSelectedItem();

        if (sel == null) {
            mostrarAlerta("Error", "Seleccione una venta.");
            return;
        }

        try {
            List<VentaDetalle> detalles = ventaDetalleRepository.findByIdVenta(sel.getIdVenta());

            for (VentaDetalle d : detalles) {
                ventaDetalleService.delete(d.getIdVentaDetalle());
            }

            ventaService.delete(sel.getIdVenta());

            mostrarAlerta("Éxito", "Venta eliminada correctamente.");

            limpiar();
            cargarDatos();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar la venta: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        txtIdVenta.clear();
        dpFechaVenta.setValue(null);
        cmbCliente.setValue(null);
        txtNetoTotal.clear();
        txtIgv.clear();
        txtPrecioTotal.clear();
        tblVenta.getSelectionModel().clearSelection();
    }

    @FXML
    private void generarBoleta() {
        Venta sel = tblVenta.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Error", "Seleccione una venta para generar la boleta.");
            return;
        }
        try {
            Comprobante comprobante = construirComprobante(sel);
            mostrarVistaPrevia(comprobante);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar la boleta: " + e.getMessage());
        }
    }

    private Comprobante construirComprobante(Venta v) {
        List<VentaDetalle> detalles = ventaDetalleRepository.findByIdVenta(v.getIdVenta());
//hace apareceer los porductos que hay
        List<ItemComprobante> items = detalles.stream()
                .map(d -> ItemComprobante.builder()
                        .cantidad(d.getCantidad())
                        .descripcion(d.getIdProducto().getNombre())
                        .precioUnitario(d.getPrecioUni())
                        .subtotal(d.getNetoTotal())
                        .build())
                .collect(Collectors.toList());

        Cliente cliente = v.getDniCliente();

        return Comprobante.builder()
                .razonSocial("SysVentas Polos E.I.R.L.")
                .rucEmisor("20123456789")
                .direccion("Av. Ejemplo 123")
                .ubigeo("Puno - Puno - Puno")
                .telefono("051-123456")
                .tipoDocumento("BOLETA DE VENTA ELECTRÓNICA")
                .serie("B001")
                .numero(v.getIdVenta())
                .fechaEmision(v.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .moneda("PEN")
                .tipoDocCliente("DNI")
                .nroDocCliente(cliente.getDni())
                .nombreCliente(cliente.getNombre())
                .direccionCliente(cliente.getDireccion())
                .items(items)
                .opGravadas(v.getNetoTotal())
                .igv(v.getIgv())
                .total(v.getPrecioTotal())
                .build();
    }

    private void mostrarVistaPrevia(Comprobante comprobante) {
        ComprobanteView view = new ComprobanteView(comprobante);
        ScrollPane scroll = new ScrollPane(view.construir());
        scroll.setFitToWidth(true);

        Button btnExportar = new Button("📄  Exportar PDF");
        btnExportar.setStyle("-fx-background-color:#1a4a8a; -fx-text-fill:white; -fx-font-weight:bold; " +
                "-fx-padding:8 16 8 16; -fx-background-radius:6; -fx-cursor:hand;");
        btnExportar.setOnAction(e -> exportarPdf(comprobante));

        HBox barra = new HBox(btnExportar);
        barra.setPadding(new Insets(10));
        barra.setStyle("-fx-background-color:#1e2536;");
        barra.setAlignment(Pos.CENTER_RIGHT);

        BorderPane root = new BorderPane();
        root.setTop(barra);
        root.setCenter(scroll);

        Stage stage = new Stage();
        stage.setTitle("Vista previa - Boleta " + comprobante.getNumero());
        stage.setScene(new Scene(root, 800, 650));
        stage.show();
    }

    private void exportarPdf(Comprobante comprobante) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("Boleta_" + comprobante.getNumero() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        try {
            new ComprobantePdfService().exportar(comprobante, file.toPath());
            mostrarAlerta("Éxito", "PDF generado correctamente.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo exportar el PDF: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

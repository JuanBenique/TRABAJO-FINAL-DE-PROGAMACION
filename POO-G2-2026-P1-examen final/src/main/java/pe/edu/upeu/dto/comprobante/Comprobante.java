package pe.edu.upeu.dto.comprobante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comprobante {
    // Datos del emisor (tu empresa)
    private String razonSocial;
    private String rucEmisor;
    private String direccion;
    private String ubigeo;
    private String telefono;

    // Datos del documento
    private String tipoDocumento; // "BOLETA DE VENTA ELECTRÓNICA"
    private String serie;
    private String numero;
    private String fechaEmision;
    private String moneda;

    // Datos del cliente
    private String tipoDocCliente; // "DNI"
    private String nroDocCliente;
    private String nombreCliente;
    private String direccionCliente;

    // Detalle de productos
    private List<ItemComprobante> items;

    // Totales
    private Double opGravadas;
    private Double igv;
    private Double total;
}
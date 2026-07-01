package pe.edu.upeu.dto.comprobante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemComprobante {
    private Integer cantidad;
    private String descripcion;
    private Double precioUnitario;
    private Double subtotal;
}
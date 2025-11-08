package co.edu.unicauca.servicios.dto.servicio;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ServicioRequestDTO {
    private String nombre;
    private String descripcion;
    private double precio;
    private String createBy;
    private String estado;
    private String imagenURL;
    private Long categoriaId;
    private Long subCategoriaId;


}

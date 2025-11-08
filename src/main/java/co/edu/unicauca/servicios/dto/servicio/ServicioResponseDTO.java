package co.edu.unicauca.servicios.dto.servicio;


import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaMiniDTO;
import co.edu.unicauca.servicios.dto.categoria.CategoriaMiniDTO;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ServicioResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String createBy;
    private Date createAt;
    private Date updateAt;
    private String estado;
    private String imagenURL;
    private CategoriaMiniDTO categoria;
    private SubCategoriaMiniDTO subCategoria;
}

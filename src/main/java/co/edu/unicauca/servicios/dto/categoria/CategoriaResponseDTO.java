package co.edu.unicauca.servicios.dto.categoria;


import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaResponseDTO;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String createdBy;
    private Date createDate;
    private List<SubCategoriaResponseDTO> subCategorias;
}

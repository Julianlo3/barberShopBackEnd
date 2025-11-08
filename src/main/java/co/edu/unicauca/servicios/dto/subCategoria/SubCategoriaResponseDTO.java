package co.edu.unicauca.servicios.dto.subCategoria;

import co.edu.unicauca.servicios.dto.categoria.CategoriaMiniDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SubCategoriaResponseDTO {
    private Long id;
    private String nombre;
    private CategoriaMiniDTO categoria;
}

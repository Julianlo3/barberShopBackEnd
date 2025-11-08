package co.edu.unicauca.servicios.dto.subCategoria;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SubCategoriaRequestDTO {
    private String nombre;
    private Long categoriaId;
}

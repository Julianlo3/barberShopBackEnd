package co.edu.unicauca.servicios.dto.categoria;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CategoriaRequestDTO {
    private String nombre;
    private String createdBy;
}

package co.edu.unicauca.servicios.entity;


import lombok.*;

import java.util.Date;

/**
 * @author lopez
 * @date 1/11/2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class servicioEntity {
    private Long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String createBy;
    private Date createAt;
    private Date updateAt;
    private String estado;
    private String imagenURL;
    private categoriaEntity categoria;
    private subCategoriaEntity subCategoria;

}

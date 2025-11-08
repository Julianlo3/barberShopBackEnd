package co.edu.unicauca.servicios.entity;


import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lopez
 * @date 1/11/2025
 */
@Data
@Getter
@Setter
public class categoriaEntity {
    private Long id;
    private String nombre;
    private String createdBy;
    private Date createDate;

    List<subCategoriaEntity> subCategorias;

    public categoriaEntity() {
        subCategorias = new ArrayList<>();
    }

    public categoriaEntity(Long id, String nombre, String createdBy, Date createDate) {
        this.id = id;
        this.nombre = nombre;
        this.createdBy = createdBy;
        this.createDate = createDate;
        subCategorias = new ArrayList<>();
    }
}

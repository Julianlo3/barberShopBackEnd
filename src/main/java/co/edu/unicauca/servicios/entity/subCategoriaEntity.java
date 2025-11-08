package co.edu.unicauca.servicios.entity;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lopez
 * @date 1/11/2025
 */
@Data
@Getter
@Setter
public class subCategoriaEntity {
    private Long id;
    private String nombre;
    private categoriaEntity categoria;

    List<servicioEntity> servicios;

    public subCategoriaEntity() {
        servicios  = new ArrayList<>();
    }

    public subCategoriaEntity(Long id, String nombre, categoriaEntity categoria) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        servicios  = new ArrayList<>();
    }
}

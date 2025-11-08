package co.edu.unicauca.servicios.mapper;

import co.edu.unicauca.servicios.entity.categoriaEntity;
import co.edu.unicauca.servicios.entity.subCategoriaEntity;
import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaMiniDTO;
import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaResponseDTO;

public class SubCategoriaMapper {

    public static subCategoriaEntity toEntity(SubCategoriaRequestDTO dto, categoriaEntity categoria) {
        subCategoriaEntity entity = new subCategoriaEntity();
        entity.setNombre(dto.getNombre());
        entity.setCategoria(categoria);
        return entity;
    }

    public static SubCategoriaResponseDTO toResponse(subCategoriaEntity entity) {
        SubCategoriaResponseDTO dto = new SubCategoriaResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());

        if (entity.getCategoria() != null) {
            dto.setCategoria(CategoriaMapper.toMiniDTO(entity.getCategoria()));
        }

        return dto;
    }

    public static SubCategoriaMiniDTO toMiniDTO(subCategoriaEntity entity) {
        SubCategoriaMiniDTO dto = new SubCategoriaMiniDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        return dto;
    }
}


package co.edu.unicauca.servicios.mapper;

import co.edu.unicauca.servicios.entity.categoriaEntity;
import co.edu.unicauca.servicios.dto.categoria.CategoriaMiniDTO;
import co.edu.unicauca.servicios.dto.categoria.CategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.categoria.CategoriaResponseDTO;

import java.util.stream.Collectors;

public class CategoriaMapper {

    public static categoriaEntity toEntity(CategoriaRequestDTO dto) {
        categoriaEntity entity = new categoriaEntity();
        entity.setNombre(dto.getNombre());
        entity.setCreatedBy(dto.getCreatedBy());
        return entity;
    }

    public static CategoriaResponseDTO toResponse(categoriaEntity entity) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreateDate(entity.getCreateDate());

        if (entity.getSubCategorias() != null) {
            dto.setSubCategorias(
                    entity.getSubCategorias().stream()
                            .map(SubCategoriaMapper::toResponse)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static CategoriaMiniDTO toMiniDTO(categoriaEntity entity) {
        CategoriaMiniDTO dto = new CategoriaMiniDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        return dto;
    }
}

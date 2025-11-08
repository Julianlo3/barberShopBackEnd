package co.edu.unicauca.servicios.mapper;

import co.edu.unicauca.servicios.entity.categoriaEntity;
import co.edu.unicauca.servicios.entity.servicioEntity;
import co.edu.unicauca.servicios.entity.subCategoriaEntity;
import co.edu.unicauca.servicios.dto.servicio.ServicioRequestDTO;
import co.edu.unicauca.servicios.dto.servicio.ServicioResponseDTO;

public class ServicioMapper {

    public static servicioEntity toEntity(ServicioRequestDTO dto, categoriaEntity categoria, subCategoriaEntity subCategoria) {
        servicioEntity entity = new servicioEntity();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setCreateBy(dto.getCreateBy());
        entity.setEstado(dto.getEstado());
        entity.setImagenURL(dto.getImagenURL());
        entity.setCategoria(categoria);
        entity.setSubCategoria(subCategoria);
        entity.setCreateAt(new java.util.Date());
        return entity;
    }

    public static ServicioResponseDTO toResponse(servicioEntity entity) {
        ServicioResponseDTO dto = new ServicioResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPrecio(entity.getPrecio());
        dto.setCreateBy(entity.getCreateBy());
        dto.setCreateAt(entity.getCreateAt());
        dto.setUpdateAt(entity.getUpdateAt());
        dto.setEstado(entity.getEstado());
        dto.setImagenURL(entity.getImagenURL());

        if (entity.getCategoria() != null) {
            dto.setCategoria(CategoriaMapper.toMiniDTO(entity.getCategoria()));
        }

        if (entity.getSubCategoria() != null) {
            dto.setSubCategoria(SubCategoriaMapper.toMiniDTO(entity.getSubCategoria()));
        }

        return dto;
    }
}


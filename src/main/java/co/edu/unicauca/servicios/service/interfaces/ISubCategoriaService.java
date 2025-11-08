package co.edu.unicauca.servicios.service.interfaces;

import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaResponseDTO;

import java.util.List;

public interface ISubCategoriaService {

    SubCategoriaResponseDTO saveSubCategoria(SubCategoriaRequestDTO subCategoria);

    SubCategoriaResponseDTO getSubCategoriaById(Long id);

    List<SubCategoriaResponseDTO> getAllSubCategorias();

    SubCategoriaResponseDTO updateSubCategoria(Long id, SubCategoriaRequestDTO subCategoria);

    boolean deleteSubCategoria(Long id);
}


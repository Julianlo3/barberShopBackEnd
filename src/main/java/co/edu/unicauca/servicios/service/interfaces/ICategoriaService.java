package co.edu.unicauca.servicios.service.interfaces;

import co.edu.unicauca.servicios.dto.categoria.CategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.categoria.CategoriaResponseDTO;

import java.util.List;

public interface ICategoriaService {

    CategoriaResponseDTO saveCategoria(CategoriaRequestDTO categoria);

    CategoriaResponseDTO getCategoriaById(Long id);

    List<CategoriaResponseDTO> getAllCategorias();

    CategoriaResponseDTO updateCategoria(Long id, CategoriaRequestDTO categoria);

    boolean deleteCategoria(Long id);
}


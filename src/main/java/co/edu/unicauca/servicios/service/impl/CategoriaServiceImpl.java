package co.edu.unicauca.servicios.service.impl;

import co.edu.unicauca.servicios.dto.categoria.CategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.categoria.CategoriaResponseDTO;
import co.edu.unicauca.servicios.entity.categoriaEntity;
import co.edu.unicauca.servicios.exception.DatabaseOperationException;
import co.edu.unicauca.servicios.exception.ResourceNotFoundException;
import co.edu.unicauca.servicios.mapper.CategoriaMapper;
import co.edu.unicauca.servicios.repository.categoriaRepositorySQL;
import co.edu.unicauca.servicios.repository.subCategoriaRepositorySQL;
import co.edu.unicauca.servicios.service.interfaces.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements ICategoriaService {

    private static final Logger logger = Logger.getLogger(CategoriaServiceImpl.class.getName());

    private final categoriaRepositorySQL categoriaRepository;
    private final subCategoriaRepositorySQL subCategoriaRepository;

    @Autowired
    public CategoriaServiceImpl(categoriaRepositorySQL categoriaRepository,
                                subCategoriaRepositorySQL subCategoriaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.subCategoriaRepository = subCategoriaRepository;
    }

    /**
     * Registrar una nueva categoría
     */
    @Override
    public CategoriaResponseDTO saveCategoria(CategoriaRequestDTO dto) {
        try {
            categoriaEntity entity = CategoriaMapper.toEntity(dto);

            var result = categoriaRepository.save(entity);
            if (result.isEmpty()) {
                throw new DatabaseOperationException("No se pudo registrar la categoría.");
            }

            return CategoriaMapper.toResponse(result.get());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al registrar la categoría: " + e.getMessage(), e);
            throw new DatabaseOperationException("Error interno al registrar la categoría", e);
        }
    }

    /**
     * Obtener una categoría por ID
     */
    @Override
    public CategoriaResponseDTO getCategoriaById(Long id) {
        var categoriaOpt = categoriaRepository.findById(id);
        if (categoriaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        return CategoriaMapper.toResponse(categoriaOpt.get());
    }

    /**
     * Listar todas las categorías
     */
    @Override
    public List<CategoriaResponseDTO> getAllCategorias() {
        var categoriasOpt = categoriaRepository.findAll();
        if (categoriasOpt.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron categorías registradas.");
        }

        return categoriasOpt.get().stream()
                .map(CategoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar una categoría existente
     */
    @Override
    public CategoriaResponseDTO updateCategoria(Long id, CategoriaRequestDTO dto) {
        var categoriaOpt = categoriaRepository.findById(id);
        if (categoriaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }

        categoriaEntity categoria = categoriaOpt.get();
        categoria.setNombre(dto.getNombre());
        categoria.setCreatedBy(dto.getCreatedBy());

        var actualizada = categoriaRepository.update(id, categoria);
        if (actualizada.isEmpty()) {
            throw new DatabaseOperationException("Error al actualizar la categoría con ID: " + id);
        }

        return CategoriaMapper.toResponse(actualizada.get());
    }

    /**
     * Eliminar una categoría
     */
    @Override
    public boolean deleteCategoria(Long id) {
        var eliminada = categoriaRepository.delete(id);
        if (eliminada.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró la categoría con ID: " + id + " para eliminar.");
        }
        return true;
    }
}

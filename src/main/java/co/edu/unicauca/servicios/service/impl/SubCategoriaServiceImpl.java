package co.edu.unicauca.servicios.service.impl;

import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaResponseDTO;
import co.edu.unicauca.servicios.entity.subCategoriaEntity;
import co.edu.unicauca.servicios.exception.DatabaseOperationException;
import co.edu.unicauca.servicios.exception.ResourceNotFoundException;
import co.edu.unicauca.servicios.mapper.SubCategoriaMapper;
import co.edu.unicauca.servicios.repository.categoriaRepositorySQL;
import co.edu.unicauca.servicios.repository.subCategoriaRepositorySQL;
import co.edu.unicauca.servicios.service.interfaces.ISubCategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SubCategoriaServiceImpl implements ISubCategoriaService {

    private static final Logger logger = Logger.getLogger(SubCategoriaServiceImpl.class.getName());

    private final subCategoriaRepositorySQL subCategoriaRepository;
    private final categoriaRepositorySQL categoriaRepository;

    @Autowired
    public SubCategoriaServiceImpl(subCategoriaRepositorySQL subCategoriaRepository,
                                   categoriaRepositorySQL categoriaRepository) {
        this.subCategoriaRepository = subCategoriaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Registrar una nueva subcategoría
     */
    @Override
    public SubCategoriaResponseDTO saveSubCategoria(SubCategoriaRequestDTO dto) {
        try {
            var categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
            if (categoriaOpt.isEmpty()) {
                throw new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId());
            }

            subCategoriaEntity entity = SubCategoriaMapper.toEntity(dto, categoriaOpt.get());
            var result = subCategoriaRepository.save(entity);

            if (result.isEmpty()) {
                throw new DatabaseOperationException("No se pudo registrar la subcategoría.");
            }

            return SubCategoriaMapper.toResponse(result.get());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al registrar la subcategoría: " + e.getMessage(), e);
            throw new DatabaseOperationException("Error interno al registrar la subcategoría", e);
        }
    }

    /**
     * Obtener una subcategoría por ID
     */
    @Override
    public SubCategoriaResponseDTO getSubCategoriaById(Long id) {
        var subCategoriaOpt = subCategoriaRepository.findById(id);
        if (subCategoriaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Subcategoría no encontrada con ID: " + id);
        }
        return SubCategoriaMapper.toResponse(subCategoriaOpt.get());
    }

    /**
     * Listar todas las subcategorías
     */
    @Override
    public List<SubCategoriaResponseDTO> getAllSubCategorias() {
        var subCategoriasOpt = subCategoriaRepository.findAll();
        if (subCategoriasOpt.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron subcategorías registradas.");
        }

        return subCategoriasOpt.get().stream()
                .map(SubCategoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar una subcategoría
     */
    @Override
    public SubCategoriaResponseDTO updateSubCategoria(Long id, SubCategoriaRequestDTO dto) {
        var subCategoriaOpt = subCategoriaRepository.findById(id);
        if (subCategoriaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Subcategoría no encontrada con ID: " + id);
        }

        var categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
        if (categoriaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId());
        }

        subCategoriaEntity subCategoria = subCategoriaOpt.get();
        subCategoria.setNombre(dto.getNombre());
        subCategoria.setCategoria(categoriaOpt.get());

        var actualizada = subCategoriaRepository.update(id, subCategoria);
        if (actualizada.isEmpty()) {
            throw new DatabaseOperationException("Error al actualizar la subcategoría con ID: " + id);
        }

        return SubCategoriaMapper.toResponse(actualizada.get());
    }

    /**
     * Eliminar una subcategoría
     */
    @Override
    public boolean deleteSubCategoria(Long id) {
        var eliminada = subCategoriaRepository.delete(id);
        if (eliminada.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró la subcategoría con ID: " + id + " para eliminar.");
        }
        return true;
    }
}


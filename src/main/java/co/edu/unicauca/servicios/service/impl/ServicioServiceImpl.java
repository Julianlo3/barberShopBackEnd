package co.edu.unicauca.servicios.service.impl;

import co.edu.unicauca.servicios.entity.servicioEntity;
import co.edu.unicauca.servicios.repository.categoriaRepositorySQL;
import co.edu.unicauca.servicios.repository.servicioRepositorySQL;
import co.edu.unicauca.servicios.repository.subCategoriaRepositorySQL;
import co.edu.unicauca.servicios.dto.servicio.ServicioRequestDTO;
import co.edu.unicauca.servicios.dto.servicio.ServicioResponseDTO;
import co.edu.unicauca.servicios.exception.DatabaseOperationException;
import co.edu.unicauca.servicios.exception.ResourceNotFoundException;
import co.edu.unicauca.servicios.mapper.ServicioMapper;
import co.edu.unicauca.servicios.service.interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.io.File;

@Service
public class ServicioServiceImpl implements IServicioService {

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;
    private static final Logger logger = Logger.getLogger(ServicioServiceImpl.class.getName());

    private final servicioRepositorySQL servicioRepository;
    private final categoriaRepositorySQL categoriaRepository;
    private final subCategoriaRepositorySQL subCategoriaRepository;

    @Autowired
    public ServicioServiceImpl(servicioRepositorySQL servicioRepository,
                               categoriaRepositorySQL categoriaRepository,
                               subCategoriaRepositorySQL subCategoriaRepository) {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.subCategoriaRepository = subCategoriaRepository;
    }

    /**
     * Registrar un nuevo servicio
     */
    @Override
    public ServicioResponseDTO saveServicio(ServicioRequestDTO dto, MultipartFile imagen) throws IOException {
        try {
            var categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
            var subCategoriaOpt = subCategoriaRepository.findById(dto.getSubCategoriaId());

            if (categoriaOpt.isEmpty()) {
                throw new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId());
            }
            if (subCategoriaOpt.isEmpty()) {
                throw new ResourceNotFoundException("Subcategoría no encontrada con ID: " + dto.getSubCategoriaId());
            }

            // Carpeta "uploads" dentro del proyecto (fuera del JAR)
            String uploadPath = System.getProperty("user.dir") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                boolean creada = uploadDir.mkdirs();
                if (!creada) {
                    throw new IOException("No se pudo crear la carpeta de uploads en: " + uploadPath);
                }
            }

            // Nombre único para evitar colisiones
            String fileName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            File destino = new File(uploadDir, fileName);
            imagen.transferTo(destino);

            // URL pública (sirve con WebMvcConfigurer)
            String imagenURL = "/uploads/" + fileName;

            dto.setImagenURL(imagenURL);
            // ------------------------------------------------------------------------------------------

            // Crear la entidad a partir del DTO
            servicioEntity entity = ServicioMapper.toEntity(dto, categoriaOpt.get(), subCategoriaOpt.get());

            var result = servicioRepository.save(entity);
            if (result.isEmpty()) {
                throw new DatabaseOperationException("No se pudo registrar el servicio.");
            }

            return ServicioMapper.toResponse(result.get());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar la imagen: " + e.getMessage(), e);
            throw new DatabaseOperationException("Error al guardar la imagen", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al registrar servicio: " + e.getMessage(), e);
            throw new DatabaseOperationException("Error interno al registrar el servicio", e);
        }
    }


    /**
     * Obtener un servicio por ID
     */
    @Override
    public ServicioResponseDTO getServicioById(Long id) {
        var servicioOpt = servicioRepository.findById(id);
        if (servicioOpt.isEmpty()) {
            throw new ResourceNotFoundException("Servicio no encontrado con ID: " + id);
        }
        return ServicioMapper.toResponse(servicioOpt.get());
    }


    /**
     * Listar todos los servicios
     */
    @Override
    public List<ServicioResponseDTO> getAllServicios() {
        var serviciosOpt = servicioRepository.findAll();
        if (serviciosOpt.isEmpty()) {
            throw new ResourceNotFoundException("No hay servicios registrados en la base de datos.");
        }
        return serviciosOpt.get().stream()
                .map(ServicioMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Listar los servicios por categoria id
     */
    @Override
    public List<ServicioResponseDTO> getServiciosByCategoriaId(Long idCategoria) {
        var result = servicioRepository.findByCategoriaId(idCategoria);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron servicios para la categoría ID: " + idCategoria);
        }
        return result.get().stream()
                .map(ServicioMapper::toResponse)
                .toList();
    }

    /**
     * Listar los servicios sub categoria id
     */
    @Override
    public List<ServicioResponseDTO> getServiciosBySubCategoriaId(Long idSubCategoria) {
        var result = servicioRepository.findBySubCategoriaId(idSubCategoria);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron servicios para la subcategoría ID: " + idSubCategoria);
        }
        return result.get().stream()
                .map(ServicioMapper::toResponse)
                .toList();
    }

    /**
     * Actualizar un servicio existente
     */
    @Override
    public ServicioResponseDTO updateServicio(Long id, ServicioRequestDTO dto, MultipartFile imagen) throws IOException {
        var servicioOpt = servicioRepository.findById(id);
        if (servicioOpt.isEmpty()) {
            throw new ResourceNotFoundException("Servicio no encontrado con ID: " + id);
        }

        var categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
        var subCategoriaOpt = subCategoriaRepository.findById(dto.getSubCategoriaId());

        if (categoriaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId());
        }
        if (subCategoriaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Subcategoría no encontrada con ID: " + dto.getSubCategoriaId());
        }

        servicioEntity servicio = servicioOpt.get();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecio(dto.getPrecio());
        servicio.setCreateBy(dto.getCreateBy());
        servicio.setEstado(dto.getEstado());
        servicio.setImagenURL(
                (imagen != null && !imagen.isEmpty())
                        ? "/uploads/" + imagen.getOriginalFilename()
                        : servicio.getImagenURL()
        );
        servicio.setCategoria(categoriaOpt.get());
        servicio.setSubCategoria(subCategoriaOpt.get());
        servicio.setUpdateAt(new java.util.Date());

        var actualizado = servicioRepository.update(id, servicio);
        if (actualizado.isEmpty()) {
            throw new DatabaseOperationException("Error al actualizar el servicio con ID: " + id);
        }

        return ServicioMapper.toResponse(actualizado.get());
    }

    /**
     * Eliminar un servicio
     */
    @Override
    public boolean deleteServicio(Long id) {
        var eliminado = servicioRepository.delete(id);
        if (eliminado.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró el servicio con ID: " + id + " para eliminar.");
        }
        return true;
    }
}

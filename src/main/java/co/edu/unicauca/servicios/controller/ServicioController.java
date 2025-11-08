package co.edu.unicauca.servicios.controller;

import co.edu.unicauca.servicios.dto.servicio.ServicioRequestDTO;
import co.edu.unicauca.servicios.dto.servicio.ServicioResponseDTO;
import co.edu.unicauca.servicios.exception.ResourceNotFoundException;
import co.edu.unicauca.servicios.service.interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "http://localhost:4200")
public class ServicioController {

    private static final Logger logger = Logger.getLogger(ServicioController.class.getName());

    private final IServicioService servicioService;

    @Autowired
    public ServicioController(IServicioService servicioService) {
        this.servicioService = servicioService;
    }

    /**
     * Crear un nuevo servicio (con imagen opcional)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveServicio(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") double precio,
            @RequestParam("createBy") String createBy,
            @RequestParam("estado") String estado,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam("subCategoriaId") Long subCategoriaId,
            @RequestParam("imagen") MultipartFile imagen) throws IOException {

        try {
            // Construcción del DTO de petición con los valores recibidos
            ServicioRequestDTO dto = new ServicioRequestDTO();
            dto.setNombre(nombre);
            dto.setDescripcion(descripcion);
            dto.setPrecio(precio);
            dto.setCreateBy(createBy);
            dto.setEstado(estado);
            dto.setCategoriaId(categoriaId);
            dto.setSubCategoriaId(subCategoriaId);

            // Guardar servicio usando el Service
            ServicioResponseDTO respuesta = servicioService.saveServicio(dto, imagen);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);

        } catch (IllegalArgumentException e) {
            logger.warning("Datos inválidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (ResourceNotFoundException e) {
            logger.warning("Recurso no encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            logger.warning("Conflicto de integridad: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicto de datos al guardar el servicio.");

        } catch (Exception e) {
            logger.severe("Error interno al guardar servicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al registrar el servicio. Detalle: " + e.getMessage());
        }
    }

    /**
     * Obtener un servicio por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerServicioPorId(@PathVariable Long id) {
        ServicioResponseDTO servicio = servicioService.getServicioById(id);
        return ResponseEntity.ok(servicio);
    }

    /**
     * Listar todos los servicios
     */
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        List<ServicioResponseDTO> servicios = servicioService.getAllServicios();
        return ResponseEntity.ok(servicios);
    }

    /**
     * Obtener servicios por ID de categoría.
     */
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<?> getServiciosByCategoriaId(@PathVariable Long idCategoria) {
        try {
            List<ServicioResponseDTO> servicios = servicioService.getServiciosByCategoriaId(idCategoria);
            return ResponseEntity.ok(servicios);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al consultar servicios por categoría: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al consultar los servicios por categoría.");
        }
    }

    /**
     * Obtener servicios por ID de subcategoría.
     */
    @GetMapping("/subcategoria/{idSubCategoria}")
    public ResponseEntity<?> getServiciosBySubCategoriaId(@PathVariable Long idSubCategoria) {
        try {
            List<ServicioResponseDTO> servicios = servicioService.getServiciosBySubCategoriaId(idSubCategoria);
            return ResponseEntity.ok(servicios);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al consultar servicios por subcategoría: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al consultar los servicios por subcategoría.");
        }
    }

    /**
     * Actualizar un servicio existente (con imagen opcional)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateServicio(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") double precio,
            @RequestParam("createBy") String createBy,
            @RequestParam("estado") String estado,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam("subCategoriaId") Long subCategoriaId,
            @RequestParam("imagen") MultipartFile imagen) throws IOException {

        try {
            // Construcción del DTO de petición con los valores recibidos
            ServicioRequestDTO dto = new ServicioRequestDTO();
            dto.setNombre(nombre);
            dto.setDescripcion(descripcion);
            dto.setPrecio(precio);
            dto.setCreateBy(createBy);
            dto.setEstado(estado);
            dto.setCategoriaId(categoriaId);
            dto.setSubCategoriaId(subCategoriaId);

            // Guardar servicio usando el Service
            ServicioResponseDTO respuesta = servicioService.updateServicio(id, dto, imagen);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);

        } catch (IllegalArgumentException e) {
            logger.warning("Datos inválidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (ResourceNotFoundException e) {
            logger.warning("Recurso no encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            logger.warning("Conflicto de integridad: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicto de datos al guardar el servicio.");

        } catch (Exception e) {
            logger.severe("Error interno al guardar servicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al registrar el servicio. Detalle: " + e.getMessage());
        }
    }

    /**
     * Eliminar un servicio por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarServicio(@PathVariable Long id) {
        boolean eliminado = servicioService.deleteServicio(id);

        if (eliminado) {
            return ResponseEntity.ok("Servicio eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el servicio con ID: " + id);
        }
    }

}


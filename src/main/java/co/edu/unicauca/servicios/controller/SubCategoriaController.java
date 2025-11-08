package co.edu.unicauca.servicios.controller;

import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.subCategoria.SubCategoriaResponseDTO;
import co.edu.unicauca.servicios.service.interfaces.ISubCategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategorias")
public class SubCategoriaController {

    private final ISubCategoriaService subCategoriaService;

    @Autowired
    public SubCategoriaController(ISubCategoriaService subCategoriaService) {
        this.subCategoriaService = subCategoriaService;
    }

    /**
     * Crear una nueva subcategoría
     */
    @PostMapping
    public ResponseEntity<SubCategoriaResponseDTO> crearSubCategoria(@RequestBody SubCategoriaRequestDTO dto) {
        SubCategoriaResponseDTO nuevaSubCategoria = subCategoriaService.saveSubCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSubCategoria);
    }

    /**
     * Obtener una subcategoría por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubCategoriaResponseDTO> obtenerSubCategoriaPorId(@PathVariable Long id) {
        SubCategoriaResponseDTO subCategoria = subCategoriaService.getSubCategoriaById(id);
        return ResponseEntity.ok(subCategoria);
    }

    /**
     * Listar todas las subcategorías
     */
    @GetMapping
    public ResponseEntity<List<SubCategoriaResponseDTO>> listarSubCategorias() {
        List<SubCategoriaResponseDTO> subCategorias = subCategoriaService.getAllSubCategorias();
        return ResponseEntity.ok(subCategorias);
    }

    /**
     * Actualizar una subcategoría existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubCategoriaResponseDTO> actualizarSubCategoria(
            @PathVariable Long id,
            @RequestBody SubCategoriaRequestDTO dto) {

        SubCategoriaResponseDTO subCategoriaActualizada = subCategoriaService.updateSubCategoria(id, dto);
        return ResponseEntity.ok(subCategoriaActualizada);
    }

    /**
     * Eliminar una subcategoría por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarSubCategoria(@PathVariable Long id) {
        boolean eliminada = subCategoriaService.deleteSubCategoria(id);

        if (eliminada) {
            return ResponseEntity.ok("Subcategoría eliminada correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la subcategoría con ID: " + id);
        }
    }
}


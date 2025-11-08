package co.edu.unicauca.servicios.controller;

import co.edu.unicauca.servicios.dto.categoria.CategoriaRequestDTO;
import co.edu.unicauca.servicios.dto.categoria.CategoriaResponseDTO;
import co.edu.unicauca.servicios.service.interfaces.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final ICategoriaService categoriaService;

    @Autowired
    public CategoriaController(ICategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * Crear una nueva categoría
     */
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO nuevaCategoria = categoriaService.saveCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    /**
     * Obtener una categoría por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaService.getCategoriaById(id);
        return ResponseEntity.ok(categoria);
    }

    /**
     * Listar todas las categorías
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Actualizar una categoría existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaRequestDTO dto) {

        CategoriaResponseDTO categoriaActualizada = categoriaService.updateCategoria(id, dto);
        return ResponseEntity.ok(categoriaActualizada);
    }

    /**
     * Eliminar una categoría por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCategoria(@PathVariable Long id) {
        boolean eliminada = categoriaService.deleteCategoria(id);

        if (eliminada) {
            return ResponseEntity.ok("Categoría eliminada correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la categoría con ID: " + id);
        }
    }
}

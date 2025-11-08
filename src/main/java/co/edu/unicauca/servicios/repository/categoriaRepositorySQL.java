package co.edu.unicauca.servicios.repository;


import co.edu.unicauca.servicios.entity.categoriaEntity;
import co.edu.unicauca.servicios.entity.subCategoriaEntity;
import co.edu.unicauca.servicios.config.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lopez
 * @date 1/11/2025
 */
@Repository
public class categoriaRepositorySQL {

    private static final Logger logger = Logger.getLogger(categoriaRepositorySQL.class.getName());
    private final ConexionBD conexionABaseDeDatos;

    public categoriaRepositorySQL(ConexionBD conexionABaseDeDatos) {
        this.conexionABaseDeDatos = conexionABaseDeDatos;
    }

    /**
     * Registrar una nueva categoría en la base de datos
     */
    public Optional<categoriaEntity> save(categoriaEntity objCategoria) {
        logger.info("Registrando categoría en base de datos...");
        Optional<categoriaEntity> objCategoriaAlmacenada = Optional.empty();

        String sql = "INSERT INTO categorias(nombre, create_by) VALUES(?, ?)";

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asignar valores al statement
            stmt.setString(1, objCategoria.getNombre());
            stmt.setString(2, objCategoria.getCreatedBy());

            int resultado = stmt.executeUpdate();

            // Obtener ID generado y devolver la categoría registrada
            if (resultado > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Long idGenerado = rs.getLong(1);
                        objCategoria.setId(idGenerado);
                        logger.info("Categoría registrada con ID: " + idGenerado);

                        // Recuperar el objeto recién insertado
                        objCategoriaAlmacenada = this.findById(idGenerado);
                    } else {
                        logger.warning("No se obtuvo el ID generado para la categoría insertada.");
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar la categoría: " + e.getMessage(), e);
        }

        return objCategoriaAlmacenada;
    }

    /**
     * Buscar una categoría por su ID.
     */
    public Optional<categoriaEntity> findById(Long id) {
        Optional<categoriaEntity> categoriaOpt = Optional.empty();
        String sqlCategoria = "SELECT id, nombre, create_by, create_date FROM categorias WHERE id = ?";
        String sqlSubcategorias = "SELECT id, nombre FROM sub_categorias WHERE categoria_id = ?";

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmtCat = conn.prepareStatement(sqlCategoria)) {

            stmtCat.setLong(1, id);
            try (ResultSet rsCat = stmtCat.executeQuery()) {
                if (rsCat.next()) {
                    // Crear la categoría
                    categoriaEntity categoria = new categoriaEntity();
                    categoria.setId(rsCat.getLong("id"));
                    categoria.setNombre(rsCat.getString("nombre"));
                    categoria.setCreatedBy(rsCat.getString("create_by"));
                    categoria.setCreateDate(rsCat.getDate("create_date"));

                    // Consultar subcategorías asociadas
                    try (PreparedStatement stmtSub = conn.prepareStatement(sqlSubcategorias)) {
                        stmtSub.setLong(1, id);
                        try (ResultSet rsSub = stmtSub.executeQuery()) {

                            List<subCategoriaEntity> subcategorias = new ArrayList<>();
                            while (rsSub.next()) {
                                subCategoriaEntity sub = new subCategoriaEntity();
                                sub.setId(rsSub.getLong("id"));
                                sub.setNombre(rsSub.getString("nombre"));
                                sub.setCategoria(categoria); // relación inversa
                                subcategorias.add(sub);
                            }
                            categoria.setSubCategorias(subcategorias);
                        }
                    }

                    categoriaOpt = Optional.of(categoria);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar categoría con ID " + id + ": " + e.getMessage(), e);
        }

        return categoriaOpt;
    }

    /**
     * Listar todas las categorías junto con sus subcategorías
     */
    public Optional<Collection<categoriaEntity>> findAll() {
        List<categoriaEntity> categorias = new ArrayList<>();
        String sqlCategorias = "SELECT id, nombre, create_by, create_date FROM categorias";
        String sqlSubcategorias = "SELECT id, nombre, categoria_id FROM sub_categorias";

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmtCat = conn.prepareStatement(sqlCategorias);
             PreparedStatement stmtSub = conn.prepareStatement(sqlSubcategorias);
             ResultSet rsCat = stmtCat.executeQuery();
             ResultSet rsSub = stmtSub.executeQuery()) {

            // Cargar categorías
            Map<Long, categoriaEntity> categoriaMap = new HashMap<>();
            while (rsCat.next()) {
                categoriaEntity cat = new categoriaEntity();
                cat.setId(rsCat.getLong("id"));
                cat.setNombre(rsCat.getString("nombre"));
                cat.setCreatedBy(rsCat.getString("create_by"));
                cat.setCreateDate(rsCat.getDate("create_date"));
                cat.setSubCategorias(new ArrayList<>());
                categoriaMap.put(cat.getId(), cat);
            }

            // Cargar subcategorías y asociarlas
            while (rsSub.next()) {
                Long idCat = rsSub.getLong("categoria_id");
                categoriaEntity categoria = categoriaMap.get(idCat);
                if (categoria != null) {
                    subCategoriaEntity sub = new subCategoriaEntity();
                    sub.setId(rsSub.getLong("id"));
                    sub.setNombre(rsSub.getString("nombre"));
                    sub.setCategoria(categoria);
                    categoria.getSubCategorias().add(sub);
                }
            }

            categorias.addAll(categoriaMap.values());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar categorías: " + e.getMessage(), e);
        }

        return categorias.isEmpty() ? Optional.empty() : Optional.of(categorias);
    }


    /**
     * Actualizar una categoría existente
     */
    public Optional<categoriaEntity> update(Long idCategoria, categoriaEntity objCategoria) {
        Optional<categoriaEntity> objCategoriaActualizada = Optional.empty();
        String sql = "UPDATE categorias SET nombre = ?, create_by = ? WHERE id = ?";

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, objCategoria.getNombre());
            stmt.setString(2, objCategoria.getCreatedBy());
            stmt.setLong(3, idCategoria);

            int resultado = stmt.executeUpdate();

            if (resultado == 1) {
                objCategoriaActualizada = this.findById(idCategoria);
                logger.info("Categoría actualizada correctamente con ID: " + idCategoria);
            } else {
                logger.warning("No se encontró la categoría con ID: " + idCategoria + " para actualizar.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar categoría con ID " + idCategoria + ": " + e.getMessage(), e);
        }

        return objCategoriaActualizada;
    }


    /**
     * Eliminar una categoría y sus subcategorías por ID
     */
    public Optional<categoriaEntity> delete(Long idCategoria) {
        Optional<categoriaEntity> categoriaEliminada = this.findById(idCategoria);
        if (categoriaEliminada.isEmpty()) {
            logger.warning("No se encontró la categoría con ID: " + idCategoria + " para eliminar.");
            return Optional.empty();
        }

        String sqlDeleteSubcategorias = "DELETE FROM sub_categorias WHERE categoria_id = ?";
        String sqlDeleteCategoria = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = conexionABaseDeDatos.conectar()) {

            conn.setAutoCommit(false); // Inicia transacción

            try (PreparedStatement stmtSub = conn.prepareStatement(sqlDeleteSubcategorias);
                 PreparedStatement stmtCat = conn.prepareStatement(sqlDeleteCategoria)) {

                // 1. Eliminar subcategorías
                stmtSub.setLong(1, idCategoria);
                stmtSub.executeUpdate();

                // 2. Eliminar la categoría principal
                stmtCat.setLong(1, idCategoria);
                int resultado = stmtCat.executeUpdate();

                if (resultado == 1) {
                    conn.commit();
                    logger.info("Categoría y sus subcategorías eliminadas correctamente con ID: " + idCategoria);
                } else {
                    conn.rollback();
                    logger.warning("No se pudo eliminar la categoría con ID: " + idCategoria);
                    categoriaEliminada = Optional.empty();
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar categoría con ID " + idCategoria + ": " + e.getMessage(), e);
            categoriaEliminada = Optional.empty();
        }

        return categoriaEliminada;
    }


}

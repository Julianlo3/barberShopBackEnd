package co.edu.unicauca.servicios.repository;

import co.edu.unicauca.servicios.entity.categoriaEntity;
import co.edu.unicauca.servicios.entity.servicioEntity;
import co.edu.unicauca.servicios.entity.subCategoriaEntity;
import co.edu.unicauca.servicios.config.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class subCategoriaRepositorySQL {

    private static final Logger logger = Logger.getLogger(subCategoriaRepositorySQL.class.getName());
    private final ConexionBD conexionABaseDeDatos;

    public subCategoriaRepositorySQL(ConexionBD conexionABaseDeDatos) {
        this.conexionABaseDeDatos = conexionABaseDeDatos;
    }

    /**
     * Registrar una nueva subcategoría en la base de datos
     */
    public Optional<subCategoriaEntity> save(subCategoriaEntity objSubCategoria) {
        Optional<subCategoriaEntity> objSubCategoriaAlmacenada = Optional.empty();
        String sql = "INSERT INTO sub_categorias(nombre, categoria_id) VALUES (?, ?)";

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, objSubCategoria.getNombre());
            stmt.setLong(2, objSubCategoria.getCategoria().getId());

            int resultado = stmt.executeUpdate();

            if (resultado == 1) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Long idGenerado = rs.getLong(1);
                        objSubCategoria.setId(idGenerado);
                        objSubCategoriaAlmacenada = this.findById(idGenerado);
                        logger.info("Subcategoría registrada correctamente con ID: " + idGenerado);
                    } else {
                        logger.warning("No se obtuvo el ID generado para la subcategoría insertada.");
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar la subcategoría: " + e.getMessage(), e);
        }

        return objSubCategoriaAlmacenada;
    }

    /**
     * Listar todas las subcategorías con su categoría asociada
     */
    public Optional<Collection<subCategoriaEntity>> findAll() {
        List<subCategoriaEntity> subcategorias = new ArrayList<>();
        String sql = """
        SELECT sc.id AS id_subcategoria, sc.nombre AS subcategoria_nombre,
               c.id AS id_categoria, c.nombre AS categoria_nombre
        FROM sub_categorias sc
        LEFT JOIN categorias c ON sc.categoria_id = c.id
        ORDER BY sc.id
    """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categoriaEntity categoria = new categoriaEntity();
                categoria.setId(rs.getLong("id_categoria"));
                categoria.setNombre(rs.getString("categoria_nombre"));

                subCategoriaEntity subCategoria = new subCategoriaEntity();
                subCategoria.setId(rs.getLong("id_subcategoria"));
                subCategoria.setNombre(rs.getString("subcategoria_nombre"));
                subCategoria.setCategoria(categoria);

                subcategorias.add(subCategoria);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar subcategorías: " + e.getMessage(), e);
        }

        return subcategorias.isEmpty() ? Optional.empty() : Optional.of(subcategorias);
    }

    /**
     * Buscar una subcategoría por ID, incluyendo su categoría asociada
     */
    public Optional<subCategoriaEntity> findById(Long idSubCategoria) {
        Optional<subCategoriaEntity> subCategoriaOpt = Optional.empty();
        String sql = """
        SELECT 
            sc.id AS id_subcategoria, sc.nombre AS subcategoria_nombre,
            c.id AS id_categoria, c.nombre AS categoria_nombre
        FROM sub_categorias sc
        LEFT JOIN categorias c ON sc.categoria_id = c.id
        WHERE sc.id = ?
    """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idSubCategoria);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    categoriaEntity categoria = new categoriaEntity();
                    categoria.setId(rs.getLong("id_categoria"));
                    categoria.setNombre(rs.getString("categoria_nombre"));

                    subCategoriaEntity subCategoria = new subCategoriaEntity();
                    subCategoria.setId(rs.getLong("id_subcategoria"));
                    subCategoria.setNombre(rs.getString("subcategoria_nombre"));
                    subCategoria.setCategoria(categoria);

                    subCategoriaOpt = Optional.of(subCategoria);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar la subcategoría con ID " + idSubCategoria + ": " + e.getMessage(), e);
        }

        return subCategoriaOpt;
    }

    /**
     * Actualizar una subcategoría existente
     */
    public Optional<subCategoriaEntity> update(Long idSubCategoria, subCategoriaEntity objSubCategoria) {
        Optional<subCategoriaEntity> subCategoriaActualizada = Optional.empty();
        String sql = "UPDATE sub_categorias SET nombre = ?, categoria_id = ? WHERE id = ?";

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, objSubCategoria.getNombre());
            stmt.setLong(2, objSubCategoria.getCategoria().getId());
            stmt.setLong(3, idSubCategoria);

            int resultado = stmt.executeUpdate();

            if (resultado == 1) {
                subCategoriaActualizada = this.findById(idSubCategoria);
                logger.info("Subcategoría actualizada correctamente con ID: " + idSubCategoria);
            } else {
                logger.warning("No se encontró la subcategoría con ID: " + idSubCategoria + " para actualizar.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar la subcategoría con ID " + idSubCategoria + ": " + e.getMessage(), e);
        }

        return subCategoriaActualizada;
    }


    /**
     * Eliminar una subcategoría por ID
     */
    public Optional<subCategoriaEntity> delete(Long idSubCategoria) {
        Optional<subCategoriaEntity> subCategoriaEliminada = this.findById(idSubCategoria);

        if (subCategoriaEliminada.isEmpty()) {
            logger.warning("No se encontró una sub categoria con ID: " + idSubCategoria + " para eliminar.");
            return Optional.empty();
        }

        String sql = "DELETE FROM sub_categorias WHERE id = ?";

        try (Connection conn = conexionABaseDeDatos.conectar()) {

            conn.setAutoCommit(false); // Inicia transacción

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, idSubCategoria);
                int resultado = stmt.executeUpdate();

                if (resultado == 1) {
                    conn.commit();
                    logger.info("Subcategoría eliminada correctamente con ID: " + idSubCategoria);
                } else {
                    conn.rollback();
                    logger.warning("No se pudo eliminar la subcategoría con ID: " + idSubCategoria);
                    subCategoriaEliminada = Optional.empty();
                }
            } catch (SQLException e) {
            conn.rollback();
            throw e;
        }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar la subcategoría con ID " + idSubCategoria + ": " + e.getMessage(), e);
            subCategoriaEliminada = Optional.empty();
        }

        return subCategoriaEliminada;
    }

}

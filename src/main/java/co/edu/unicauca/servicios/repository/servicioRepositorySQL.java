package co.edu.unicauca.servicios.repository;

import co.edu.unicauca.servicios.entity.servicioEntity;
import co.edu.unicauca.servicios.entity.categoriaEntity;
import co.edu.unicauca.servicios.entity.subCategoriaEntity;
import co.edu.unicauca.servicios.config.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class servicioRepositorySQL {

    private static final Logger logger = Logger.getLogger(servicioRepositorySQL.class.getName());
    private final ConexionBD conexionABaseDeDatos;

    public servicioRepositorySQL(ConexionBD conexionABaseDeDatos) {
        this.conexionABaseDeDatos = conexionABaseDeDatos;
    }

    /**
     * Registrar un nuevo servicio en la base de datos
     */
    public Optional<servicioEntity> save(servicioEntity objServicio) {
        Optional<servicioEntity> objServicioAlmacenado = Optional.empty();
        String sql = """
            INSERT INTO servicios(nombre, descripcion, precio, create_by, create_at, estado, imagen_url, id_cat, id_sub_cat)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, objServicio.getNombre());
            stmt.setString(2, objServicio.getDescripcion());
            stmt.setDouble(3, objServicio.getPrecio());
            stmt.setString(4, objServicio.getCreateBy());
            stmt.setTimestamp(5, new java.sql.Timestamp(objServicio.getCreateAt().getTime()));
            stmt.setString(6, objServicio.getEstado());
            stmt.setString(7, objServicio.getImagenURL());
            stmt.setLong(8, objServicio.getCategoria().getId());
            stmt.setLong(9, objServicio.getSubCategoria().getId());

            int resultado = stmt.executeUpdate();

            if (resultado == 1) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Long idGenerado = rs.getLong(1);
                        objServicioAlmacenado = this.findById(idGenerado);
                        logger.info("Servicio registrado correctamente con ID: " + idGenerado);
                    } else {
                        logger.warning("No se obtuvo el ID generado para el servicio insertado.");
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar el servicio: " + e.getMessage(), e);
        }

        return objServicioAlmacenado;
    }


    /**
     * Listar todos los servicios con su categoría y subcategoría
     */
    public Optional<Collection<servicioEntity>> findAll() {
        List<servicioEntity> servicios = new ArrayList<>();
        String sql = """
            SELECT 
                s.id, s.nombre, s.descripcion, s.precio, s.create_by, s.create_at, s.update_at, 
                s.estado, s.imagen_url, 
                c.id AS id_categoria, c.nombre AS categoria_nombre, 
                sc.id AS id_subcategoria, sc.nombre AS subcategoria_nombre
            FROM servicios s
            LEFT JOIN categorias c ON s.id_cat = c.id
            LEFT JOIN sub_categorias sc ON s.id_sub_cat = sc.id
            ORDER BY s.id
        """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                servicioEntity servicio = new servicioEntity();
                servicio.setId(rs.getLong("id"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setPrecio(rs.getDouble("precio"));
                servicio.setCreateBy(rs.getString("create_by"));
                servicio.setCreateAt(rs.getTimestamp("create_at"));
                servicio.setUpdateAt(rs.getTimestamp("update_at"));
                servicio.setEstado(rs.getString("estado"));
                servicio.setImagenURL(rs.getString("imagen_url"));

                categoriaEntity categoria = new categoriaEntity();
                categoria.setId(rs.getLong("id_categoria"));
                categoria.setNombre(rs.getString("categoria_nombre"));
                servicio.setCategoria(categoria);

                subCategoriaEntity subCategoria = new subCategoriaEntity();
                subCategoria.setId(rs.getLong("id_subcategoria"));
                subCategoria.setNombre(rs.getString("subcategoria_nombre"));
                subCategoria.setCategoria(categoria);
                servicio.setSubCategoria(subCategoria);

                servicios.add(servicio);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar servicios: " + e.getMessage(), e);
        }

        return servicios.isEmpty() ? Optional.empty() : Optional.of(servicios);
    }


    /**
     * Buscar un servicio por ID, incluyendo su categoría y subcategoría
     */
    public Optional<servicioEntity> findById(Long idServicio) {
        Optional<servicioEntity> servicioOpt = Optional.empty();
        String sql = """
            SELECT 
                s.id, s.nombre, s.descripcion, s.precio, s.create_by, s.create_at, s.update_at, 
                s.estado, s.imagen_url, 
                c.id AS id_categoria, c.nombre AS categoria_nombre, 
                sc.id AS id_subcategoria, sc.nombre AS subcategoria_nombre
            FROM servicios s
            LEFT JOIN categorias c ON s.id_cat = c.id
            LEFT JOIN sub_categorias sc ON s.id_sub_cat = sc.id
            WHERE s.id = ?
        """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idServicio);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    servicioEntity servicio = new servicioEntity();
                    servicio.setId(rs.getLong("id"));
                    servicio.setNombre(rs.getString("nombre"));
                    servicio.setDescripcion(rs.getString("descripcion"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicio.setCreateBy(rs.getString("create_by"));
                    servicio.setCreateAt(rs.getTimestamp("create_at"));
                    servicio.setUpdateAt(rs.getTimestamp("update_at"));
                    servicio.setEstado(rs.getString("estado"));
                    servicio.setImagenURL(rs.getString("imagen_url"));

                    categoriaEntity categoria = new categoriaEntity();
                    categoria.setId(rs.getLong("id_categoria"));
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    servicio.setCategoria(categoria);

                    subCategoriaEntity subCategoria = new subCategoriaEntity();
                    subCategoria.setId(rs.getLong("id_subcategoria"));
                    subCategoria.setNombre(rs.getString("subcategoria_nombre"));
                    subCategoria.setCategoria(categoria);
                    servicio.setSubCategoria(subCategoria);

                    servicioOpt = Optional.of(servicio);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar el servicio con ID " + idServicio + ": " + e.getMessage(), e);
        }

        return servicioOpt;
    }

    /**
     * Buscar todos los servicios por ID de categoría,
     * incluyendo la información de su categoría y subcategoría.
     */
    public Optional<Collection<servicioEntity>> findByCategoriaId(Long idCategoria) {
        Collection<servicioEntity> servicios = new LinkedList<>();
        String sql = """
        SELECT 
            s.id, s.nombre, s.descripcion, s.precio, s.create_by, s.create_at, s.update_at, 
            s.estado, s.imagen_url, 
            c.id AS id_categoria, c.nombre AS categoria_nombre, 
            sc.id AS id_subcategoria, sc.nombre AS subcategoria_nombre
        FROM servicios s
        LEFT JOIN categorias c ON s.id_cat = c.id
        LEFT JOIN sub_categorias sc ON s.id_sub_cat = sc.id
        WHERE s.id_cat = ?
    """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCategoria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    servicioEntity servicio = new servicioEntity();
                    servicio.setId(rs.getLong("id"));
                    servicio.setNombre(rs.getString("nombre"));
                    servicio.setDescripcion(rs.getString("descripcion"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicio.setCreateBy(rs.getString("create_by"));
                    servicio.setCreateAt(rs.getTimestamp("create_at"));
                    servicio.setUpdateAt(rs.getTimestamp("update_at"));
                    servicio.setEstado(rs.getString("estado"));
                    servicio.setImagenURL(rs.getString("imagen_url"));

                    categoriaEntity categoria = new categoriaEntity();
                    categoria.setId(rs.getLong("id_categoria"));
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    servicio.setCategoria(categoria);

                    subCategoriaEntity subCategoria = new subCategoriaEntity();
                    subCategoria.setId(rs.getLong("id_subcategoria"));
                    subCategoria.setNombre(rs.getString("subcategoria_nombre"));
                    subCategoria.setCategoria(categoria);
                    servicio.setSubCategoria(subCategoria);

                    servicios.add(servicio);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar servicios por categoría con ID " + idCategoria + ": " + e.getMessage(), e);
        }

        return servicios.isEmpty() ? Optional.empty() : Optional.of(servicios);
    }

    /**
     * Buscar todos los servicios por ID de subcategoría,
     * incluyendo la información de su categoría y subcategoría.
     */
    public Optional<Collection<servicioEntity>> findBySubCategoriaId(Long idSubCategoria) {
        Collection<servicioEntity> servicios = new LinkedList<>();
        String sql = """
        SELECT 
            s.id, s.nombre, s.descripcion, s.precio, s.create_by, s.create_at, s.update_at, 
            s.estado, s.imagen_url, 
            c.id AS id_categoria, c.nombre AS categoria_nombre, 
            sc.id AS id_subcategoria, sc.nombre AS subcategoria_nombre
        FROM servicios s
        LEFT JOIN categorias c ON s.id_cat = c.id
        LEFT JOIN sub_categorias sc ON s.id_sub_cat = sc.id
        WHERE s.id_sub_cat = ?
    """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idSubCategoria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    servicioEntity servicio = new servicioEntity();
                    servicio.setId(rs.getLong("id"));
                    servicio.setNombre(rs.getString("nombre"));
                    servicio.setDescripcion(rs.getString("descripcion"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicio.setCreateBy(rs.getString("create_by"));
                    servicio.setCreateAt(rs.getTimestamp("create_at"));
                    servicio.setUpdateAt(rs.getTimestamp("update_at"));
                    servicio.setEstado(rs.getString("estado"));
                    servicio.setImagenURL(rs.getString("imagen_url"));

                    categoriaEntity categoria = new categoriaEntity();
                    categoria.setId(rs.getLong("id_categoria"));
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    servicio.setCategoria(categoria);

                    subCategoriaEntity subCategoria = new subCategoriaEntity();
                    subCategoria.setId(rs.getLong("id_subcategoria"));
                    subCategoria.setNombre(rs.getString("subcategoria_nombre"));
                    subCategoria.setCategoria(categoria);
                    servicio.setSubCategoria(subCategoria);

                    servicios.add(servicio);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar servicios por subcategoría con ID " + idSubCategoria + ": " + e.getMessage(), e);
        }

        return servicios.isEmpty() ? Optional.empty() : Optional.of(servicios);
    }

    /**
     * Actualizar un servicio existente
     */
    public Optional<servicioEntity> update(Long idServicio, servicioEntity objServicio) {
        Optional<servicioEntity> servicioActualizado = Optional.empty();
        String sql = """
            UPDATE servicios
            SET nombre = ?, descripcion = ?, precio = ?, create_by = ?, update_at = ?, 
                estado = ?, imagen_url = ?, id_cat = ?, id_sub_cat = ?
            WHERE id = ?
        """;

        try (Connection conn = conexionABaseDeDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, objServicio.getNombre());
            stmt.setString(2, objServicio.getDescripcion());
            stmt.setDouble(3, objServicio.getPrecio());
            stmt.setString(4, objServicio.getCreateBy());
            stmt.setTimestamp(5, new java.sql.Timestamp(objServicio.getUpdateAt().getTime()));
            stmt.setString(6, objServicio.getEstado());
            stmt.setString(7, objServicio.getImagenURL());
            stmt.setLong(8, objServicio.getCategoria().getId());
            stmt.setLong(9, objServicio.getSubCategoria().getId());
            stmt.setLong(10, idServicio);

            int resultado = stmt.executeUpdate();

            if (resultado == 1) {
                servicioActualizado = this.findById(idServicio);
                logger.info("Servicio actualizado correctamente con ID: " + idServicio);
            } else {
                logger.warning("No se encontró el servicio con ID: " + idServicio + " para actualizar.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar el servicio con ID " + idServicio + ": " + e.getMessage(), e);
        }

        return servicioActualizado;
    }


    /**
     * Eliminar un servicio por ID
     */
    public Optional<servicioEntity> delete(Long idServicio) {
        Optional<servicioEntity> servicioEliminado = this.findById(idServicio);

        if (servicioEliminado.isEmpty()) {
            logger.warning("No se encontró el servicio con ID: " + idServicio + " para eliminar.");
            return Optional.empty();
        }

        String sql = "DELETE FROM servicios WHERE id = ?";

        try (Connection conn = conexionABaseDeDatos.conectar()) {

            conn.setAutoCommit(false); // Inicia transacción

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, idServicio);
                int resultado = stmt.executeUpdate();

                if (resultado == 1) {
                    conn.commit();
                    logger.info("Servicio eliminado correctamente con ID: " + idServicio);
                } else {
                    conn.rollback();
                    logger.warning("No se pudo eliminar el servicio con ID: " + idServicio);
                    servicioEliminado = Optional.empty();
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar el servicio con ID " + idServicio + ": " + e.getMessage(), e);
            servicioEliminado = Optional.empty();
        }

        return servicioEliminado;
    }


}

package co.edu.unicauca.servicios.service.interfaces;

import java.io.IOException;
import java.util.List;

import co.edu.unicauca.servicios.dto.servicio.ServicioRequestDTO;
import co.edu.unicauca.servicios.dto.servicio.ServicioResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IServicioService {

    ServicioResponseDTO saveServicio(ServicioRequestDTO servicio, MultipartFile imagen) throws IOException;

    ServicioResponseDTO getServicioById(Long id);

    List<ServicioResponseDTO> getAllServicios();

    ServicioResponseDTO updateServicio(Long id, ServicioRequestDTO servicio, MultipartFile imagen) throws IOException;

    boolean deleteServicio(Long id);

    List<ServicioResponseDTO> getServiciosByCategoriaId(Long idCategoria);

    List<ServicioResponseDTO> getServiciosBySubCategoriaId(Long idSubCategoria);
}


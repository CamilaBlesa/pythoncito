package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.models.BloqueCodigo;
import ar.edu.unsam.programacion3.pythoncito.repositories.BloqueCodigoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de la gestion individual de bloques de codigo para el panel de administracion.
 */
@Service
public class BloqueCodigoService {

    private final BloqueCodigoRepository bloqueCodigoRepository;

    public BloqueCodigoService(BloqueCodigoRepository bloqueCodigoRepository) {
        this.bloqueCodigoRepository = bloqueCodigoRepository;
    }

    /**
     * Obtiene el detalle administrativo de un bloque de codigo por su identificador.
     * @param id identificador unico del bloque
     * @return DTO con los datos del bloque incluyendo su orden correcto
     */
    public BloqueCodigoAdminDTO obtenerPorId(Long id) {
        BloqueCodigo bloque = bloqueCodigoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Bloque de codigo", id));
        return new BloqueCodigoAdminDTO(
                bloque.getId(),
                bloque.getContenido(),
                bloque.getOrdenPresentacion(),
                bloque.getOrdenCorrecto()
        );
    }

    /**
     * Modifica el contenido y los ordenes de un bloque de codigo existente.
     * @param id identificador del bloque a modificar
     * @param dto nuevos datos del bloque
     * @return DTO con los datos del bloque actualizado
     */
    @Transactional
    public BloqueCodigoAdminDTO modificar(Long id, BloqueCodigoAdminDTO dto) {
        BloqueCodigo bloque = bloqueCodigoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Bloque de codigo", id));
        bloque.setContenido(dto.getContenido());
        bloque.setOrdenPresentacion(dto.getOrdenPresentacion());
        bloque.setOrdenCorrecto(dto.getOrdenCorrecto());
        BloqueCodigo guardado = bloqueCodigoRepository.save(bloque);
        return new BloqueCodigoAdminDTO(
                guardado.getId(),
                guardado.getContenido(),
                guardado.getOrdenPresentacion(),
                guardado.getOrdenCorrecto()
        );
    }
}

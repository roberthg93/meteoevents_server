package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.UsuariDTO;
import ioc.dam.meteoevents.entity.Usuari;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interfície que defineix el mapeig entre les entitats {@link Usuari} i els seus corresponents objectes de transferència de dades {@link UsuariDTO}.
 * Aquesta interfície utilitza la llibreria MapStruct per generar automàticament les implementacions de mapeig.
 *
 * @author Generat amb IA (ChatGPT). Prompt utilitzat: "Documenta aquesta classe en Javadoc".
 * @prompt "Mapper utilitzant Mapstruct per mapejar les classes Entity i DTO"
 */

@Mapper
public interface UsuariMapper {
    /**
     * Instància de {@link UsuariMapper} generada per MapStruct.
     */
    UsuariMapper INSTANCE = Mappers.getMapper(UsuariMapper.class);

    /**
     * Converteix una entitat {@link Usuari} a un objecte de transferència de dades {@link UsuariDTO}.
     *
     * @param usuari l'entitat Usuari que s'ha de convertir.
     * @return l'objecte {@link UsuariDTO} corresponent.
     */
    UsuariDTO toDTO(Usuari usuari);

    /**
     * Converteix un objecte de transferència de dades {@link UsuariDTO} a una entitat {@link Usuari}.
     *
     * @param usuariDTO l'objecte UsuariDTO que s'ha de convertir.
     * @return l'entitat {@link Usuari} corresponent.
     */
    Usuari toEntity(UsuariDTO usuariDTO);
}

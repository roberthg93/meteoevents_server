package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.MunicipiDTO;
import ioc.dam.meteoevents.entity.Municipi;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interfície que defineix el mapeig entre les entitats {@link Municipi} i els seus corresponents objectes de transferència de dades {@link MunicipiDTO}.
 * Aquesta interfície utilitza la llibreria MapStruct per generar automàticament les implementacions de mapeig.
 *
 * @autor Generat amb IA (ChatGPT). Prompt utilitzat: "Mapper utilitzant Mapstruct per mapejar les classes Entity i DTO"
 */
@Mapper
public interface MunicipiMapper {

    /**
     * Instància de {@link MunicipiMapper} generada per MapStruct.
     */
    MunicipiMapper INSTANCE = Mappers.getMapper(MunicipiMapper.class);

    /**
     * Converteix una entitat {@link Municipi} a un objecte de transferència de dades {@link MunicipiDTO}.
     *
     * @param municipi l'entitat Municipi que s'ha de convertir.
     * @return l'objecte {@link MunicipiDTO} corresponent.
     */
    MunicipiDTO toDTO(Municipi municipi);

    /**
     * Converteix un objecte de transferència de dades {@link MunicipiDTO} a una entitat {@link Municipi}.
     *
     * @param municipiDTO l'objecte MunicipiDTO que s'ha de convertir.
     * @return l'entitat {@link Municipi} corresponent.
     */
    Municipi toEntity(MunicipiDTO municipiDTO);
}

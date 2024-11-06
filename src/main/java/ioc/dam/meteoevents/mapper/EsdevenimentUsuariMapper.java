package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.EsdevenimentUsuariDTO;
import ioc.dam.meteoevents.entity.EsdevenimentUsuari;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interfície que defineix el mapeig entre les entitats {@link EsdevenimentUsuari} i els seus corresponents objectes de transferència de dades {@link EsdevenimentUsuariDTO}.
 * Aquesta interfície utilitza la llibreria MapStruct per generar automàticament les implementacions de mapeig.
 *
 * @autor Generat amb IA (ChatGPT). Prompt utilitzat: "Mapper utilitzant Mapstruct per mapejar les classes Entity i DTO"
 */
@Mapper
public interface EsdevenimentUsuariMapper {

    /**
     * Instància de {@link EsdevenimentUsuariMapper} generada per MapStruct.
     */
    EsdevenimentUsuariMapper INSTANCE = Mappers.getMapper(EsdevenimentUsuariMapper.class);

    /**
     * Converteix una entitat {@link EsdevenimentUsuari} a un objecte de transferència de dades {@link EsdevenimentUsuariDTO}.
     *
     * @param esdevenimentUsuari l'entitat EsdevenimentUsuari que s'ha de convertir.
     * @return l'objecte {@link EsdevenimentUsuariDTO} corresponent.
     */
    EsdevenimentUsuariDTO toDTO(EsdevenimentUsuari esdevenimentUsuari);

    /**
     * Converteix un objecte de transferència de dades {@link EsdevenimentUsuariDTO} a una entitat {@link EsdevenimentUsuari}.
     *
     * @param esdevenimentUsuariDTO l'objecte EsdevenimentUsuariDTO que s'ha de convertir.
     * @return l'entitat {@link EsdevenimentUsuari} corresponent.
     */
    EsdevenimentUsuari toEntity(EsdevenimentUsuariDTO esdevenimentUsuariDTO);
}

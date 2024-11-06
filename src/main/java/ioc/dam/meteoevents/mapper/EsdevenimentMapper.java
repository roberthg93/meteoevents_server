package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.EsdevenimentDTO;
import ioc.dam.meteoevents.entity.Esdeveniment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interfície que defineix el mapeig entre les entitats {@link Esdeveniment} i els seus corresponents objectes de transferència de dades {@link EsdevenimentDTO}.
 * Aquesta interfície utilitza la llibreria MapStruct per generar automàticament les implementacions de mapeig.
 *
 * @autor Generat amb IA (ChatGPT). Prompt utilitzat: "Mapper utilitzant Mapstruct per mapejar les classes Entity i DTO"
 */
@Mapper
public interface EsdevenimentMapper {

    /**
     * Instància de {@link EsdevenimentMapper} generada per MapStruct.
     */
    EsdevenimentMapper INSTANCE = Mappers.getMapper(EsdevenimentMapper.class);

    /**
     * Converteix una entitat {@link Esdeveniment} a un objecte de transferència de dades {@link EsdevenimentDTO}.
     *
     * @param esdeveniment l'entitat Esdeveniment que s'ha de convertir.
     * @return l'objecte {@link EsdevenimentDTO} corresponent.
     */
    EsdevenimentDTO toDTO(Esdeveniment esdeveniment);

    /**
     * Converteix un objecte de transferència de dades {@link EsdevenimentDTO} a una entitat {@link Esdeveniment}.
     *
     * @param esdevenimentDTO l'objecte EsdevenimentDTO que s'ha de convertir.
     * @return l'entitat {@link Esdeveniment} corresponent.
     */
    Esdeveniment toEntity(EsdevenimentDTO esdevenimentDTO);
}

package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.MesuraDTO;
import ioc.dam.meteoevents.entity.Mesura;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interfície que defineix el mapeig entre les entitats {@link Mesura} i els seus corresponents objectes de transferència de dades {@link MesuraDTO}.
 * Aquesta interfície utilitza la llibreria MapStruct per generar automàticament les implementacions de mapeig.
 *
 * @autor Generat amb IA (ChatGPT). Prompt utilitzat: "Mapper utilitzant Mapstruct per mapejar les classes Entity i DTO"
 */
@Mapper
public interface MesuraMapper {

    /**
     * Instància de {@link MesuraMapper} generada per MapStruct.
     */
    MesuraMapper INSTANCE = Mappers.getMapper(MesuraMapper.class);

    /**
     * Converteix una entitat {@link Mesura} a un objecte de transferència de dades {@link MesuraDTO}.
     *
     * @param mesura l'entitat Mesura que s'ha de convertir.
     * @return l'objecte {@link MesuraDTO} corresponent.
     */
    MesuraDTO toDTO(Mesura mesura);

    /**
     * Converteix un objecte de transferència de dades {@link MesuraDTO} a una entitat {@link Mesura}.
     *
     * @param mesuraDTO l'objecte MesuraDTO que s'ha de convertir.
     * @return l'entitat {@link Mesura} corresponent.
     */
    Mesura toEntity(MesuraDTO mesuraDTO);
}

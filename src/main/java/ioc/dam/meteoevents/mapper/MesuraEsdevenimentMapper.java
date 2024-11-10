package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.MesuraEsdevenimentDTO;
import ioc.dam.meteoevents.entity.MesuraEsdeveniment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interfície que defineix el mapeig entre les entitats {@link MesuraEsdeveniment} i els seus corresponents objectes de transferència de dades {@link MesuraEsdevenimentDTO}.
 * Aquesta interfície utilitza la llibreria MapStruct per generar automàticament les implementacions de mapeig.
 *
 * @autor Generat amb IA (ChatGPT). Prompt utilitzat: "Mapper utilitzant Mapstruct per mapejar les classes Entity i DTO"
 */
@Mapper
public interface MesuraEsdevenimentMapper {

    /**
     * Instància de {@link MesuraEsdevenimentMapper} generada per MapStruct.
     */
    MesuraEsdevenimentMapper INSTANCE = Mappers.getMapper(MesuraEsdevenimentMapper.class);

    /**
     * Converteix una entitat {@link MesuraEsdeveniment} a un objecte de transferència de dades {@link MesuraEsdevenimentDTO}.
     *
     * @param mesuraEsdeveniment l'entitat MesuraEsdeveniment que s'ha de convertir.
     * @return l'objecte {@link MesuraEsdevenimentDTO} corresponent.
     */
    MesuraEsdevenimentDTO toDTO(MesuraEsdeveniment mesuraEsdeveniment);

    /**
     * Converteix un objecte de transferència de dades {@link MesuraEsdevenimentDTO} a una entitat {@link MesuraEsdeveniment}.
     *
     * @param mesuraEsdevenimentDTO l'objecte MesuraEsdevenimentDTO que s'ha de convertir.
     * @return l'entitat {@link MesuraEsdeveniment} corresponent.
     */
    MesuraEsdeveniment toEntity(MesuraEsdevenimentDTO mesuraEsdevenimentDTO);
}

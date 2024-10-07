package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.UsuariDTO;
import ioc.dam.meteoevents.entity.Usuari;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsuariMapper {
    UsuariMapper INSTANCE = Mappers.getMapper(UsuariMapper.class);

    UsuariDTO toDTO(Usuari usuari);
    Usuari toEntity(UsuariDTO ususariDTO);
}

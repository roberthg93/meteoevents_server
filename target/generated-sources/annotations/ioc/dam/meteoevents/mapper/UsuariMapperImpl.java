package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.UsuariDTO;
import ioc.dam.meteoevents.entity.Usuari;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-03T21:35:31+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
public class UsuariMapperImpl implements UsuariMapper {

    @Override
    public UsuariDTO toDTO(Usuari usuari) {
        if ( usuari == null ) {
            return null;
        }

        UsuariDTO usuariDTO = new UsuariDTO();

        usuariDTO.setId( usuari.getId() );
        usuariDTO.setNomUsuari( usuari.getNomUsuari() );
        usuariDTO.setNom_c( usuari.getNom_c() );

        return usuariDTO;
    }

    @Override
    public Usuari toEntity(UsuariDTO usuariDTO) {
        if ( usuariDTO == null ) {
            return null;
        }

        Usuari usuari = new Usuari();

        usuari.setId( usuariDTO.getId() );
        usuari.setNom_c( usuariDTO.getNom_c() );
        usuari.setNomUsuari( usuariDTO.getNomUsuari() );

        return usuari;
    }
}

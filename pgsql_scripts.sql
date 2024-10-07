-- Funcional
DROP TABLE IF EXISTS funcional;
CREATE TABLE IF NOT EXISTS funcional
(
    id character varying(30) COLLATE pg_catalog."default" NOT NULL,
    nom character varying(100) COLLATE pg_catalog."default" NOT NULL
);

ALTER TABLE IF EXISTS funcional ADD CONSTRAINT funcional_pk PRIMARY KEY (id);

COMMENT ON TABLE funcional
    IS 'Rols funcionals';

INSERT INTO funcional (id, nom) VALUES ('USR', 'Usuari');
INSERT INTO funcional (id, nom) VALUES ('ADM', 'Administrador');


-- Usuari
DROP TABLE IF EXISTS usuaris;
DROP SEQUENCE IF EXISTS usuaris_seq;
CREATE SEQUENCE usuaris_seq START 1;
CREATE TABLE usuaris (
	id INTEGER NOT NULL DEFAULT nextval('usuaris_seq'),
	nom_c CHARACTER VARYING,
	funcional_id CHARACTER VARYING (3),
	nom_usuari CHARACTER VARYING (40),
	contrasenya CHARACTER VARYING (20),
	ultima_connexio TIMESTAMP	
);

ALTER TABLE IF EXISTS usuaris ADD CONSTRAINT usuari_pk PRIMARY KEY (id);
	
ALTER TABLE IF EXISTS usuaris
    ADD CONSTRAINT usuari_fk_funcional FOREIGN KEY (funcional_id)
    REFERENCES funcional (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

INSERT INTO usuaris (nom_c, funcional_id, nom_usuari, contrasenya) VALUES ('Administrador', 'ADM', 'admin', 'admin24');
INSERT INTO usuaris (nom_c, funcional_id, nom_usuari, contrasenya) VALUES ('Convidat', 'USR', 'convidat', 'convidat24');
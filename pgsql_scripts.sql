ALTER TABLE IF EXISTS public.usuaris DROP CONSTRAINT IF EXISTS usuari_fk_funcional;
ALTER TABLE IF EXISTS public.esdeveniments_usuaris DROP CONSTRAINT IF EXISTS esdeveniments_usuaris_fk_usr;
ALTER TABLE IF EXISTS public.esdeveniments_usuaris DROP CONSTRAINT IF EXISTS esdeveniments_usuaris_fk_esd;
ALTER TABLE IF EXISTS public.mesures_esdeveniments DROP CONSTRAINT IF EXISTS mesures_esdeveniments_fk_esd;
ALTER TABLE IF EXISTS public.mesures_esdeveniments DROP CONSTRAINT IF EXISTS mesures_esdeveniments_fk_mes;

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
	ultima_connexio TIMESTAMP,
	data_naixement DATE,
	sexe CHARACTER VARYING,
	poblacio CHARACTER VARYING,
	email CHARACTER VARYING,
	telefon INTEGER,
	descripcio TEXT
);

ALTER TABLE IF EXISTS usuaris ADD CONSTRAINT usuari_pk PRIMARY KEY (id);

ALTER TABLE IF EXISTS usuaris
    ADD CONSTRAINT usuari_fk_funcional FOREIGN KEY (funcional_id)
    REFERENCES funcional (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


INSERT INTO usuaris (nom_c, funcional_id, nom_usuari, contrasenya) VALUES ('Administrador', 'ADM', 'admin', 'admin24');
INSERT INTO usuaris (nom_c, funcional_id, nom_usuari, contrasenya) VALUES ('Convidat', 'USR', 'convidat', 'convidat24');
INSERT INTO usuaris (nom_c, funcional_id, nom_usuari, contrasenya, data_naixement, sexe, poblacio, email, telefon, descripcio) VALUES ('Joan Vidal Garcia', 'USR', 'jvidal', 'jvidal3', '15/12/1980', 'masculí', 'Malgrat de Mar', 'jvidal@gmail.com', 644589884, 'Dill. a Div. 7:00h a 15:00h');
INSERT INTO usuaris (nom_c, funcional_id, nom_usuari, contrasenya, data_naixement, sexe, poblacio, email, telefon, descripcio) VALUES ('Mònica Vinyals Dalmau', 'USR', 'mvinyals', 'masdlfj', '28/2/1995', 'femení', 'Figueres', 'mvd95@gmail.com', 654878333, 'Contracte 3 mesos');


-- Esdeveniments
DROP TABLE IF EXISTS esdeveniments;
DROP SEQUENCE IF EXISTS esdeveniments_seq;
CREATE SEQUENCE esdeveniments_seq START 1;
CREATE TABLE esdeveniments (
	id INTEGER NOT NULL DEFAULT nextval('esdeveniments_seq'),
	nom CHARACTER VARYING,
	descripcio CHARACTER VARYING,
	organitzador CHARACTER VARYING,
	direccio CHARACTER VARYING,
	codi_postal CHARACTER VARYING,
	poblacio CHARACTER VARYING,
	aforament CHARACTER VARYING,
	horari CHARACTER VARYING
);

ALTER TABLE IF EXISTS esdeveniments ADD CONSTRAINT esdeveniments_pk PRIMARY KEY (id);

INSERT INTO esdeveniments (nom, descripcio, organitzador, direccio, codi_postal, poblacio, horari) VALUES ('IV The Traka', 'Cursa de ciclisme gravel a la provincia de Girona', 'Klassmark', 'Carrer Can Pau Birol, 35', '17005', 'Girona', '8:00 a 14:00h');
INSERT INTO esdeveniments (nom, descripcio, organitzador, direccio, codi_postal, poblacio, horari) VALUES ('Festival Cruïlla', 'Festival de música organitzat per Cruïlla Barcelona celebrat anualment el mes de juliol al Parc del Fòrum de Barcelona', 'Cruïlla Barcelona', 'Carrer de la Pau, 12 (Parc del Fòrum)', '08930', 'Sant Adrià de Besòs', '8:00 a 14:00h');


-- Esdeveniments usuaris
DROP TABLE IF EXISTS esdeveniments_usuaris;
CREATE TABLE esdeveniments_usuaris (
	id_esdeveniment INTEGER NOT NULL,
	id_usuari INTEGER NOT NULL
);

ALTER TABLE IF EXISTS esdeveniments_usuaris ADD CONSTRAINT esdeveniments_usuaris_pk PRIMARY KEY (id_esdeveniment, id_usuari);

ALTER TABLE IF EXISTS esdeveniments_usuaris
    ADD CONSTRAINT esdeveniments_usuaris_fk_esd FOREIGN KEY (id_esdeveniment)
    REFERENCES esdeveniments (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE IF EXISTS esdeveniments_usuaris
    ADD CONSTRAINT esdeveniments_usuaris_fk_usr FOREIGN KEY (id_usuari)
    REFERENCES usuaris (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

INSERT INTO esdeveniments_usuaris (id_esdeveniment, id_usuari) VALUES (1, 1);
INSERT INTO esdeveniments_usuaris (id_esdeveniment, id_usuari) VALUES (1, 3);
INSERT INTO esdeveniments_usuaris (id_esdeveniment, id_usuari) VALUES (1, 4);
INSERT INTO esdeveniments_usuaris (id_esdeveniment, id_usuari) VALUES (2, 1);



-- Mesures prevenció
DROP TABLE IF EXISTS mesures;
DROP SEQUENCE IF EXISTS mesures_seq;
CREATE SEQUENCE mesures_seq START 1;
CREATE TABLE mesures (
	id INTEGER NOT NULL DEFAULT nextval('mesures_seq'),
	condicio CHARACTER VARYING,
	valor NUMERIC,
	valor_um CHARACTER VARYING,
	accio TEXT
);

ALTER TABLE IF EXISTS mesures ADD CONSTRAINT mesures_pk PRIMARY KEY (id);

INSERT INTO mesures (condicio, valor, valor_um, accio) VALUES ('vent', 30, 'km/h', 'desmontar pancartes, fixar escenari');
INSERT INTO mesures (condicio, valor, valor_um, accio) VALUES ('vent', 40, 'km/h', 'cancelar esdeveniment');
INSERT INTO mesures (condicio, valor, valor_um, accio) VALUES ('precipitacio', 10, 'mm/h', 'informació i comunicació als usuaris, revisió desguassos i embornals');
INSERT INTO mesures (condicio, valor, valor_um, accio) VALUES ('precipitacio', 30, 'mm/h', 'avaluació de zones vulnerables, seguiment de rius i rieres, desviar trànsit si s''escau');
INSERT INTO mesures (condicio, valor, valor_um, accio) VALUES ('precipitacio', 60, 'mm/h', 'activació protocols de protecció civil, evacuació de zones inundables, assegurar estructures a l''aire lliure');


-- Mesures prevenció esdeveniments
DROP TABLE IF EXISTS mesures_esdeveniments;
CREATE TABLE mesures_esdeveniments (
	id_esdeveniment INTEGER NOT NULL,
	id_mesura INTEGER NOT NULL
);

ALTER TABLE IF EXISTS mesures_esdeveniments ADD CONSTRAINT mesures_esdeveniments_pk PRIMARY KEY (id_esdeveniment, id_mesura);

ALTER TABLE IF EXISTS mesures_esdeveniments
    ADD CONSTRAINT mesures_esdeveniments_fk_esd FOREIGN KEY (id_esdeveniment)
    REFERENCES esdeveniments (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE IF EXISTS mesures_esdeveniments
    ADD CONSTRAINT mesures_esdeveniments_fk_mes FOREIGN KEY (id_mesura)
    REFERENCES mesures (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

INSERT INTO mesures_esdeveniments (id_esdeveniment, id_mesura) VALUES (1, 1);
INSERT INTO mesures_esdeveniments (id_esdeveniment, id_mesura) VALUES (1, 2);
INSERT INTO mesures_esdeveniments (id_esdeveniment, id_mesura) VALUES (1, 3);
INSERT INTO mesures_esdeveniments (id_esdeveniment, id_mesura) VALUES (1, 4);
INSERT INTO mesures_esdeveniments (id_esdeveniment, id_mesura) VALUES (1, 5);
INSERT INTO mesures_esdeveniments (id_esdeveniment, id_mesura) VALUES (2, 1);
INSERT INTO mesures_esdeveniments (id_esdeveniment, id_mesura) VALUES (2, 2);
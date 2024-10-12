# meteoevents_server
Projecte final de DAM - Meteo Events (backend)

## Inicialització servidor
Aquesta aplicació servidor està pensada per ser distribuïda i executada des de Docker.
Una vegada descarregat el projecte en local. Seguir els següent passos:

0. Instal·lar a l'equip Docker i Docker Compose si no estan instal·lats
   - Docker Desktop: Ens el podem descarregar aquí: https://www.docker.com/products/docker-desktop
   - Docker Compose: Instal·lar-lo quan instal·lem Docker Desktop, ja ve integrat i en principi no hem de fer res.
1. Aixecar els contenidors:
   - Anem a Inicio i 
   - Des del Terminal, anem a la ruta on ens haguem descarregat el projecte "meteoevents_server" en el nostre equip.
   - Construïm i aixequem els contenidors amb la següent comanda:
   `docker-compose up --build`

   - Hem de poder veure els nostres contenidors (BD i aplicació servidor) corrents:
![image](https://github.com/user-attachments/assets/e2384e3f-2682-4dd6-ac4b-4088cb70bca1)

   - Si heu descarregat una nova versió del codi "meteoevents_server" i ja teniu creats aquests contenidors en el Docker Desktop del vostre equip heu d'eliminar-los i tornar-los a construir:
   ```
   docker-compose down
   docker-compose up --build

**Nota: Si volem accedir a la BD des de pgAdmin**

La base de dades “meteoevents” del contenidor “postgres-container” que acabem de crear es troba en el localhost del nostre equip, però si des del pgAdmin fem una connexió en el servidor localhost no ens trobarà la base de dades que corre en el docker. Entrarà en conflicte i només trobarà les bases de dades ubicades en el localhost del nostre equip. Perquè ens apareguin les bases de dades de Docker. Hem d’anar a “Servicios” del nostre sistema Windows i aturar el servei de PostgreSQL que està corrents en el nostre equip (en el meu cas “postgresql-x64-14”).
![image](https://github.com/user-attachments/assets/4d5766f5-7d60-4175-bee8-bf472c444af7)

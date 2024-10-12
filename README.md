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
   `bash
   `docker-compose up --build`

   - Hem de poder veure els nostres contenidors (BD i aplicació servidor) corrents:
![image](https://github.com/user-attachments/assets/e2384e3f-2682-4dd6-ac4b-4088cb70bca1)

# Requisiti
1. JDK 17
2. Maven 3.8+
3. MySQL 8.0 o Docker


## Istruzioni per avviare MySQL usando Docker

### Nota: I comandi vanno eseguiti nella cartella root del progetto

```bash
# Bisogna attendere l'inizializzazione del container
docker compose up

# Una volta che si e' avviato il container MySQL
mvn spring-boot:run

```

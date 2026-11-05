
Start docker images
    docker compose --env-file .dev.env up -d        // docker compose down : Σταματάει + σβήνει containers & networks
    
If we want to change the migration sql code 
For Flyway not to complain we have to :

    docker compose down -v // -v == --volumes and reamoves all volumes 
                            // down erase all containers
    docker compose --env-file .dev.env up -d  // stands for detach and starts containers in the background without showing the logs in terminal
- Connect to pgadmin
  http://localhost:5050 ή http://127.0.0.1:5050

--------------------- Prometheus config queries for graphs-------------

All modules request/sec in one graph
sum by (module) (
label_replace(
rate(http_server_requests_seconds_count{uri=~"/api/.*"}[1m]),
"module", "$1", "uri", "^/api/([^/]+).*"
)
)


sum(rate(http_server_requests_seconds_count{uri=~"/api/auth/.*"}[1m]))
sum(rate(http_server_requests_seconds_count{uri=~"/api/patient/.*"}[1m]))
sum(rate(http_server_requests_seconds_count{uri=~"/api/doctor/.*"}[1m]))
sum(rate(http_server_requests_seconds_count{uri=~"/api/admin/.*"}[1m]))
sum(rate(http_server_requests_seconds_count{uri=~"/api/marketplace/.*"}[1m]))


--------------------------------------------------------------------------

Feature fixes :

DocumentService --> getDocumentDownloadUrl method returns a List of all 
the urls for all the documents in the same type . 
But every document type has one document
We return list for now so why dont change the controller.

Also every doc has expiration date. We have to make a logc for expired registrations 

----------------
Account Status contains also REJECTED and SUPSENDED enum
that has not logic attached to it .


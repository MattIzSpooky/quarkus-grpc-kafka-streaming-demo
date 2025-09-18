# Demo

## Windows
1. Install grpcurl
2. start the application (`./mvnw quarkus:dev`)
2. Run `grpcurl -plaintext -import-path src/main/proto -proto notification.proto -d '{\"user_id\":\"bob\"}' localhost:9000 notification.NotificationService/StreamNotifications
` to add a user to the connection list managed by the application
3. Send a PowerShell call to the rest endpoint `Invoke-RestMethod -Uri http://localhost:8080/notifications -Method Post -ContentType "application/json" -Body '{"userId":"bob","message":"Hello Bob from REST!"}'
`
## MacOS
1. Install grpcurl
2. start the application (`./mvnw quarkus:dev`)
2. Run `grpcurl -plaintext -d '{"userId":"bob"}' localhost:9000 notification.NotificationService/StreamNotifications` to add a user to the connection list managed by the application
3. Send a curl call to the rest endpoint `curl -X POST http://localhost:8080/notifications -H "Content-Type: application/json" -d '{"userId":"bob","message":"Hello Bob from REST!"}'
`
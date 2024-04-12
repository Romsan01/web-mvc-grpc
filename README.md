To start application inside the docker container:

1. mvn clean install
2. docker build -f docker/Dockerfile -t web .
3. docker run -p 8080:8080 -p 8181:8181 --name web web

To test REST API calls:
Install Postman and import Todos.postman_collection.json

To test GRPC calls:
1. brew install grpcurl
2. grpcurl -plaintext -d '{"description":"test1", "completionStatus": "false"}' localhost:8181 todo.TodoService/AddTodo
3. grpcurl -plaintext localhost:8181 todo.TodoService/GetAllTodos
4. grpcurl -plaintext -d '{"id": "1"}' localhost:8181 todo.TodoService/GetTodo
5. grpcurl -plaintext -d '{"id":"1", "description":"test1", "completionStatus": "true"}' localhost:8181 todo.TodoService/UpdateTodo
6. grpcurl -plaintext -d '{"id": "1"}' localhost:8181 todo.TodoService/DeleteTodo
# Simple search engine

This is simple search engine implemented as Inverted Index and returns sorted list of entries by TF-IDF

## How to use it

This is REST API application. After running main class server starts on port 8080 and can be reached on http://localhost:8080

There are two endpoints exposed for usage:

- http://localhost:8080/documents - it is used for sending new documents into application. This endpoint uses POST method with a JSON body
```
[
    {
        "id": "document name 1",
        "content": "simple document content"
    },
    {
        "id": "document name 2",
        "content": "second document"
    },
    {
        "id": "document name 3",
        "content": "just string words"
    }
]
```

- http://localhost:8080/search/{term} - it is used to retrieve a list of documents containing searched term. '{term}' should be replaced with searched word. This endpoint uses GET method

## Options to call REST API

- Postman - use presented urls and add a body for a POST request
- CRUD command from terminal e.g.
```
curl -H "Content-Type: application/json" -X POST -d '[{"id": "document name 1","content": "simple document content"},{"id": "document name 2","content": "second document"},{"id": "document name 3","content": "just string words"}]' http://localhost:8080/documents
```
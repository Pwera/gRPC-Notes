#gRPC

Is a free and open-source framework developed by Google, part of CNCF.
Allows to define request and response for RPC (Remote Procedure Calls) and handles all the rest for you.
On top of it, it's modern, fastm efficient, build on top of HTTP/2, low latency, support streaming, language independent.
It's used by :
- Google
- Netflix
- Square
- CoreOS
- CoackroachDB


## HTTP/2
gRPC levarages HTPP/2 as a backbone for communications, eg https://imagekit.io/demo/http2-vs-http1.
HTTP/2 is the newer standard for internet communications that address common pitfall of HTTP/1.1 on modern web pages
HTTP/1.1 open a new TCP connection to a server at each request
It does not compress headers (which are plaintext- heavy size)
It only works with Request/Response mechanism (no server push)

HTTP/2 was released in 2015, supports multiplexing
The client & server can push messages in parallel over the same TCP connection, this greatly reduces latency.

HTTP/2 supports server push, servers can push streams for one request from the client. It allso supports header compression.


## Types of API in gRPC
- Unary
- Server Streaming
- Client Streaming
- BI Directional Streaming

## Scalability in gRPC
- gRPC servers are asynchronous by default
- This means they do not blosk threads on request.
- Therefore each gRPC server can serve milion of requests in parallel.
- gRPC clients can be asynchronous or synchronous
- The client decides which model works best
- The gRPC clients can perform client side load balancing. Allows to scale horizontally.

## Security in gRPC
Each language will provide an API to load gRPC with the required certificates and provide encryption capability out  of the box
Additionally using Interceptors, we can also provide authentication

## Unary 
Unary is what a traditional API looks like (http rest)
``` 
     service GreetService {
          rpc Greet(GreetRequest) returns (GreetResponse){};
     }
}
```


## Server streaming
In gRPC server streaming calls are defined using keyword "stream"

``` 
     service GreetService {
          rpc Greet(GreetRequest) returns (stream GreetResponse){};
     }
}
```

## Client streaming
In gRPC client streaming calls are defined using keyword "stream".
The client will send many message to the server and will receive one response form the server, at any time.

``` 
     service GreetService {
          rpc ManyGreetRequest(stream GreetRequest) returns (GreetResponse){};
     }
}
```

## Bi Directional streaming

``` 
     service GreetService {
          rpc ManyGreetRequest(stream GreetRequest) returns (stream GreetResponse){};
     }
}
```



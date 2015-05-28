package br.com.oktolab.netflix;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;

import java.nio.charset.Charset;

public class RxNettyExample {

	public static void main(String... args) throws InterruptedException {
	        HttpServer<ByteBuf, ByteBuf> server = RxNetty.createHttpServer(8080, (request, response) -> {
	            System.out.println("Server => Request: " + request.getPath());
	            try {
	                if ("http://localhost:8080/error".equals(request.getPath())) {
	                    throw new RuntimeException("forced error");
	                }
	                response.setStatus(HttpResponseStatus.OK);
	                response.writeString("Path Requested =>: " + request.getPath() + '\n');
	                return response.close();
	            } catch (Throwable e) {
	                System.err.println("Server => Error [" + request.getPath() + "] => " + e);
	                response.setStatus(HttpResponseStatus.BAD_REQUEST);
	                response.writeString("Error 500: Bad Request\n");
	                return response.close();
	            }
	        });

	        server.start();

	        RxNetty.createHttpGet("http://localhost:8080/")
	               .flatMap(response -> response.getContent())
	               .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
	               .toBlockingObservable().forEach(System.out::println);

	        RxNetty.createHttpGet("http://localhost:8080/error")
	               .flatMap(response -> response.getContent())
	               .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
	               .toBlockingObservable().forEach(System.out::println);

	        RxNetty.createHttpGet("http://localhost:8080/data")
	               .flatMap(response -> response.getContent())
	               .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
	               .toBlockingObservable().forEach(System.out::println);

	        server.shutdown();
	    }
}

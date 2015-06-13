package br.com.oktolab.netflix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.pipeline.PipelineConfigurator;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.udp.client.UdpClientBuilder;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * HystrixObservableCommand - Used to wrap code that will execute potentially
 * risky functionality (typically meaning a service call over the network) - Bot
 * Busca passagem - Elt?
 * 
 * 
 * 
 * @author arthur
 *
 */
public class RxNettyExample {

	static int counter = 0;

	public static void main(String... args) throws InterruptedException {
		HttpServer<ByteBuf, ByteBuf> server = RxNetty.createHttpServer(8090, (request, response) -> {
	            System.out.println("Server => Request: " + request.getPath());
	            try {
            		UdpClientBuilder<DatagramPacket, DatagramPacket> udpClientBuilder = RxNetty.<DatagramPacket, DatagramPacket>newUdpClientBuilder("127.0.0.1", 7777)
            				.enableWireLogging(LogLevel.INFO);
            		RxClient<DatagramPacket, DatagramPacket> udpClient = udpClientBuilder.build(); 
            		String first = 
            				udpClient.connect()
            				.flatMap(new Func1<ObservableConnection<DatagramPacket, DatagramPacket>,
            						Observable<DatagramPacket>>() {
            					@Override
            					public Observable<DatagramPacket> call(ObservableConnection<DatagramPacket, DatagramPacket> connection) {
            						connection.writeStringAndFlush("Is there anybody out there?" + counter);
            						return connection.getInput();
            					}
            				})
            				.take(1)// todo 100?
            				.map(new Func1<DatagramPacket, String>() {
            					@Override
            					public String call(DatagramPacket datagramPacket) {
            						String content = datagramPacket.content().toString(Charset.defaultCharset());
            						System.out.println("map -> call -> content = " + ++counter  + " - " + content);
            						datagramPacket.release();
            						return content;
            					}
            				})
            				.toBlocking()
            				.first();
            		System.out.println("First? --> " + first);
	                response.writeString("Path Requested =>: " + request.getPath() + '\n');
	                response.setStatus(HttpResponseStatus.OK);
	                return response.close();
	            } catch (Throwable e) {
	                System.err.println("Server => Error [" + request.getPath() + "] => " + e);
	                response.setStatus(HttpResponseStatus.BAD_REQUEST);
	                response.writeString("Error 500: Bad Request\n");
	                return response.close();
	            }
	        });

	        server.start();
//	        HttpClientResponse<ByteBuf> response =
//	                RxNetty.createHttpPost("http://localhost:" + 777 + '/',
//	                                       Observable.just(Unpooled.buffer().writeBytes("Hello!".getBytes())))
//	                                                 .toBlocking().toFuture().get(1, TimeUnit.MINUTES);
        	for (int i = 0 ; i < 100; i ++ ) {
		        RxNetty.createHttpPost("http://localhost:8090/", Observable.just(Unpooled.buffer().writeBytes("{name:\"Arthur\"}".getBytes())))
		               .flatMap(response -> response.getContent())
		               .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
		               .toBlocking().forEach(System.out::println);
        	}
//
//	        RxNetty.createHttpGet("http://localhost:8090/error")
//	               .flatMap(response -> response.getContent())
//	               .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
//	               .toBlocking().forEach(System.out::println);
//
//	        RxNetty.createHttpGet("http://localhost:8090/data")
//	               .flatMap(response -> response.getContent())
//	               .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
//	               .toBlocking().forEach(System.out::println);

	        server.shutdown();
	    }
}

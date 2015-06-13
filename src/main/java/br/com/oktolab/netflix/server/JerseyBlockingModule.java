package br.com.oktolab.netflix.server;

import static com.netflix.config.ConfigurationManager.getConfigInstance;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import netflix.karyon.KaryonBootstrap;
import netflix.karyon.jersey.blocking.KaryonJerseyModule;
import netflix.karyon.transport.interceptor.DuplexInterceptor;
import rx.Observable;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.netflix.governator.annotations.Modules;

/*@KaryonBootstrap( name = "jersey-blocking" )
@Singleton
@Modules(include = {
        JerseyBlockingModule.TestModule.class,
        JerseyBlockingModule.KaryonRxRouterModuleImpl.class,
})*/
public interface JerseyBlockingModule {
  /*class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        getConfigInstance().addProperty("com.sun.jersey.config.property.packages", "br.com.oktolab.netflix");       
    }
  }
  
  class KaryonRxRouterModuleImpl extends KaryonJerseyModule {
    @Override
    protected void configureServer() {
      server().port( 7001 ).threadPoolSize( 200 );
    }
  }
  
  public class AccessInterceptor implements DuplexInterceptor<HttpServerRequest<ByteBuf>, HttpServerResponse<ByteBuf>> {
    @Override
    public Observable<Void> in(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
      return Observable.empty();
    }

    @Override
    public Observable<Void> out(HttpServerResponse<ByteBuf> response) {
      return Observable.empty();
    }
  }
  */
}

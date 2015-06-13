package br.com.oktolab.netflix;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.samples.SampleApplicationWithDefaultConfiguration;
import com.netflix.governator.annotations.Modules;
import com.netflix.governator.guice.BootstrapModule;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.guice.LifecycleInjectorBuilder;
import com.netflix.governator.guice.bootstrap.GovernatorBootstrap;
import com.sun.jersey.guice.JerseyServletModule;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerBuilder;
import netflix.adminresources.resources.KaryonWebAdminModule;
import netflix.karyon.Karyon;
import netflix.karyon.KaryonBootstrap;
import netflix.karyon.KaryonBootstrapModule;
import netflix.karyon.KaryonServer;
import netflix.karyon.ShutdownModule;
import netflix.karyon.archaius.ArchaiusBootstrap;
import netflix.karyon.archaius.ArchaiusBootstrapModule;
import netflix.karyon.eureka.KaryonEurekaModule;
import netflix.karyon.jersey.blocking.JerseyBasedRouter;
import netflix.karyon.jersey.blocking.KaryonJerseyModule;
import netflix.karyon.servo.KaryonServoModule;
import br.com.oktolab.netflix.JerseyHelloWorldApp.KaryonJerseyModuleImpl;
import br.com.oktolab.netflix.ws.healthcheck.HealthCheck;

//https://github.com/Netflix/archaius/wiki/Getting-Started

//@ArchaiusBootstrap
//@KaryonBootstrap(name = "netflix-stack", healthcheck = HealthCheck.class)
//@Modules(include = {
//        ShutdownModule.class,
//        KaryonWebAdminModule.class,
//        // KaryonEurekaModule.class, // Uncomment this to enable Eureka client.
//        KaryonJerseyModuleImpl.class,
//        KaryonServoModule.class
//})
public class Application {

//	public static void main(String args[]) throws Exception {
//    	System.setProperty("archaius.deployment.applicationId", "netflix-stack");
////    	System.setProperty(PropertyNames.SERVER_BOOTSTRAP_BASE_PACKAGES_OVERRIDE, "com.netflix");
//    	System.setProperty("com.netflix.karyon.server.base.packages", "com.netflix,br.com.oktolab");
//    	
//    	BaseNettyServer nettyServer = new BaseNettyServer();
//    	nettyServer.start();
//    }
	
//	public static void main(String[] args) {
//		HealthCheck healthCheckHandler = new HealthCheck();
//	    Karyon.forRequestHandler(8888,
//	            new RxNettyHandler("/healthcheck",
//	                    new HealthCheckEndpoint(healthCheckHandler)),
//	            new KaryonBootstrapModule(healthCheckHandler),
//	            new ArchaiusBootstrapModule("netflix-stack"),
//	             KaryonEurekaModule.asBootstrapModule(), /* Uncomment if you need eureka */
//	            Karyon.toBootstrapModule(KaryonWebAdminModule.class),
//	            ShutdownModule.asBootstrapModule(),
//	            KaryonServoModule.asBootstrapModule()
//	    ).startAndWaitTillShutdown();
//	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		System.setProperty("archaius.configurationSource.defaultFileName", "netflix-stack.properties");
        System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");
    	System.setProperty("archaius.deployment.applicationId", "netflix-stack");
    	System.setProperty("com.sun.jersey.config.property.packages", "br.com.oktolab.netflix.ws");
    	System.setProperty("com.netflix.karyon.server.base.packages", "br.com.oktolab");
    	
//    	LifecycleInjector injector = LifecycleInjector.builder().build();
    	JerseyBasedRouter jerseyBasedRouter = new JerseyBasedRouter();
    	HttpServer<ByteBuf, ByteBuf> httpServer = RxNetty.createHttpServer(6616, jerseyBasedRouter);
//		HttpServerBuilder httpServerBuilder = new HttpServerBuilder(6616, jerseyBasedRouter);
    	
//		netflix.karyon.jersey.blocking.KaryonJerseyModule 
		
		KaryonServer server = Karyon.forHttpServer(httpServer, 
				new KaryonBootstrapModule(new HealthCheck()),
	            new ArchaiusBootstrapModule("netflix-stack"),
	            KaryonEurekaModule.asBootstrapModule(),
	            Karyon.toBootstrapModule(KaryonWebAdminModule.class),
	            ShutdownModule.asBootstrapModule(),
	            KaryonServoModule.asBootstrapModule()
	            );
		//		new JerseyHelloWorldApp.KaryonJerseyModuleImpl()
		server.start();
		server.waitTillShutdown();
    	
    	
    	/*KaryonServer forApplication = Karyon.forApplication(JerseyHelloWorldApp.class, 
//    			new KaryonBootstrapModule(new HealthCheck()),
//	            new ArchaiusBootstrapModule("netflix-stack"),
	            KaryonEurekaModule.asBootstrapModule(),
	            Karyon.toBootstrapModule(KaryonWebAdminModule.class),
	            ShutdownModule.asBootstrapModule(),
	            KaryonServoModule.asBootstrapModule()
	            );
    	forApplication.start();
    	forApplication.waitTillShutdown();*/
	}
	
	static class A extends KaryonJerseyModule {

		@Override
		protected void configureServer() {
			// TODO Auto-generated method stub
			
		}
		
	}
}

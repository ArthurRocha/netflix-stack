package br.com.oktolab.netflix;

import static com.netflix.config.ConfigurationManager.getConfigInstance;
import netflix.adminresources.resources.KaryonWebAdminModule;
import netflix.karyon.KaryonBootstrap;
import netflix.karyon.ShutdownModule;
import netflix.karyon.archaius.ArchaiusBootstrap;
import netflix.karyon.examples.hellonoss.common.auth.AuthInterceptor;
import netflix.karyon.examples.hellonoss.common.auth.AuthenticationService;
import netflix.karyon.examples.hellonoss.common.auth.AuthenticationServiceImpl;
import netflix.karyon.examples.hellonoss.common.auth.LoggingInterceptor;
import netflix.karyon.jersey.blocking.KaryonJerseyModule;
import netflix.karyon.servo.KaryonServoModule;
import br.com.oktolab.netflix.JerseyHelloWorldApp.KaryonJerseyModuleImpl;
import br.com.oktolab.netflix.ws.healthcheck.HealthCheck;

import com.netflix.governator.annotations.Modules;

@ArchaiusBootstrap
@KaryonBootstrap(name = "netflix-stack", healthcheck = HealthCheck.class)
@Modules(include = {
        ShutdownModule.class,
        KaryonWebAdminModule.class,
        // KaryonEurekaModule.class, // Uncomment this to enable Eureka client.
        KaryonJerseyModuleImpl.class,
        KaryonServoModule.class
})
public interface JerseyHelloWorldApp {

    public class KaryonJerseyModuleImpl extends KaryonJerseyModule {
        @Override
        protected void configureServer() {
        	getConfigInstance().addProperty("com.sun.jersey.config.property.packages", "br.com.oktolab.netflix");       
            bind(AuthenticationService.class).to(AuthenticationServiceImpl.class);
            interceptorSupport().forUri("/*").intercept(LoggingInterceptor.class);
            interceptorSupport().forUri("/hello").interceptIn(AuthInterceptor.class);
            server().port(8888).threadPoolSize(100);
        }
    }
}

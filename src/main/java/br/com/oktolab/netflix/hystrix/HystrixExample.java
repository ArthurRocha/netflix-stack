package br.com.oktolab.netflix.hystrix;

import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;

public class HystrixExample {

	public static void main(String[] args) {
		BuscadorAsyncCommand bc = new BuscadorAsyncCommand();
		ConnectableObservable<String> publish = bc.observe().publish();
		publish.subscribe((v) -> {
			System.out.println(v);
		});
		
		
		System.out.println(new CommandHelloFailure("World").execute());
		Observable<String> observe = new CommandObservableHelloWorld("World").toObservable();
//		observe.subscribe(onNext, onError)
	}

	static class BuscadorAsyncCommand extends HystrixObservableCommand<String> {

		private static final String name = "buscador-passagem";

		protected BuscadorAsyncCommand() {
			super(HystrixCommandGroupKey.Factory.asKey(name));
		}

		@Override
		protected Observable<String> construct() {
			return Observable.create(new Observable.OnSubscribe<String>() {
				@Override
				public void call(Subscriber<? super String> observer) {
					try {
						if (!observer.isUnsubscribed()) {
							// a real example would do work like a network call
							// here
							observer.onNext("Hello");
							observer.onNext(name + "!");
							observer.onCompleted();
						}
					} catch (Exception e) {
						observer.onError(e);
					}
				}
			});
		}
	}
	
	static class CommandObservableHelloWorld extends HystrixObservableCommand<String> {
		 
	    private final String name;
	 
	    public CommandObservableHelloWorld(String name) {
	        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")); 
	        this.name = name;
	    } 
	 
	    @Override 
	    protected Observable<String> construct() {
	        return Observable.create(new Observable.OnSubscribe<String>() {
	            @Override 
	            public void call(Subscriber<? super String> observer) { 
	                try { 
	                    if (!observer.isUnsubscribed()) { 
	                        // a real example would do work like a network call here 
	                        observer.onNext("Hello"); 
	                        observer.onNext(name + "!"); 
	                        observer.onCompleted(); 
	                    } 
	                } catch (Exception e) { 
	                    observer.onError(e); 
	                } 
	            } 
	         } ); 
	    } 
	} 
	
	static class CommandHelloFailure extends HystrixCommand<String> {
		 
	    private final String name;
	 
	    public CommandHelloFailure(String name) {
	        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")); 
	        this.name = name;
	    } 
	 
	    @Override 
	    protected String run() {
	        throw new RuntimeException("this command always fails");
	    } 
	 
	    @Override 
	    protected String getFallback() {
	        return "Hello Failure " + name + "!";
	    } 
	} 
}

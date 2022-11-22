package org.acme;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/hello")
public class GreetingResource {

    @Channel("quote-requests")
  Emitter<String> quoteRequestEmitter;

  @Incoming("requests")
  public void process(byte[] quoteRequest) throws InterruptedException {
    System.out.println("Received request: " + new String(quoteRequest));
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    System.out.println("Sending request");
    quoteRequestEmitter.send("Hello romain");
    return "Hello from RESTEasy Reactive with Quarkus";
  }
}
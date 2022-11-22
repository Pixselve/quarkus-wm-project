package org.acme;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class GreetingResource {

  @Inject
  Template welcome;

  @Inject
  ReactiveMailer reactiveMailer;

  @Incoming("requests")
  public Uni<Void> process(JsonObject quoteRequest) {
    NestedData data = quoteRequest.getJsonObject("data").mapTo(NestedData.class);
    System.out.printf("✉️ Received request for %s %s with email %s%n", data.firstName, data.lastName, data.email);
    return reactiveMailer.send(Mail.withHtml(data.email, "❤️ A big welcome from KittenAsso's", welcome.data("firstName", data.firstName).data("lastName", data.lastName).render()));
  }
}
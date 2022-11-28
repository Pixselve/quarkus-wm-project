package org.acme;

import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class GreetingResource {

  @Inject
  Template welcome;

  @Inject
  ReactiveMailer reactiveMailer;


  @Incoming("events")
  public void onEvent(JsonObject event) {
    Log.info("New association event created");
    try {
      Event e = event.getJsonObject("data").mapTo(Event.class);


      DateTime start = new DateTime(e.start);
      DateTime end = new DateTime(e.end);


      VEvent meeting = new VEvent(start, end, e.name);

      net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();

      icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
      icsCalendar.getProperties().add(CalScale.GREGORIAN);


      // Add the event and print
      icsCalendar.getComponents().add(meeting);
      System.out.println(icsCalendar);

      return reactiveMailer.send(Mail.withText(
              data.email,
              "Invitation: " + e.name + " from " + e.associationName,
              "You have been invited to an event.")
          .addAttachment("invitation.ics", icsCalendar.toString().getBytes(), "text/calendar"));


    } catch (DecodeException e) {
      Log.error("Error while creating event", e);
    }

  }

  @Incoming("requests")
  public Uni<Void> process(JsonObject quoteRequest) {
    NestedData data = quoteRequest.getJsonObject("data").mapTo(NestedData.class);
    System.out.printf("✉️ Received request for %s %s with email %s%n", data.firstName, data.lastName, data.email);


    return reactiveMailer.send(Mail.withHtml(data.email, "❤️ A big welcome from KittenAsso's", welcome.data("firstName", data.firstName).data("lastName", data.lastName).render()));
  }
}
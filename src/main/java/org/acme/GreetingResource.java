package org.acme;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.GregorianCalendar;


@ApplicationScoped
public class GreetingResource {

  @Inject
  Template welcome;

  @Inject
  ReactiveMailer reactiveMailer;


  @Incoming("events")
  public void onEvent(JsonObject event) {
  }

  @Incoming("requests")
  public Uni<Void> process(JsonObject quoteRequest) {
    NestedData data = quoteRequest.getJsonObject("data").mapTo(NestedData.class);
    System.out.printf("✉️ Received request for %s %s with email %s%n", data.firstName, data.lastName, data.email);
    java.util.Calendar startDate = new GregorianCalendar();
    startDate.set(java.util.Calendar.MONTH, Calendar.DECEMBER);
    startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
    startDate.set(java.util.Calendar.YEAR, 2022);
    startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
    startDate.set(java.util.Calendar.MINUTE, 0);
    startDate.set(java.util.Calendar.SECOND, 0);

    java.util.Calendar endDate = new GregorianCalendar();
    startDate.set(java.util.Calendar.MONTH, Calendar.DECEMBER);
    startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
    startDate.set(java.util.Calendar.YEAR, 2022);
    startDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
    startDate.set(java.util.Calendar.MINUTE, 0);
    startDate.set(java.util.Calendar.SECOND, 0);

    DateTime start = new DateTime(startDate.getTime());
    DateTime end = new DateTime(endDate.getTime());

    String eventName = "Progress Meeting";

    VEvent meeting = new VEvent(start, end, eventName);

    net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();

    icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
    icsCalendar.getProperties().add(CalScale.GREGORIAN);


    // Add the event and print
    icsCalendar.getComponents().add(meeting);
    System.out.println(icsCalendar);

    return reactiveMailer.send(Mail.withHtml(data.email, "❤️ A big welcome from KittenAsso's", welcome.data("firstName", data.firstName).data("lastName", data.lastName).render()).addAttachment("invitation.ics", icsCalendar.toString().getBytes(), "text/calendar"));
  }
}
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
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.time.Instant;
import java.util.Date;


@ApplicationScoped
public class MainService {

  @Inject
  Template welcome;

  @Inject
  ReactiveMailer reactiveMailer;


  @Incoming("events")
  public Uni<Void> onEvent(JsonObject event) {
    Log.info("New association event created");
    try {
      Event e = event.getJsonObject("data").mapTo(Event.class);


      Instant start = Instant.parse(e.start);
      Instant end = Instant.parse(e.end);
      DateTime startDateTime = new DateTime(Date.from(start));
      DateTime endDateTime = new DateTime(Date.from(end));

      VEvent meeting = new VEvent(startDateTime, endDateTime, e.name);

      net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();

      icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
      icsCalendar.getProperties().add(CalScale.GREGORIAN);

      for (String attendeesEmail : e.attendeesEmails) {
        Attendee attendee = new Attendee(URI.create("mailto:" + attendeesEmail));
        attendee.getParameters().add(Role.REQ_PARTICIPANT);
        meeting.getProperties().add(attendee);
      }

      // Add the event and print
      icsCalendar.getComponents().add(meeting);


      Mail mail = Mail.withText(
          e.attendeesEmails[0],
          "Invitation: " + e.name + " from " + e.associationName,
          "You have been invited to an event.");

      mail.addAttachment("event.ics", icsCalendar.toString().getBytes(), "text/calendar");


      for (int i = 1; i < e.attendeesEmails.length; i++) {
        mail.addCc(e.attendeesEmails[i]);
      }

      return reactiveMailer.send(mail);

    } catch (DecodeException e) {
      Log.error("Error while creating event", e);
    }

    return Uni.createFrom().voidItem();

  }

  @Incoming("registration_confirmation")
  public Uni<Void> onRequest(JsonObject quoteRequest) {
    UserRegistration data = quoteRequest.getJsonObject("data").mapTo(UserRegistration.class);
    Log.info("New user registered");
    return reactiveMailer.send(Mail.withHtml(data.email, "❤️ A big welcome from KittenAsso's", welcome.data("firstName", data.firstName).data("lastName", data.lastName).render()));
  }
}
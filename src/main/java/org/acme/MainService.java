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
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@ApplicationScoped
public class MainService {

    @Inject
    Template welcome;

    @Inject
    ReactiveMailer reactiveMailer;


    @Incoming("events")
    @Counted(description = "How many creation event request has been made", absolute = true, name = "countEventCreation")
    @Timed(name = "creationEventTime", description = "A measure of how long it takes to handle the event creation request", unit = "milliseconds")
    public Uni<Void> onEvent(JsonObject event) {
        Log.info("New event event created");
        try {
            Event e = event.getJsonObject("data").mapTo(Event.class);
            Log.info("Event created: " + e.toString());

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime startDateTime2 = LocalDateTime.parse(e.start, formatter);
            LocalDateTime endDateTime2 = LocalDateTime.parse(e.end, formatter);

            DateTime startDateTime = new DateTime(startDateTime2.toInstant(ZoneOffset.UTC).toEpochMilli());
            DateTime endDateTime = new DateTime(endDateTime2.toInstant(ZoneOffset.UTC).toEpochMilli());

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
    @Counted(description = "How many registration confirmation request has been made", absolute = true, name = "countRegistrationConfirmation")
    @Timed(name = "registrationConfirmationTime", description = "A measure of how long it takes to handle the registration confirmation request", unit = "milliseconds")
    public Uni<Void> onRequest(JsonObject quoteRequest) {
        UserRegistration data = quoteRequest.getJsonObject("data").mapTo(UserRegistration.class);
        Log.info("New user registered");
        return reactiveMailer.send(Mail.withHtml(data.email, "❤️ A big welcome from KittenAsso's", welcome.data("firstName", data.firstName).data("lastName", data.lastName).data("verifLink", data.validationUrl).render()));
    }
}

package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Event {
  public String name;
  public String description;
  public String location;
  public long start;
  public long end;

  public String associationName;

  public String[] attendeesEmails;

  public Event() {
  }

  @Override
  public String toString() {
    return "Event{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", location='" + location + '\'' +
        ", start='" + start + '\'' +
        ", end='" + end + '\'' +
        '}';
  }
}

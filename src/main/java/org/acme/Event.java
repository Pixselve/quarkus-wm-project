package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Arrays;

@RegisterForReflection
public class Event {
  public String name;
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
        ", start=" + start +
        ", end=" + end +
        ", associationName='" + associationName + '\'' +
        ", attendeesEmails=" + Arrays.toString(attendeesEmails) +
        '}';
  }
}

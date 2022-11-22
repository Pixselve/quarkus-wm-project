package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
class NestedData {
  public String firstName;
  public String lastName;
  public String email;

  public NestedData(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  public NestedData() {
  }

  @Override
  public String toString() {
    return "NestedData{" +
        "firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
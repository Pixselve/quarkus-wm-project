package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
class UserRegistration {
  public String firstName;
  public String lastName;
  public String email;

  public UserRegistration(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  public UserRegistration() {
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
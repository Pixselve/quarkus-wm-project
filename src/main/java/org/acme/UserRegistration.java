package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
class UserRegistration {
  public String firstName;
  public String lastName;
  public String email;
  public String validationUrl;

  public UserRegistration(String firstName, String lastName, String email, String validationUrl) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.validationUrl = validationUrl;
  }

  public UserRegistration() {
  }

  @Override
  public String toString() {
    return "NestedData{" +
        "firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", email='" + email + '\'' +
        ", validationUrl='" + validationUrl + '\'' +
        '}';
  }
}
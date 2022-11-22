package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
class DataFromNest {
  public String data;
  public String pattern;

  public DataFromNest(String data, String pattern) {
    this.data = data;
    this.pattern = pattern;
  }

  public DataFromNest() {
  }

  @Override
  public String toString() {
    return "DataFromNest{" +
        "data='" + data + '\'' +
        ", pattern='" + pattern + '\'' +
        '}';
  }
}

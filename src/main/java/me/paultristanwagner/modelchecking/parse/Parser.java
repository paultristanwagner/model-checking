package me.paultristanwagner.modelchecking.parse;

import java.util.concurrent.atomic.AtomicInteger;

public interface Parser<T> {

  default T parse(String input) {
    return parse(input, new AtomicInteger(0));
  }

  T parse(String input, AtomicInteger index);
}

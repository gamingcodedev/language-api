package de.gamingcode.languageapi;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Placeholder {

  private final String target;
  private final Object replacement;

}

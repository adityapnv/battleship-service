package com.abnamro.battleship.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Swagger constants.
 */
@Component
@ConfigurationProperties("swagger.api")
@Getter
@Setter
public class SwaggerConstants {

  private String title;
  private String description;
  private String version;
  private Contact contact;
  private String basePackage;
  private String apiType;
  private boolean defaultResponseMessage;
}

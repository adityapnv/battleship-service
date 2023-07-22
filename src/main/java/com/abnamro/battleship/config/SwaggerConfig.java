package com.abnamro.battleship.config;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The type Swagger config.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  private static final String X_API_TYPE = "x-apiType";
  private final SwaggerConstants swaggerConstants;
  private Contact apiContact;

  /**
   * Instantiates a new Swagger config.
   *
   * @param swaggerConstants the swagger constants
   */
  public SwaggerConfig(final SwaggerConstants swaggerConstants) {
    this.swaggerConstants = swaggerConstants;
  }

  /**
   * Api docket.
   *
   * @return the docket
   */
  @Bean
  public Docket api() {

    return new Docket(DocumentationType.SWAGGER_2)
        .useDefaultResponseMessages(swaggerConstants.isDefaultResponseMessage()).apiInfo(apiInfo()).select()
        .apis(RequestHandlerSelectors.basePackage(swaggerConstants.getBasePackage())).paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {

    Optional.ofNullable(swaggerConstants.getContact())
        .ifPresent(con -> this.apiContact = new Contact(con.getTeamName(), con.getUrl(), con.getEmail()));

    return new ApiInfo(swaggerConstants.getTitle(), swaggerConstants.getDescription(), swaggerConstants.getVersion(),
        null, this.apiContact, null, null,
        List.of(new StringVendorExtension(X_API_TYPE, swaggerConstants.getApiType())));

  }

}

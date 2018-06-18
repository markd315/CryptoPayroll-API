package io.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T10:30:50.994-04:00")

@Configuration
public class SwaggerDocumentationConfig {

  ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("NU-Payroll cryptopayments")
        .description(
            "This is a microservice for accepting and aggregating cryptocurrency orders from our HCM clients to reduce transactional overhead on Payroll.")
        .license("All rights reserved by Mark Davis except where overridden by the rights of Ultimate Software")
        .licenseUrl("http://unlicense.org")
        .termsOfServiceUrl("")
        .version("1.0.0")
        .contact(new Contact("", "", "mark_davis@ultimatesoftware.com"))
        .build();
  }

  @Bean
  public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("io.swagger.api"))
        .build()
        .directModelSubstitute(org.threeten.bp.LocalDate.class, java.sql.Date.class)
        .directModelSubstitute(org.threeten.bp.OffsetDateTime.class, java.util.Date.class)
        .apiInfo(apiInfo());
  }

}

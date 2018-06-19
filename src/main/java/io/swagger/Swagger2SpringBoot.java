package io.swagger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {"io.swagger", "com.coinbase.exchange.api", "io.swagger.api", "io.swagger.repo", "io.swagger.services",
    "io.swagger.configuration"})
public class Swagger2SpringBoot implements CommandLineRunner {

  @Override
  public void run(String... arg0) throws Exception {
    if (arg0.length > 0 && arg0[0].equals("exitcode")) {
      throw new ExitException();
    }
  }

  public static final String SYSTEM_PROPERTY_JAVA_AWT_HEADLESS = "java.awt.headless";

  public static void main(String[] args) throws Exception {
    System.setProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, System.getProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, Boolean.toString(false)));
    new SpringApplication(Swagger2SpringBoot.class).run(args);
  }

  class ExitException extends RuntimeException implements ExitCodeGenerator {
    private static final long serialVersionUID = 1L;

    @Override
    public int getExitCode() {
      return 10;
    }

  }
}

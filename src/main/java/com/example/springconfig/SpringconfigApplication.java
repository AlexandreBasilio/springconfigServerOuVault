package com.example.springconfig;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;


@Log4j2
@SpringBootApplication
@EnableConfigurationProperties({BootfulProperties.class, MyConfigurationVault.class})
public class SpringconfigApplication {

    public static void main(String[] args) {
        // SpringApplication.run(SpringconfigApplication.class, args);
        new SpringApplicationBuilder()
                .sources(SpringconfigApplication.class)
                //              .initializers(applicationContext -> applicationContext.getEnvironment().getPropertySources().addLast(new BootFulPropertySource()))
                .run(args);
    }

    // add custom propertie
    @Autowired
    void contributeToPropertySources(ConfigurableEnvironment environment) {
        environment.getPropertySources().addLast(new BootFulPropertySource());
    }

    @Bean
        // @RefreshScope  refresh value from consfig server
    ApplicationRunner applicationRunner(Environment env,
                                        @Value("${HOME: not found}") String userHome,
                                        @Value("${spring.datasource.url: Not found}") String springDatasourceUrl,
                                        @Value("${bootiful-message}") String bootfilmessage,
                                        BootfulProperties bootfulProperties,
                                        @Value("${message-from-program-args:}") String messageFromProgramArgs,
                                        @Value("${message-from-cloud-config-server}") String propertieFromConfigServer,
                                        @Value("${greeting-message:Default Hello : ${message-from-application-properties} }") String defaultValue
                                        //@Value("${example.password}") String passwordFromVault,
                                        //MyConfigurationVault myConfiguration)
    ) {
        return args -> {

            // FROM ENVIRONMENT
            log.info("FROM ENVIRONMENT VARIABLE=" + env.getProperty("message-from-application-properties"));
            log.info("FROM ENVIRONMENT VARIABLE=" + env.getProperty("greeting-message"));
            log.info("FROM ENVIRONMENT VARIABLE=" + env.getProperty("spring.datasource.url"));
            log.info("FROM ENVIRONMENT VARIABLE=" + env.getProperty("bootful.message"));
            log.info("FROM ENVIRONMENT VARIABLE=" + env.getProperty("spring.config.import"));
            log.info("FROM ENVIRONMENT VARIABLE=" + env.getProperty("spring.application.name"));

            // FROM ENVIRONMENT VARIABLE
            log.info("FROM ENVIRONMENT VARIABLE HOME=" + userHome);

            // Replace property spring.datasource.url avec le valeur de sa variable d'environment SPRING_DATASOURCE_URL
            log.info("REPLACE WITH SPRING_DATASOURCE_URL if exists=" + springDatasourceUrl);

            // From Custom on the fly propertie
            log.info("FROM CUSTOM PROPERTY=" + bootfilmessage);

            // From Bean BootfulProperties
            log.info("FROM BEAN BOOTIFUL=" + bootfulProperties.getMessage());

            // From Program Args
            log.info("FROM PROGRAM ARGS=" + messageFromProgramArgs);

            // Config Server
            log.info("CONFIG SERVER propertie from spring cloud config server " + propertieFromConfigServer);

            // Show defult message
            log.info("SHOW DEFALT MESSAGE FOR A PROPERTY=" + defaultValue);

            // VAULT
            //log.info("propertie from VAULT " + myConfiguration.getPassword() + myConfiguration.getUsername());
            //log.info("FROM VAULT - PASSWORD=" + passwordFromVault);
        };
    }

    static class BootFulPropertySource extends PropertySource<String> {

        public BootFulPropertySource() {
            super("bootiful");
        }

        @Override
        public Object getProperty(String name) {
            if (name.equalsIgnoreCase("bootiful-message")) {
                return "Hello from " + BootFulPropertySource.class.getSimpleName() + "!";
            }
            return null;
        }
    }
}

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("bootful")
class BootfulProperties {
    private final String message;
}

@ConfigurationProperties(prefix = "example")
@Data
class MyConfigurationVault {

    private String username;
    private String password;
}

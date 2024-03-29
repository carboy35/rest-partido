package cl.servel.gasto;


import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig 
{                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .paths(PathSelectors.any())            
          .apis( RequestHandlerSelectors.basePackage("cl.servel.gasto"))
          .build().pathMapping("/")
          .apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() 
    {
        return new ApiInfo(
		 "SERVEL Gasto Electoral", 
         "Servicios de consulta Gasto Electoral", 
         "1.0", 
         "Terminos de servicio", 
          new Contact("ADEXUS", "", "ao.support@adexus.cl"), 
          "License of API", "API license URL", 
          Collections.emptyList());
   }
}
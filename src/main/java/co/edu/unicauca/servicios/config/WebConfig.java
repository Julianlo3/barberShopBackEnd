package co.edu.unicauca.servicios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Configura el acceso público a los archivos subidos
     * (por ejemplo, imágenes de los servicios).
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Obtiene la ruta absoluta dentro del proyecto (fuera del JAR)
                String projectDir = System.getProperty("user.dir");
                String absolutePath = projectDir + File.separator + uploadDir;

                // Crea el directorio si no existe
                File dir = new File(absolutePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                    System.out.println("Directorio de uploads creado en: " + absolutePath);
                }

                // Mapea /uploads/** a la carpeta física
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + absolutePath + File.separator);
            }
        };
    }
}

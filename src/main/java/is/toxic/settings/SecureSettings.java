package is.toxic.settings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "is-toxic")
public class SecureSettings {
    private List<MappingPermissions> mappingPermissions = new ArrayList<>();
}

package is.toxic.settings;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class MappingPermissions {
    private String mapping;
    private List<String> roles;
}

package pokemon;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import pokemon.resource.Resource;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(Resource.class);
    }
}

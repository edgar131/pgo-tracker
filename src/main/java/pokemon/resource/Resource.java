package pokemon.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pokemon.service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Component
@Path("/")
public class Resource {

    private Service service;

    @Autowired
    public Resource(Service service){
        this.service = service;
    }

    @GET
    public String output(@Context HttpServletRequest req) {
        service.login(req);
        service.getProfile();
        return null;
    }
}

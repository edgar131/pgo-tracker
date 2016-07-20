package pokemon.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pokemon.protobuf.PokemonProtos;
import pokemon.service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import static pokemon.protobuf.PokemonProtos.ResponseEnvelop.Profile;

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
        Profile profile = service.getProfile();
        PokemonProtos.ResponseEnvelop.HeartbeatPayload hb = service.heartbeat();
        for(PokemonProtos.ResponseEnvelop.ClientMapCell cell: hb.getCellsList()){
            for(PokemonProtos.ResponseEnvelop.MapPokemonProto pokemon: cell.getMapPokemonList()){
                System.out.println(pokemon.toString());
            }
        }
        return null;
    }
}

package pokemon.resource;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import com.google.common.collect.Sets;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.exceptions.RemoteServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import pokemon.Util.CatchablePokemonToPokemonLocationConverter;
import pokemon.domain.Point;
import pokemon.dto.PokemonLocation;
import pokemon.service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;
@Component
@Path("/")
public class Resource {

    private static final String AUTH = "AUTH";
    private static final String POKEMON = "POKEMON";
    private Service service;

    @Autowired
    public Resource(Service service){
        this.service = service;
    }

    @GET
    @CrossOrigin
    @RequestMapping
    @Produces("application/json")
    public Set<PokemonLocation> output(@QueryParam("lat") Double latitude,
                                       @QueryParam("lon") Double longitude,
                                       @Context HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        AuthInfo authInfo = (AuthInfo)session.getAttribute(AUTH);
        if(authInfo == null || !authInfo.hasToken()){
            authInfo = service.login();
            session.setAttribute(AUTH, authInfo);
            if(authInfo != null && authInfo.hasToken()){
                Set<PokemonLocation> pokemons = Sets.newConcurrentHashSet();
                session.setAttribute(POKEMON, pokemons);
                service.beginProcess(pokemons, new Point(latitude, longitude));
            }
        } else {
            return (Set<PokemonLocation>)session.getAttribute(POKEMON);
        }
        return null;
    }
}

package pokemon.resource;

import POGOProtos.Map.Pokemon.MapPokemonOuterClass;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import pokemon.Util.CatchablePokemonToPokemonLocationConverter;
import pokemon.domain.Point;
import pokemon.dto.PokemonLocation;
import pokemon.service.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Component
@Path("/")
public class Resource {

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
                                       @QueryParam("lon") Double longitude) {
        if(!service.hasPokemonGo()){
            service.login();
        }
        Set<PokemonLocation> pokemonLocations = new HashSet<>();
        try {
            Point basePoint = new Point(latitude, longitude);
            Date startTime = new Date();
            Set<CatchablePokemon> pokemons = service.getPokemon(basePoint);
            Date endTime = new Date();
            pokemonLocations = CatchablePokemonToPokemonLocationConverter.apply(pokemons, basePoint);
            System.out.println("Retrieved: " + pokemonLocations.size() + " Pokemon in " + ((endTime.getTime()-startTime.getTime())/1000) + " seconds");
            for(PokemonLocation pokemonLocation: pokemonLocations){
                System.out.println(pokemonLocation.getPoint().toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return pokemonLocations;
    }
}

package pokemon.resource;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pokemon.Util.LocationUtil;
import pokemon.domain.Point;
import pokemon.domain.Quadrant;
import pokemon.dto.PokemonLocation;
import pokemon.service.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    @Produces("application/json")
    public Set<PokemonLocation> output() {
        if(!service.hasPokemonGo()){
            service.login();
        }
        Set<PokemonLocation> pokemonLocations = new HashSet<>();
        try {
            Point basePoint = new Point(39.089702, -77.047756);
            Set<CatchablePokemon> pokemons = service.getPokemon(basePoint);
            for(CatchablePokemon pokemon: pokemons){
                Point pokemonPoint = new Point(pokemon.getLatitude(), pokemon.getLongitude());
                PokemonLocation pokemonLocation = new PokemonLocation();
                pokemonLocation.setName(pokemon.getPokemonId().name());
                pokemonLocation.setPoint(pokemonPoint);
                pokemonLocation.setDirection(LocationUtil.getDirection(basePoint, pokemonPoint));
                pokemonLocation.setExpirationTimestamp(pokemon.getExpirationTimestampMs());
                pokemonLocation.setMetersAway(LocationUtil.getDistance(basePoint, pokemonPoint));
                pokemonLocations.add(pokemonLocation);
            }
            System.out.println("Number of Pokemon: " + pokemonLocations.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pokemonLocations;
    }
}

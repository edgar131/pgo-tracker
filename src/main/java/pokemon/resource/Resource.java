package pokemon.resource;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pokemon.Util.CatchablePokemonToPokemonLocationConverter;
import pokemon.Util.LocationUtil;
import pokemon.domain.Point;
import pokemon.domain.Quadrant;
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
    @Produces("application/json")
    public Set<PokemonLocation> output(@QueryParam("lat") Double latitude, @QueryParam("lon") Double longitude) {
        if(!service.hasPokemonGo()){
            service.login();
        }
        Set<PokemonLocation> pokemonLocations = new HashSet<>();
        try {
            Point basePoint = new Point(latitude, longitude);
            Set<CatchablePokemon> pokemons = service.getPokemon(basePoint);
            pokemonLocations = CatchablePokemonToPokemonLocationConverter.apply(pokemons, basePoint);
            /*for(PokemonLocation pokemonLocation: pokemonLocations){
                System.out.println(pokemonLocation.getPoint().toString());
            }*/
            System.out.println("Number of Pokemon: " + pokemonLocations.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pokemonLocations;
    }
}

package pokemon.Util;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import org.springframework.stereotype.Component;
import pokemon.domain.Point;
import pokemon.dto.PokemonLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class CatchablePokemonToPokemonLocationConverter {

    public static Set<PokemonLocation> apply(Set<CatchablePokemon> catchablePokemons, Point basePoint) {
        Set<PokemonLocation> pokemonLocations = new HashSet<>();
        for(CatchablePokemon pokemon: catchablePokemons){
            Point pokemonPoint = new Point(pokemon.getLatitude(), pokemon.getLongitude());
            PokemonLocation pokemonLocation = new PokemonLocation();
            pokemonLocation.setName(pokemon.getPokemonId().name());
            pokemonLocation.setPoint(pokemonPoint);
            pokemonLocation.setDirection(LocationUtil.getDirection(basePoint, pokemonPoint));
            pokemonLocation.setExpirationTimestamp(pokemon.getExpirationTimestampMs());
            pokemonLocation.setMetersAway(LocationUtil.getDistance(basePoint, pokemonPoint));
            pokemonLocations.add(pokemonLocation);
        }
        return pokemonLocations;
    }
}

package pokemon.service;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import pokemon.domain.Point;

import java.util.Set;

class MapWorker implements Runnable {

    private Point point;
    private Set<CatchablePokemon> catchablePokemon;
    private Service svc;

    public MapWorker(Service svc, Point point, Set<CatchablePokemon> catchablePokemon){
        this.point = point;
        this.catchablePokemon = catchablePokemon;
        this.svc = svc;
    }

    @Override
    public void run() {
        try {
            catchablePokemon.addAll(svc.getCatchablePokemonAtPoint(point));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addAllPokemon(Set<CatchablePokemon> catchablePokemon, Set<CatchablePokemon> toAdd){
        catchablePokemon.addAll(toAdd);
    }
}

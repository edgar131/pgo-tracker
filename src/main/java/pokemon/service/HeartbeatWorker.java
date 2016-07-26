package pokemon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pokemon.domain.Point;
import pokemon.dto.PokemonLocation;

import java.util.Date;
import java.util.Set;

public class HeartbeatWorker implements Runnable{

    private Service service;
    private Boolean running;
    private Set<PokemonLocation> pokemons;
    private Point basePoint;

    public HeartbeatWorker(Set<PokemonLocation> pokemons, Service service, Point basePoint){
        this.service = service;
        this.running = false;
        this.pokemons = pokemons;
        this.basePoint = basePoint;
    }

    @Override
    public void run() {
        running = true;
        try {
            while(running){
                System.out.println("Beginning Heartbeat");
                Set<PokemonLocation> newPokemon = service.heartbeat(basePoint);
                System.out.println(newPokemon.size() + " New Pokemon");
                pokemons.addAll(newPokemon);
                cullExpiredPokemon();
                /*DEBUG*/
                System.out.println(pokemons.size() + " Total Pokemon");
                System.out.println("Heartbeat Complete");
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            running = false;
            e.printStackTrace();
        }
    }

    private void cullExpiredPokemon(){
        Date currentTime = new Date();
        for(PokemonLocation pokemon: pokemons){
            if(pokemon.getExpirationTimestamp().compareTo(currentTime) < 0){
                pokemons.remove(pokemon);
            }
        }
    }
    void setRunning(Boolean running){
        this.running = running;
    }
}

package pokemon.service;

import com.google.common.collect.Sets;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.PtcLogin;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pokemon.Util.CatchablePokemonToPokemonLocationConverter;
import pokemon.domain.Point;
import pokemon.domain.Quadrant;
import pokemon.dto.PokemonLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;

@Component
public class Service {

    private PokemonGo pokemonGo;
    private static final Double DEGREE_DIFF = .0003;
    private static final Integer STEPS = 10;
    private static final Double ALTITUDE = 0.0;
    private String userName;
    private String password;

    @Autowired
    public Service(@Value("${userName}") String userName, @Value("${password}") String password){
        this.userName = userName;
        this.password = password;
    }

    public AuthInfo login(){
        OkHttpClient client = new OkHttpClient();
        PtcLogin ptcLogin = new PtcLogin(client);
        AuthInfo authInfo = null;
        try {
            authInfo = ptcLogin.login(userName, password);
            if(authInfo.hasToken()){
                this.pokemonGo = new PokemonGo(authInfo, client);
            }
        } catch (LoginFailedException e) {
            System.out.println("Login Failed (Check userName/Password)");
        } catch (RemoteServerException e) {
            System.out.println("Login Failed (PTC Server may be down)");
        }
        return authInfo;
    }

    public void beginProcess(Set<PokemonLocation> pokemons, Point basePoint) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new HeartbeatWorker(pokemons, this, basePoint));
    }

    Set<PokemonLocation> heartbeat(Point basePoint) throws Exception{
        Set<Point> points = getAllPoints(basePoint);
        Set<CatchablePokemon> pokemons = Sets.newConcurrentHashSet();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(Point point: points){
            Runnable worker = new MapWorker(this, point, pokemons);
            executorService.execute(worker);
        }
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        Set<PokemonLocation> pokemonLocations = CatchablePokemonToPokemonLocationConverter.apply(pokemons, basePoint);
        return pokemonLocations;
    }

    Set<CatchablePokemon> getCatchablePokemonAtPoint(Point point) throws Exception{
        pokemonGo.setLocation(point.getLatitude(), point.getLongitude(), ALTITUDE);
        Set<CatchablePokemon> pokemons = Sets.newConcurrentHashSet();
        Object[] pokemonArray = null;
        synchronized(this) {
            pokemonArray = pokemonGo.getMap().getCatchablePokemon().toArray();
        }
        if(pokemonArray != null && pokemonArray.length > 0){
            for(Object pokemon: pokemonArray){
                pokemons.add((CatchablePokemon)pokemon);
            }
        }
        return pokemons;
    }

    private Set<Point> getAllPoints(Point basePoint){
        Set<Point> allPoints = new HashSet<>();
        allPoints.addAll(getQuadrant(basePoint, Quadrant.NE));
        allPoints.addAll(getQuadrant(basePoint, Quadrant.NW));
        allPoints.addAll(getQuadrant(basePoint, Quadrant.SE));
        allPoints.addAll(getQuadrant(basePoint, Quadrant.SW));
        return allPoints;
    }

    private Set<Point> getQuadrant(Point basePoint, Quadrant quadrant) {
        HashSet<Point> points = new HashSet<>();
        for(int x = 0; x < STEPS; x++){
            for(int y = 0; y < STEPS; y++){
                points.add(new Point(
                        (basePoint.getLatitude() + (x*quadrant.getLatAdd()*DEGREE_DIFF)),
                        (basePoint.getLongitude() + (y*quadrant.getLonAdd()*DEGREE_DIFF)))
                );
            }
        }
        return points;
    }

}

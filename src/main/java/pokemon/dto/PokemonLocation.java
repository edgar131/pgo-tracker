package pokemon.dto;

import com.google.common.base.Objects;
import pokemon.domain.Point;

import java.util.Date;

public class PokemonLocation {
    private PokemonId pokemonId;
    private Date expirationTimestamp;
    private Point point;
    private String direction;
    private Double metersAway;

    public PokemonLocation() {}

    public PokemonId getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(PokemonId id) {
        this.pokemonId = id;
    }

    public Date getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(Date expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getMetersAway() {
        return metersAway;
    }

    public void setMetersAway(Double metersAway) {
        this.metersAway = metersAway;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonLocation that = (PokemonLocation) o;
        return Objects.equal(pokemonId, that.pokemonId) &&
                Objects.equal(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pokemonId, point);
    }
}

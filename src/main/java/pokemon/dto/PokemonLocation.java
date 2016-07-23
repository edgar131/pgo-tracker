package pokemon.dto;

import com.google.common.base.Objects;
import pokemon.domain.Point;

public class PokemonLocation {
    private String name;
    private Long expirationTimestamp;
    private Point point;
    private String direction;
    private Double metersAway;

    public PokemonLocation() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(Long expirationTimestamp) {
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
        return Objects.equal(name, that.name) &&
                Objects.equal(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, point);
    }
}

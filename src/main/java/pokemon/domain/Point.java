package pokemon.domain;

import com.google.common.base.Objects;

public class Point {
    private Double latitude;
    private Double longitude;

    public Point(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Objects.equal(latitude, point.latitude) &&
                Objects.equal(longitude, point.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(latitude, longitude);
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}

package pokemon.Util;

import pokemon.domain.Point;

public class LocationUtil {

    private static final String[] directions = new String[]{"E", "SE", "S", "SW", "W", "NW", "N", "NE"};
    public static Double getDistance(Point pa, Point pb) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(pb.getLatitude() - pa.getLatitude());
        Double lonDistance = Math.toRadians(pb.getLongitude() - pa.getLongitude());
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(pa.getLatitude())) * Math.cos(Math.toRadians(pb.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public static String getDirection(Point endpoint, Point startpoint) {

        Double degrees = Math.toDegrees(Math.atan2(
                endpoint.getLongitude() - startpoint.getLongitude(),
                endpoint.getLatitude() - startpoint.getLatitude()
        ) + 360) % 360;

        return directions[degrees.intValue()/45];
    }
}

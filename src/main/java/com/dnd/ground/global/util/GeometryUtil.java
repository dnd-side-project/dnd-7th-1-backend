package com.dnd.ground.global.util;

import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.global.exception.CommonException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * @description 위치 정보 관련 Geometry Util
 * @author 박찬호
 * @since 2023-02-18
 * @updated 1. MBR 계산을 위한 calculate 메소드 생성
 *          2. Matrix 엔티티에서 Point 자료형으로 데이터를 받기 위한 메소드 생성
 *          - 2023.02.18 박찬호
 */
public class GeometryUtil {
    private static final WKTReader wktReader = new WKTReader();

    public static Point coordinateToPoint(double latitude, double longitude) {
        Geometry read;
        try {
            read = wktReader.read(String.format("POINT(%s %s)", latitude, longitude));
        } catch (ParseException e) {
            throw new CommonException(ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        }
        return read.getInteriorPoint();
    }

    public static Location calculate(Double baseLatitude, Double baseLongitude, Double distance,
                                     Direction direction) {
        Double radianLatitude = toRadian(baseLatitude);
        Double radianLongitude = toRadian(baseLongitude);
        Double radianAngle = toRadian(direction.getBearing());
        Double distanceRadius = distance / 6371.01;

        Double latitude = Math.asin(sin(radianLatitude) * cos(distanceRadius) +
                cos(radianLatitude) * sin(distanceRadius) * cos(radianAngle));
        Double longitude = radianLongitude + Math.atan2(sin(radianAngle) * sin(distanceRadius) *
                cos(radianLatitude), cos(distanceRadius) - sin(radianLatitude) * sin(latitude));

        longitude = normalizeLongitude(longitude);
        return new Location(toDegree(latitude), toDegree(longitude));
    }
    private static Double toRadian(Double coordinate) {
        return coordinate * Math.PI / 180.0;
    }

    private static Double toDegree(Double coordinate) {
        return coordinate * 180.0 / Math.PI;
    }

    private static Double sin(Double coordinate) {
        return Math.sin(coordinate);
    }

    private static Double cos(Double coordinate) {
        return Math.cos(coordinate);
    }

    private static Double normalizeLongitude(Double longitude) {
        return (longitude + 540) % 360 - 180;
    }
}

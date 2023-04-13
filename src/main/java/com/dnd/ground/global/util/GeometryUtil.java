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
 * @updated 1.화면의 SpanDelta 값에 따라 MBR 계산하도록 변경
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

    public static Location calculate(Location center, Double spanDelta, Direction direction) {
        Double centerLatitude = center.getLatitude();
        Double centerLongitude = center.getLongitude();

        Double latitude = null;
        Double longitude = null;

        switch(direction) {
            case NORTHWEST:
                latitude = centerLatitude + spanDelta * 0.5;
                longitude = centerLongitude - spanDelta * 0.5;
                break;
            case NORTHEAST:
                latitude = centerLatitude + spanDelta * 0.5;
                longitude = centerLongitude + spanDelta * 0.5;
                break;
            case SOUTHEAST:
                latitude = centerLatitude - spanDelta * 0.5;
                longitude = centerLongitude + spanDelta * 0.5;
                break;
            case SOUTHWEST:
                latitude = centerLatitude - spanDelta * 0.5;
                longitude = centerLongitude - spanDelta * 0.5;
                break;
        }
        return new Location(latitude, longitude);
    }
}

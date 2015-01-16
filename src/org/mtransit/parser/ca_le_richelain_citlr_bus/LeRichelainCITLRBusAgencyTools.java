package org.mtransit.parser.ca_le_richelain_citlr_bus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// https://www.amt.qc.ca/en/about/open-data
// http://www.amt.qc.ca/xdata/citlr/google_transit.zip
public class LeRichelainCITLRBusAgencyTools extends DefaultAgencyTools {

	public static final String ROUTE_TYPE_FILTER = "3"; // bus only

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-le-richelain-citlr-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new LeRichelainCITLRBusAgencyTools().start(args);
	}

	@Override
	public void start(String[] args) {
		System.out.printf("Generating CITLR bus data...\n");
		long start = System.currentTimeMillis();
		super.start(args);
		System.out.printf("Generating CITLR bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		if (ROUTE_TYPE_FILTER != null && !gRoute.route_type.equals(ROUTE_TYPE_FILTER)) {
			return true;
		}
		return super.excludeRoute(gRoute);
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		routeLongName = MSpec.SAINT.matcher(routeLongName).replaceAll(MSpec.SAINT_REPLACEMENT);
		return MSpec.cleanLabel(routeLongName);
	}

	private static final Pattern CLEAN_TAXI = Pattern.compile("T\\-([\\d]+)", Pattern.CASE_INSENSITIVE);
	private static final String CLEAN_TAXI_REPLACEMENT = "T$1";

	@Override
	public String getRouteShortName(GRoute gRoute) {
		String routeShortName = gRoute.route_short_name;
		routeShortName = CLEAN_TAXI.matcher(routeShortName).replaceAll(CLEAN_TAXI_REPLACEMENT);
		return routeShortName;
	}

	private static final String ROUTE_COLOR = "009BC9";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if ("11".equals(gRoute.route_short_name)) return "FF7C80";
		if ("21".equals(gRoute.route_short_name)) return "B1A0C7";
		if ("22".equals(gRoute.route_short_name)) return "84582D";
		if ("23".equals(gRoute.route_short_name)) return "31869B";
		if ("28".equals(gRoute.route_short_name)) return "53A9FF";
		if ("29".equals(gRoute.route_short_name)) return "99CCFF";
		if ("31".equals(gRoute.route_short_name)) return "FFC000";
		if ("32".equals(gRoute.route_short_name)) return "C0504D";
		if ("33".equals(gRoute.route_short_name)) return "92D050";
		if ("38".equals(gRoute.route_short_name)) return "CCFFCC";
		if ("39".equals(gRoute.route_short_name)) return "FFFF99";
		if ("121".equals(gRoute.route_short_name)) return "8064A2";
		if ("122".equals(gRoute.route_short_name)) return "C4BD97";
		if ("123".equals(gRoute.route_short_name)) return "4BACC6";
		if ("124".equals(gRoute.route_short_name)) return "C4BD97";
		if ("132".equals(gRoute.route_short_name)) return "963634";
		if ("133".equals(gRoute.route_short_name)) return "7EC234";
		if ("321".equals(gRoute.route_short_name)) return "4BACC6";
		if ("323".equals(gRoute.route_short_name)) return "8064A2";
		if ("340".equals(gRoute.route_short_name)) return "FF7C80";
		if ("341".equals(gRoute.route_short_name)) return "FF6569";
		if ("343".equals(gRoute.route_short_name)) return "FF6569";
		if ("T-11".equals(gRoute.route_short_name)) return "FF5050";
		if ("T-12".equals(gRoute.route_short_name)) return "8064A2";
		if ("T-27".equals(gRoute.route_short_name)) return "808080";
		if ("T-28".equals(gRoute.route_short_name)) return "366092";
		if ("T-35".equals(gRoute.route_short_name)) return "D0504D";
		if ("T-36".equals(gRoute.route_short_name)) return "F79646";
		if ("T-51".equals(gRoute.route_short_name)) return "60497A";
		return ROUTE_COLOR;
	}

	private static final String ROUTE_TEXT_COLOR = "FFFFFF";

	@Override
	public String getRouteTextColor(GRoute gRoute) {
		return ROUTE_TEXT_COLOR;
	}

	@Override
	public void setTripHeadsign(MRoute route, MTrip mTrip, GTrip gTrip) {
		String stationName = cleanTripHeadsign(gTrip.trip_headsign);
		int directionId = Integer.valueOf(gTrip.direction_id);
		mTrip.setHeadsignString(stationName, directionId);
	}

	private static final Pattern DIRECTION = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);
	private static final String DIRECTION_REPLACEMENT = "";

	private static final Pattern SECTEUR = Pattern.compile("(secteur[s]? )", Pattern.CASE_INSENSITIVE);
	private static final String SECTEUR_REPLACEMENT = "";

	private static final Pattern SERVICE = Pattern.compile("(service) ([a|p]m)", Pattern.CASE_INSENSITIVE);
	private static final String SERVICE_REPLACEMENT = "$2";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = DIRECTION.matcher(tripHeadsign).replaceAll(DIRECTION_REPLACEMENT);
		tripHeadsign = SECTEUR.matcher(tripHeadsign).replaceAll(SECTEUR_REPLACEMENT);
		tripHeadsign = SERVICE.matcher(tripHeadsign).replaceAll(SERVICE_REPLACEMENT);
		return MSpec.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[] { START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE };

	private static final Pattern[] SPACE_FACES = new Pattern[] { SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE };

	private static final Pattern AVENUE = Pattern.compile("( avenue)", Pattern.CASE_INSENSITIVE);
	private static final String AVENUE_REPLACEMENT = " av.";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = AVENUE.matcher(gStopName).replaceAll(AVENUE_REPLACEMENT);
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, MSpec.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, MSpec.SPACE);
		return super.cleanStopNameFR(gStopName);
	}

	@Override
	public String getStopCode(GStop gStop) {
		if ("0".equals(gStop.stop_code)) {
			return null;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
		}
		// generating integer stop ID
		Matcher matcher = DIGITS.matcher(gStop.stop_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int stopId;
		if (gStop.stop_id.startsWith("CAN")) {
			stopId = 100000;
		} else {
			System.out.println("Stop doesn't have an ID (start with)! " + gStop);
			System.exit(-1);
			stopId = -1;
		}
		if (gStop.stop_id.endsWith("D")) {
			stopId += 4000;
		} else {
			System.out.println("Stop doesn't have an ID (end with)! " + gStop);
			System.exit(-1);
		}
		return stopId + digits;
	}
}

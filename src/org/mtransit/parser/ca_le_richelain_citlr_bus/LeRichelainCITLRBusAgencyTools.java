package org.mtransit.parser.ca_le_richelain_citlr_bus;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.mt.data.MTrip;

// https://www.amt.qc.ca/en/about/open-data
// http://www.amt.qc.ca/xdata/citlr/google_transit.zip
public class LeRichelainCITLRBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-le-richelain-citlr-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new LeRichelainCITLRBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating CITLR bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating CITLR bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final Pattern CLEAN_TAXI = Pattern.compile("T\\-([\\d]+)", Pattern.CASE_INSENSITIVE);
	private static final String CLEAN_TAXI_REPLACEMENT = "T$1";

	@Override
	public String getRouteShortName(GRoute gRoute) {
		String routeShortName = gRoute.route_short_name;
		routeShortName = CLEAN_TAXI.matcher(routeShortName).replaceAll(CLEAN_TAXI_REPLACEMENT);
		return routeShortName;
	}

	private static final String AGENCY_COLOR = "009BC9";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_60497A = "60497A";
	private static final String COLOR_F79646 = "F79646";
	private static final String COLOR_D0504D = "D0504D";
	private static final String COLOR_366092 = "366092";
	private static final String COLOR_808080 = "808080";
	private static final String COLOR_FF5050 = "FF5050";
	private static final String COLOR_FF6569 = "FF6569";
	private static final String COLOR_7EC234 = "7EC234";
	private static final String COLOR_963634 = "963634";
	private static final String COLOR_4BACC6 = "4BACC6";
	private static final String COLOR_C4BD97 = "C4BD97";
	private static final String COLOR_8064A2 = "8064A2";
	private static final String COLOR_FF9933 = "FF9933";
	private static final String COLOR_92D050 = "92D050";
	private static final String COLOR_C0504D = "C0504D";
	private static final String COLOR_FFC000 = "FFC000";
	private static final String COLOR_99CCFF = "99CCFF";
	private static final String COLOR_53A9FF = "53A9FF";
	private static final String COLOR_31869B = "31869B";
	private static final String COLOR_84582D = "84582D";
	private static final String COLOR_B1A0C7 = "B1A0C7";
	private static final String COLOR_FF7C80 = "FF7C80";

	private static final String RSN_11 = "11";
	private static final String RSN_21 = "21";
	private static final String RSN_22 = "22";
	private static final String RSN_23 = "23";
	private static final String RSN_28 = "28";
	private static final String RSN_29 = "29";
	private static final String RSN_31 = "31";
	private static final String RSN_32 = "32";
	private static final String RSN_33 = "33";
	private static final String RSN_38 = "38";
	private static final String RSN_39 = "39";
	private static final String RSN_121 = "121";
	private static final String RSN_122 = "122";
	private static final String RSN_123 = "123";
	private static final String RSN_124 = "124";
	private static final String RSN_132 = "132";
	private static final String RSN_133 = "133";
	private static final String RSN_321 = "321";
	private static final String RSN_323 = "323";
	private static final String RSN_340 = "340";
	private static final String RSN_341 = "341";
	private static final String RSN_343 = "343";
	private static final String RSN_T_11 = "T-11";
	private static final String RSN_T_12 = "T-12";
	private static final String RSN_T_27 = "T-27";
	private static final String RSN_T_28 = "T-28";
	private static final String RSN_T_35 = "T-35";
	private static final String RSN_T_36 = "T-36";
	private static final String RSN_T_51 = "T-51";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (RSN_11.equals(gRoute.route_short_name)) return COLOR_FF7C80;
		if (RSN_21.equals(gRoute.route_short_name)) return COLOR_B1A0C7;
		if (RSN_22.equals(gRoute.route_short_name)) return COLOR_84582D;
		if (RSN_23.equals(gRoute.route_short_name)) return COLOR_31869B;
		if (RSN_28.equals(gRoute.route_short_name)) return COLOR_53A9FF;
		if (RSN_29.equals(gRoute.route_short_name)) return COLOR_99CCFF;
		if (RSN_31.equals(gRoute.route_short_name)) return COLOR_FFC000;
		if (RSN_32.equals(gRoute.route_short_name)) return COLOR_C0504D;
		if (RSN_33.equals(gRoute.route_short_name)) return COLOR_92D050;
		if (RSN_38.equals(gRoute.route_short_name)) return COLOR_FF9933;
		if (RSN_39.equals(gRoute.route_short_name)) return COLOR_FF9933;
		if (RSN_121.equals(gRoute.route_short_name)) return COLOR_8064A2;
		if (RSN_122.equals(gRoute.route_short_name)) return COLOR_C4BD97;
		if (RSN_123.equals(gRoute.route_short_name)) return COLOR_4BACC6;
		if (RSN_124.equals(gRoute.route_short_name)) return COLOR_C4BD97;
		if (RSN_132.equals(gRoute.route_short_name)) return COLOR_963634;
		if (RSN_133.equals(gRoute.route_short_name)) return COLOR_7EC234;
		if (RSN_321.equals(gRoute.route_short_name)) return COLOR_4BACC6;
		if (RSN_323.equals(gRoute.route_short_name)) return COLOR_8064A2;
		if (RSN_340.equals(gRoute.route_short_name)) return COLOR_FF7C80;
		if (RSN_341.equals(gRoute.route_short_name)) return COLOR_FF6569;
		if (RSN_343.equals(gRoute.route_short_name)) return COLOR_FF6569;
		if (RSN_T_11.equals(gRoute.route_short_name)) return COLOR_FF5050;
		if (RSN_T_12.equals(gRoute.route_short_name)) return COLOR_8064A2;
		if (RSN_T_27.equals(gRoute.route_short_name)) return COLOR_808080;
		if (RSN_T_28.equals(gRoute.route_short_name)) return COLOR_366092;
		if (RSN_T_35.equals(gRoute.route_short_name)) return COLOR_D0504D;
		if (RSN_T_36.equals(gRoute.route_short_name)) return COLOR_F79646;
		if (RSN_T_51.equals(gRoute.route_short_name)) return COLOR_60497A;
		System.out.println("Unexpected route color " + gRoute);
		System.exit(-1);
		return null;
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.trip_headsign), gTrip.direction_id);
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
		return CleanUtils.cleanLabelFR(tripHeadsign);
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
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, CleanUtils.SPACE);
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

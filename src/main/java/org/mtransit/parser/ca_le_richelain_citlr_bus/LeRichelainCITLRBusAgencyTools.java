package org.mtransit.parser.ca_le_richelain_citlr_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.RegexUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mtransit.commons.Constants.SPACE_;
import static org.mtransit.commons.StringUtils.EMPTY;

// https://exo.quebec/en/about/open-data
// https://exo.quebec/xdata/citlr/google_transit.zip
public class LeRichelainCITLRBusAgencyTools extends DefaultAgencyTools {

	public static void main(@Nullable String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-le-richelain-citlr-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new LeRichelainCITLRBusAgencyTools().start(args);
	}

	@Nullable
	private HashSet<Integer> serviceIdInts;

	@Override
	public void start(@NotNull String[] args) {
		MTLog.log("Generating CITLR bus data...");
		long start = System.currentTimeMillis();
		this.serviceIdInts = extractUsefulServiceIdInts(args, this, true);
		super.start(args);
		MTLog.log("Generating CITLR bus data... DONE in %s.", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIdInts != null && this.serviceIdInts.isEmpty();
	}

	@Override
	public boolean excludeCalendar(@NotNull GCalendar gCalendar) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarInt(gCalendar, this.serviceIdInts);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(@NotNull GCalendarDate gCalendarDates) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarDateInt(gCalendarDates, this.serviceIdInts);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if (this.serviceIdInts != null) {
			return excludeUselessTripInt(gTrip, this.serviceIdInts);
		}
		return super.excludeTrip(gTrip);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final Pattern CLEAN_TAXI = Pattern.compile("T-([\\d]+)", Pattern.CASE_INSENSITIVE);
	private static final String CLEAN_TAXI_REPLACEMENT = "T$1";

	@Nullable
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		String routeShortName = gRoute.getRouteShortName();
		routeShortName = CLEAN_TAXI.matcher(routeShortName).replaceAll(CLEAN_TAXI_REPLACEMENT);
		return routeShortName;
	}

	private static final String T = "T";

	private static final long RID_STARTS_WITH_T = 20_000L;

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		if (!CharUtils.isDigitsOnly(gRoute.getRouteShortName())) {
			Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
			if (matcher.find()) {
				int digits = Integer.parseInt(matcher.group());
				if (gRoute.getRouteShortName().startsWith(T)) {
					return RID_STARTS_WITH_T + digits;
				}
			}
			throw new MTLog.Fatal("Unexpected route ID for %s!", gRoute);
		}
		return Long.parseLong(gRoute.getRouteShortName());
	}

	private static final String AGENCY_COLOR = "1F1F1F"; // DARK GRAY (from GTFS)

	@NotNull
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
	private static final String RSN_T_22 = "T-22";
	private static final String RSN_T_27 = "T-27";
	private static final String RSN_T_25 = "T-25";
	private static final String RSN_T_28 = "T-28";
	private static final String RSN_T_35 = "T-35";
	private static final String RSN_T_36 = "T-36";
	private static final String RSN_T_37 = "T-37";
	private static final String RSN_T_51 = "T-51";

	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			if (RSN_11.equals(gRoute.getRouteShortName())) return COLOR_FF7C80;
			if (RSN_21.equals(gRoute.getRouteShortName())) return COLOR_B1A0C7;
			if (RSN_22.equals(gRoute.getRouteShortName())) return COLOR_84582D;
			if (RSN_23.equals(gRoute.getRouteShortName())) return COLOR_31869B;
			if (RSN_28.equals(gRoute.getRouteShortName())) return COLOR_53A9FF;
			if (RSN_29.equals(gRoute.getRouteShortName())) return COLOR_99CCFF;
			if (RSN_31.equals(gRoute.getRouteShortName())) return COLOR_FFC000;
			if (RSN_32.equals(gRoute.getRouteShortName())) return COLOR_C0504D;
			if (RSN_33.equals(gRoute.getRouteShortName())) return COLOR_92D050;
			if (RSN_38.equals(gRoute.getRouteShortName())) return COLOR_FF9933;
			if (RSN_39.equals(gRoute.getRouteShortName())) return COLOR_FF9933;
			if (RSN_121.equals(gRoute.getRouteShortName())) return COLOR_8064A2;
			if (RSN_122.equals(gRoute.getRouteShortName())) return COLOR_C4BD97;
			if (RSN_123.equals(gRoute.getRouteShortName())) return COLOR_4BACC6;
			if (RSN_124.equals(gRoute.getRouteShortName())) return COLOR_C4BD97;
			if (RSN_132.equals(gRoute.getRouteShortName())) return COLOR_963634;
			if (RSN_133.equals(gRoute.getRouteShortName())) return COLOR_7EC234;
			if (RSN_321.equals(gRoute.getRouteShortName())) return COLOR_4BACC6;
			if (RSN_323.equals(gRoute.getRouteShortName())) return COLOR_8064A2;
			if (RSN_340.equals(gRoute.getRouteShortName())) return COLOR_FF7C80;
			if (RSN_341.equals(gRoute.getRouteShortName())) return COLOR_FF6569;
			if (RSN_343.equals(gRoute.getRouteShortName())) return COLOR_FF6569;
			if (RSN_T_11.equals(gRoute.getRouteShortName())) return COLOR_FF5050;
			if (RSN_T_12.equals(gRoute.getRouteShortName())) return COLOR_8064A2;
			if (RSN_T_22.equals(gRoute.getRouteShortName())) return "305496";
			if (RSN_T_25.equals(gRoute.getRouteShortName())) return "1F497C";
			if (RSN_T_27.equals(gRoute.getRouteShortName())) return COLOR_808080;
			if (RSN_T_28.equals(gRoute.getRouteShortName())) return COLOR_366092;
			if (RSN_T_35.equals(gRoute.getRouteShortName())) return COLOR_D0504D;
			if (RSN_T_36.equals(gRoute.getRouteShortName())) return COLOR_F79646;
			if (RSN_T_37.equals(gRoute.getRouteShortName())) return "FF9A00";
			if (RSN_T_51.equals(gRoute.getRouteShortName())) return COLOR_60497A;
			throw new MTLog.Fatal("Unexpected route color for %s!", gRoute);
		}
		return super.getRouteColor(gRoute);
	}

	private static final HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;

	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<>();
		//noinspection deprecation
		map2.put(340L, new RouteTripSpec(340L, //
				0, MTrip.HEADSIGN_TYPE_STRING, "Term Longueuil", //
				1, MTrip.HEADSIGN_TYPE_STRING, "P+R Candiac") //
				.addTripSort(0, //
						Arrays.asList(
								"75642", // P+R Candiac <=
								"75399", // ++
								"75181", // ++
								"75647",  // P+R La Prairie
								"75040", // ++
								"75038", // ++
								"75030" // Terminus Longueuil ==>
						)) //
				.addTripSort(1, //
						Arrays.asList(
								"75030", // Terminus Longueuil <=
								"75037", // ++
								"75039", // ++
								"75647", // P+R La Prairie
								"75180", // ++
								"75104", // ++
								"75642" // P+R Candiac =>
						)) //
				.compileBothTripSort());

		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, @NotNull List<MTripStop> list1, @NotNull List<MTripStop> list2, @NotNull MTripStop ts1, @NotNull MTripStop ts2, @NotNull GStop ts1GStop, @NotNull GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@NotNull
	@Override
	public ArrayList<MTrip> splitTrip(@NotNull MRoute mRoute, @Nullable GTrip gTrip, @NotNull GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@NotNull
	@Override
	public Pair<Long[], Integer[]> splitTripStop(@NotNull MRoute mRoute, @NotNull GTrip gTrip, @NotNull GTripStop gTripStop, @NotNull ArrayList<MTrip> splitTrips, @NotNull GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(@NotNull MRoute mRoute, @NotNull MTrip mTrip, @NotNull GTrip gTrip, @NotNull GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		mTrip.setHeadsignString(
				cleanTripHeadsign(gTrip.getTripHeadsignOrDefault()),
				gTrip.getDirectionIdOrDefault()
		);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@Override
	public boolean directionFinderEnabled(long routeId, @NotNull GRoute gRoute) {
		if (routeId == 340L) {
			return false; // 2 direction_id w/ same head-sign & last stop & !AM/PM (should be 1 direction_id?)
		}
		return super.directionFinderEnabled(routeId, gRoute);
	}

	@NotNull
	@Override
	public String cleanDirectionHeadsign(boolean fromStopName, @NotNull String directionHeadSign) {
		if (!fromStopName) {
			final String directionHeadSignLC = directionHeadSign.toLowerCase(Locale.FRENCH);
			if (directionHeadSignLC.endsWith(" am")) {
				return "AM";
			} else if (directionHeadSignLC.endsWith(" pm")) {
				return "PM";
			}
		}
		return super.cleanDirectionHeadsign(fromStopName, directionHeadSign);
	}

	private static final Pattern EXPRESS_ = CleanUtils.cleanWordsFR("express");

	private static final Pattern _DASH_ = Pattern.compile("( - | – )");
	private static final String _DASH_REPLACEMENT = "<>"; // form<>to

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToFR(tripHeadsign);
		tripHeadsign = _DASH_.matcher(tripHeadsign).replaceAll(_DASH_REPLACEMENT); // from - to => form<>to
		tripHeadsign = EXPRESS_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanBounds(Locale.FRENCH, tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		throw new MTLog.Fatal("Unexpected trips to merge %s & %s!", mTrip, mTripToMerge);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[]{START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE};

	private static final Pattern[] SPACE_FACES = new Pattern[]{SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE};

	private static final Pattern DEVANT_ = CleanUtils.cleanWordsFR("devant");

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = _DASH_.matcher(gStopName).replaceAll(SPACE_);
		gStopName = DEVANT_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = RegexUtils.replaceAll(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = RegexUtils.replaceAll(gStopName, SPACE_FACES, CleanUtils.SPACE);
		gStopName = CleanUtils.cleanBounds(Locale.FRENCH, gStopName);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if ("0".equals(gStop.getStopCode())) {
			return null;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public int getStopId(@NotNull GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		//noinspection deprecation
		final String stopId1 = gStop.getStopId();
		Matcher matcher = DIGITS.matcher(stopId1);
		if (matcher.find()) {
			int digits = Integer.parseInt(matcher.group());
			int stopId;
			if (stopId1.startsWith("CAN")) {
				stopId = 100_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (start with)! 5s", gStop);
			}
			if (stopId1.endsWith("D")) {
				stopId += 4_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (end with)! %s", gStop);
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}
}

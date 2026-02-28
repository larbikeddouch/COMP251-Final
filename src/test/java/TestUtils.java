import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
    static McMetro bestMetroBuilder(String tracksString , int seed) {
        Random rand = new Random(seed);

        String[] tsArray = tracksString
                .replace(" ", "")
                .replace("[", "")
                .split("]");
        Track[] tracks = new Track[tsArray.length];
        HashMap<BuildingID, Building> buildings = new HashMap<>();

        for (int i = 0; i < tsArray.length; i++) {
            String[] curTs = tsArray[i].split(",");
            BuildingID b1Id = new BuildingID(Integer.parseInt(curTs[0]));
            BuildingID b2Id = new BuildingID(Integer.parseInt(curTs[1]));
            int goodness = Integer.parseInt(curTs[2]);
            int min_cap = Integer.MAX_VALUE;
            Building b1 = null;
            Building b2 = null;


            if (buildings.containsKey(b1Id)) {
                b1 = buildings.get(b1Id);
                min_cap = b1.occupants();
            }
            if (buildings.containsKey(b2Id)) {
                b2 = buildings.get(b2Id);
                min_cap = b2.occupants();
            }

            int cost;
            int cap;
            if (min_cap != Integer.MAX_VALUE) { // One of the buildings has already been created
                // Calculate values for track and other building
                cost = min_cap/goodness;
                assert(cost > 0);

                cap = min_cap + rand.nextInt(0, 50);
                int otherOccupants = min_cap + rand.nextInt(0, 50);

                if (b1 != null && b2 != null) { // Both buildings exist
                    // Recalculate cost and cap
                    min_cap = Math.min(b1.occupants(), b2.occupants());
                    cost = min_cap/goodness;
                    cap = min_cap + rand.nextInt(0, 50);
                } else if (b1 != null) { // Start building exists
                    b2 = new Building(b2Id, otherOccupants);
                } else { // End building exists
                    b1 = new Building(b1Id, otherOccupants);
                }
            } else {
                cost = rand.nextInt(10,50);

                int governingCapacity = goodness * cost;

                int[] capacities = {
                        governingCapacity + rand.nextInt(0, 50),
                        governingCapacity + rand.nextInt(0, 50),
                        governingCapacity + rand.nextInt(0, 50),
                };
                int governingIndex = rand.nextInt(0, 50) % 3;
                capacities[governingIndex] = governingCapacity;
                b1 = new Building(b1Id, capacities[0]);
                b2 = new Building(b2Id, capacities[1]);
                cap = capacities[2];
            }


            int genGoodness = Math.min(Math.min(b1.occupants(), b2.occupants()), cap)/cost;

            //assertTrue(Math.abs(genGoodness - goodness) <= 10);

            // Save track
            Track curTrack = new Track(
                    new TrackID(i),
                    b1.id(),
                    b2.id(),
                    cost,
                    cap
            );
            tracks[i] = curTrack;

            // Save Buildings
            buildings.putIfAbsent(b1.id(), b1);
            buildings.putIfAbsent(b2.id(), b2);
        }

        return new McMetro(tracks, buildings.values().toArray(new Building[0]));
    }

    static McMetro maxPassengersBuilder(String tracksString) {

        String[] tsArray = tracksString.replace(" ", "").replace("[", "").split("]");
        Track[] tracks = new Track[tsArray.length];
        HashMap<BuildingID, Building> buildings = new HashMap<>();

        for (int i = 0; i < tsArray.length; i++) {
            String[] curTs = tsArray[i].split(",");

            BuildingID b1Id = new BuildingID(Integer.parseInt(curTs[0]));
            BuildingID b2Id = new BuildingID(Integer.parseInt(curTs[1]));
            int goalCap = Integer.parseInt(curTs[2]);

            buildings.computeIfAbsent(b1Id, x -> new Building(b1Id, Integer.MAX_VALUE));
            buildings.computeIfAbsent(b2Id, x -> new Building(b2Id, Integer.MAX_VALUE));
            Building b1 = buildings.get(b1Id);
            Building b2 = buildings.get(b2Id);

            // Save track
            Track curTrack = new Track(
                    new TrackID(i),
                    b1.id(),
                    b2.id(),
                    1,
                    goalCap
            );
            tracks[i] = curTrack;

            // Save Buildings
            buildings.putIfAbsent(b1.id(), b1);
            buildings.putIfAbsent(b2.id(), b2);
        }
        return new McMetro(tracks, buildings.values().toArray(new Building[0]));
    }

    static void checkPassengerSearch(String[] passengers, String[] expected, String firstLetters) {
        McMetro mcMetro = bestMetroBuilder("1,2,1", 1);
        mcMetro.addPassengers(passengers);
        String[] matches = mcMetro.searchForPassengers(firstLetters).toArray(new String[0]);
        assertArrayEquals(expected, matches);
    }

    static void trackIdsEqual(int[] actual, TrackID[] tarr) {
        HashSet<TrackID> hs1 = new HashSet<>();
        for (int i : actual) {
            hs1.add(new TrackID(i));
        }
        HashSet<TrackID> hs2 = new HashSet<>(Arrays.asList(tarr));
        assertEquals(hs1, hs2);
    }

    static void testHiring(String intervalsString, int hireable) {
        String[] intervals = intervalsString
                .replace(" ", "")
                .replace("[", "")
                .split("]");

        int[][] schedule = new int[intervals.length][2];
        for (int i = 0; i < intervals.length; i++) {
            String[] cur_interval = intervals[i].split(",");
            int start = Integer.parseInt(cur_interval[0]);
            int end = Integer.parseInt(cur_interval[1]);
            schedule[i][0] = start;
            schedule[i][1] = end;
        }
        int out = McMetro.hireTicketCheckers(schedule);
        assertEquals(hireable, out);
    }
}

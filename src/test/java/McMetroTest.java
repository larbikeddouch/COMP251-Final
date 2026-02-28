import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class McMetroTest {
    @Test // 2 buildings & 1 track
    void testMaxPassengers() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 50)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid2);
        assertEquals(50, maxPassengers);
    }

    @Test // Complicated graph; Fixed building occupants & Regular max flow problem
    void testMaxPassengers1() {
        // Buildings
        BuildingID b1 = new BuildingID(1);
        BuildingID b2 = new BuildingID(2);
        BuildingID b3 = new BuildingID(3);
        BuildingID b4 = new BuildingID(4);
        BuildingID b5 = new BuildingID(5);
        BuildingID b6 = new BuildingID(6);

        Building[] buildings = new Building[]{
                new Building(b1, 10),
                new Building(b2, 10),
                new Building(b3, 10),
                new Building(b4, 10),
                new Building(b5, 10),
                new Building(b6, 10)
        };

        // Tracks
        Track[] tracks = new Track[]{
                new Track(new TrackID(1), b1, b2, 5, 3),
                new Track(new TrackID(2), b1, b4, 5, 4),
                new Track(new TrackID(3), b2, b4, 5, 2),
                new Track(new TrackID(4), b2, b3, 5, 1),
                new Track(new TrackID(5), b4, b3, 5, 4),
                new Track(new TrackID(6), b4, b5, 5, 2),
                new Track(new TrackID(7), b3, b6, 5, 3),
                new Track(new TrackID(8), b5, b6, 5, 2)
        };
        McMetro metro = new McMetro(tracks, buildings);
        int maxPassengers = metro.maxPassengers(b1, b6);
        assertEquals(5, maxPassengers);
    }

    @Test
    void testMaxPassengers2() {
        // random graph
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 400)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid2, bid3, 100, 50),
                new Track(new TrackID(3), bid3, bid4, 100, 100),
                new Track(new TrackID(4), bid1, bid3, 100, 25),
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid4);
        assertEquals(75, maxPassengers);
    }

    @Test
    void testMaxPassengers3() {
        // graph with loop
        // random graph
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 400)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid2, bid3, 100, 50),
                new Track(new TrackID(3), bid3, bid4, 100, 100),
                new Track(new TrackID(4), bid3, bid2, 100, 25),
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid4);
        assertEquals(50, maxPassengers);
    }

    @Test
    void testMaxPassengers4() {
        // test disconnected graph
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid3);
        assertEquals(0, maxPassengers);
    }

    @Test
    void testMaxPassengers5() {
        // test disconnected graph
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid2, bid3, 100, 100)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid3, bid1);
        assertEquals(0, maxPassengers);
    }

    @Test // No tracks
    void testMaxPassengers6() {
        // test disconnected graph
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300)
        };

        Track[] tracks = new Track[]{};

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid3);
        assertEquals(0, maxPassengers);
    }

    @Test
    void testMaxPassengers7() {
        // weird loop
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 100),
                new Building(bid3, 100)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid2, bid3, 100, 100),
                new Track(new TrackID(3), bid3, bid1, 100, 100),

                // other loop
                new Track(new TrackID(4), bid2, bid1, 100, 100),
                new Track(new TrackID(5), bid3, bid2, 100, 100),
                new Track(new TrackID(6), bid1, bid3, 100, 100)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid3);
        assertEquals(200, maxPassengers);
    }

    @Test
    void testMaxPassengers8() {
        // only 1 building
        BuildingID bid1 = new BuildingID(1);
        Building[] buildings = new Building[]{
                new Building(bid1, 100)
        };

        Track[] tracks = new Track[]{};

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid1);
        assertEquals(0, maxPassengers);
    }

    @Test
    void testMaxPassengers9() {
        // only 1 building
        BuildingID bid1 = new BuildingID(1);
        Building[] buildings = new Building[]{
                new Building(bid1, 100)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid1, 100, 100)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid1);
        assertEquals(100, maxPassengers);
    }

    @Test
    void testMaxPassengers10() {
        // long path, but start building has 0 occupants
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);
        BuildingID bid7 = new BuildingID(7);
        BuildingID bid8 = new BuildingID(8);
        BuildingID bid9 = new BuildingID(9);

        Building[] buildings = new Building[]{
                new Building(bid1, 0),
                new Building(bid2, 100),
                new Building(bid3, 200),
                new Building(bid4, 300),
                new Building(bid5, 400),
                new Building(bid6, 500),
                new Building(bid7, 600),
                new Building(bid8, 700),
                new Building(bid9, 800)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid2, bid3, 100, 100),
                new Track(new TrackID(3), bid3, bid4, 100, 100),
                new Track(new TrackID(4), bid4, bid5, 100, 100),
                new Track(new TrackID(5), bid5, bid6, 100, 100),
                new Track(new TrackID(6), bid6, bid7, 100, 100),
                new Track(new TrackID(7), bid7, bid8, 100, 100),
                new Track(new TrackID(8), bid8, bid9, 100, 100)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid9);
        assertEquals(0, maxPassengers);
    }
/*
    @Test
    void testMaxPassengers11() {
        // long path, but end building has 0 occupants
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);
        BuildingID bid7 = new BuildingID(7);
        BuildingID bid8 = new BuildingID(8);
        BuildingID bid9 = new BuildingID(9);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 400),
                new Building(bid5, 500),
                new Building(bid6, 600),
                new Building(bid7, 700),
                new Building(bid8, 800),
                new Building(bid9, 0)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid2, bid3, 100, 100),
                new Track(new TrackID(3), bid3, bid4, 100, 100),
                new Track(new TrackID(4), bid4, bid5, 100, 100),
                new Track(new TrackID(5), bid5, bid6, 100, 100),
                new Track(new TrackID(6), bid6, bid7, 100, 100),
                new Track(new TrackID(7), bid7, bid8, 100, 100),
                new Track(new TrackID(8), bid8, bid9, 100, 100)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid9);
        assertEquals(0, maxPassengers);
    }

    @Test
    void testMaxPassengers12() {
        // 6 buildings
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 400),
                new Building(bid5, 500),
                new Building(bid6, 600)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid3, bid4, 100, 100),
                new Track(new TrackID(3), bid4, bid5, 100, 100),
                new Track(new TrackID(4), bid5, bid1, 100, 100),
                new Track(new TrackID(5), bid3, bid6, 100, 25),
                new Track(new TrackID(6), bid6, bid4, 100, 100),
                new Track(new TrackID(7), bid2, bid3, 100, 100),
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid6);
        assertEquals(25, maxPassengers);
    }
*/
    @Test
    void testMaxPassengers13() {
        // 6 buildings
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 100),
                new Building(bid3, 100),
                new Building(bid4, 100),
                new Building(bid5, 100),
                new Building(bid6, 100)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid3, bid4, 100, 25),
                new Track(new TrackID(3), bid4, bid5, 100, 100),
                new Track(new TrackID(4), bid5, bid1, 100, 100),
                new Track(new TrackID(5), bid3, bid6, 100, 25),
                new Track(new TrackID(6), bid6, bid4, 100, 100),
                new Track(new TrackID(7), bid2, bid3, 100, 100),
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid4);
        assertEquals(50, maxPassengers);
    }

    @Test
    void testMaxPassengers14() {
        // 6 buildings
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 100),
                new Building(bid3, 100),
                new Building(bid4, 100),
                new Building(bid5, 100),
                new Building(bid6, 100)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid3, bid4, 100, 25),
                new Track(new TrackID(3), bid4, bid5, 100, 100),
                new Track(new TrackID(4), bid5, bid1, 100, 100),
                new Track(new TrackID(5), bid3, bid6, 100, 25),
                new Track(new TrackID(6), bid6, bid4, 100, 100),
                new Track(new TrackID(7), bid2, bid3, 100, 100),
                new Track(new TrackID(8), bid2, bid4, 100, 75),
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid4);
        assertEquals(100, maxPassengers);
    }

    @Test
    void testMaxPassengers15() {
        // 6 buildings
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 100),
                new Building(bid3, 100),
                new Building(bid4, 100),
                new Building(bid5, 100),
                new Building(bid6, 100)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid3, bid4, 100, 25),
                new Track(new TrackID(3), bid4, bid5, 100, 100),
                new Track(new TrackID(4), bid5, bid1, 100, 100),
                new Track(new TrackID(5), bid3, bid6, 100, 25),
                new Track(new TrackID(6), bid6, bid4, 100, 100),
                new Track(new TrackID(7), bid2, bid3, 100, 100),
                new Track(new TrackID(8), bid2, bid4, 100, 75),
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid4);
        assertEquals(100, maxPassengers);
    }

    @Test
    void testMaxPassengers16() {
        // multiple tracks from same building 1 to 2
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 100),
                new Track(new TrackID(2), bid1, bid2, 100, 50),
                new Track(new TrackID(3), bid1, bid2, 100, 25),
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid2);
        assertEquals(175, maxPassengers);
    }

    @Test
    void testMaxPassengers_disconnectedPath() { // with single odd loop
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 100),
                new Building(bid5, 200) //disconnected building
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 50),
                new Track(new TrackID(2), bid2, bid4, 100, 50),
                new Track(new TrackID(3), bid3, bid1, 100, 60),
                new Track(new TrackID(4), bid1, bid3, 100, 40),
                new Track(new TrackID(5), bid2, bid3, 100, 60),
                new Track(new TrackID(6), bid3, bid2, 100, 30)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid4);
        assertEquals(50, maxPassengers);
    }

    @Test
    void testMaxPassengers_noCapacity() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 0),
                new Track(new TrackID(2), bid2, bid3, 100, 50)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid3);
        assertEquals(0, maxPassengers);
    }

    @Test
    void testMaxPassengers_parallelPaths() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 30),
                new Track(new TrackID(2), bid2, bid3, 100, 50),
                new Track(new TrackID(3), bid1, bid3, 100, 40)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid3);
        assertEquals(70, maxPassengers);
    }

    @Test
    void testMaxPassengers_simplecyclePath() { // taken from https://brilliant.org/wiki/flow-network/
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 300),
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 2),
                new Track(new TrackID(2), bid1, bid3, 100, 4),
                new Track(new TrackID(3), bid2, bid3, 100, 3),
                new Track(new TrackID(4), bid3, bid2, 100, 3),
                new Track(new TrackID(5), bid2, bid4, 100, 4),
                new Track(new TrackID(6), bid3, bid4, 100, 3)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid4);
        assertEquals(6, maxPassengers);
    }

    @Test
    void testMaxPassengers_complexcyclePath() { // taken from https://brilliant.org/wiki/flow-network/
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 300),
                new Building(bid5, 300),
                new Building(bid6, 300),
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 16),
                new Track(new TrackID(2), bid1, bid3, 100, 13),
                new Track(new TrackID(3), bid2, bid3, 100, 10),
                new Track(new TrackID(4), bid3, bid2, 100, 4),
                new Track(new TrackID(5), bid2, bid4, 100, 12),
                new Track(new TrackID(6), bid3, bid5, 100, 14),
                new Track(new TrackID(7), bid4, bid3, 100, 9),
                new Track(new TrackID(8), bid5, bid4, 100, 7),
                new Track(new TrackID(9), bid4, bid6, 100, 20),
                new Track(new TrackID(10), bid5, bid6, 100, 4)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid6);
        assertEquals(23, maxPassengers);
    }

    @Test
    void testMaxPassengers_complexcyclePath2() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);
        BuildingID bid7 = new BuildingID(7);
        BuildingID bid8 = new BuildingID(8);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 300),
                new Building(bid5, 300),
                new Building(bid6, 300),
                new Building(bid7, 300),
                new Building(bid8, 300),
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 6),
                new Track(new TrackID(2), bid1, bid3, 100, 6),
                new Track(new TrackID(3), bid3, bid2, 100, 5),
                new Track(new TrackID(4), bid2, bid4, 100, 4),
                new Track(new TrackID(5), bid2, bid5, 100, 2),
                new Track(new TrackID(6), bid3, bid5, 100, 9),
                new Track(new TrackID(7), bid4, bid6, 100, 4),
                new Track(new TrackID(8), bid4, bid7, 100, 7),
                new Track(new TrackID(9), bid5, bid4, 100, 8),
                new Track(new TrackID(10), bid5, bid7, 100, 7),
                new Track(new TrackID(11), bid6, bid8, 100, 7),
                new Track(new TrackID(12), bid7, bid6, 100, 11),
                new Track(new TrackID(13), bid7, bid8, 100, 4)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid8);
        assertEquals(11, maxPassengers);
    }

    @Test // Simple
    void testBestMetro() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200)
        };

        TrackID tid = new TrackID(1);
        Track[] tracks = new Track[]{
                new Track(tid, bid1, bid2, 100, 50)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[] expected = new TrackID[]{tid};
        assertArrayEquals(expected, bms);
    }

    @Test
    void testBestMetro2() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300)
        };

        TrackID tid1 = new TrackID(1);
        TrackID tid2 = new TrackID(2);
        TrackID tid3 = new TrackID(3);
        Track[] tracks = new Track[]{
                new Track(tid1, bid1, bid2, 1, 100),
                new Track(tid2, bid2, bid3, 1, 200),
                new Track(tid3, bid1, bid3, 1, 300)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[][] expected = {
                new TrackID[]{tid2, tid3},
                new TrackID[]{tid3, tid2},
                new TrackID[]{tid2, tid1},
                new TrackID[]{tid1, tid2},
        };
        assertTrue(Arrays.equals(expected[0], bms) || Arrays.equals(expected[1], bms) || Arrays.equals(expected[2], bms) || Arrays.equals(expected[3], bms));
    }

    @Test
    void testBestMetro3() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300),
                new Building(bid4, 400)
        };

        TrackID tid = new TrackID(1);
        TrackID tid2 = new TrackID(2);
        TrackID tid3 = new TrackID(3);
        TrackID tid4 = new TrackID(4);
        Track[] tracks = new Track[]{
                new Track(tid, bid1, bid2, 1, 100),
                new Track(tid2, bid2, bid3, 1, 200),
                new Track(tid3, bid1, bid3, 1, 300),
                new Track(tid4, bid3, bid4, 1, 400)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[][] expected = {
                new TrackID[]{tid2, tid3, tid4},
                new TrackID[]{tid2, tid, tid4},
        };

        Arrays.sort(bms);
        Arrays.sort(expected[0]);
        Arrays.sort(expected[1]);

        // check if arrays are equal ignoring order
        assertTrue(Arrays.equals(expected[0], bms) || Arrays.equals(expected[1], bms));
    }
/*
    @Test
    void testBestMetro4() {
        // 3 triangle cycle
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);

        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200),
                new Building(bid3, 300)
        };

        TrackID tid = new TrackID(1);
        TrackID tid2 = new TrackID(2);
        TrackID tid3 = new TrackID(3);

        Track[] tracks = new Track[]{
                new Track(tid, bid1, bid2, 1, 100),
                new Track(tid2, bid2, bid3, 1, 200),
                new Track(tid3, bid3, bid1, 1, 300)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[] expected = new TrackID[] {tid2, tid3}; // if your printed output has the same tid, you are good
        Arrays.sort(bms);
        Arrays.sort(expected);

        // check if arrays are equal ignoring order
        assertTrue(Arrays.equals(expected, bms));
    }

    @Test
    void testBestMetro5() {
        // only 1 building
        BuildingID bid1 = new BuildingID(1);
        Building[] buildings = new Building[]{
                new Building(bid1, 100)
        };

        Track[] tracks = new Track[]{};

        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[] expected = new TrackID[]{}; // if your printed output has the same tid, you are good
        assertArrayEquals(expected, bms);
    }
*/
    @Test
    void testBestMetro6() {
        // multiple tracks from same building 1 to 2 with other buildnigs
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);

        Building[] buildings = new Building[]{
                new Building(bid1, 1000),
                new Building(bid2, 1000),
                new Building(bid3, 1000),
                new Building(bid4, 1000)
        };

        TrackID tid = new TrackID(1);
        TrackID tid2 = new TrackID(2);
        TrackID tid3 = new TrackID(3);
        TrackID tid4 = new TrackID(4);
        TrackID tid5 = new TrackID(5);
        TrackID tid6 = new TrackID(6);
        TrackID tid7 = new TrackID(7);
        TrackID tid8 = new TrackID(8);
        TrackID tid9 = new TrackID(9);
        TrackID tid10 = new TrackID(10);
        TrackID tid11 = new TrackID(11);
        TrackID tid12 = new TrackID(12);
        TrackID tid13 = new TrackID(13);
        TrackID tid14 = new TrackID(14);
        TrackID tid15 = new TrackID(15);
        TrackID tid16 = new TrackID(16);
        TrackID tid17 = new TrackID(17);
        TrackID tid18 = new TrackID(18);

        Track[] tracks = new Track[]{
                new Track(tid, bid1, bid2, 1, 100),
                new Track(tid2, bid1, bid2, 1, 50),
                new Track(tid3, bid1, bid2, 1, 25),
                new Track(tid4, bid1, bid3, 1, 100),
                new Track(tid5, bid1, bid3, 1, 50),
                new Track(tid6, bid1, bid3, 1, 25),
                new Track(tid7, bid1, bid4, 1, 100),
                new Track(tid8, bid1, bid4, 1, 50),
                new Track(tid9, bid1, bid4, 1, 25),
                new Track(tid10, bid2, bid3, 1, 50),
                new Track(tid11, bid2, bid3, 1, 50),
                new Track(tid12, bid2, bid3, 1, 25),
                new Track(tid13, bid2, bid4, 1, 50),
                new Track(tid14, bid2, bid4, 1, 50),
                new Track(tid15, bid2, bid4, 1, 25)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[] expected = new TrackID[]{tid, tid4, tid7}; // if your printed output has the same tid, you are good
        Arrays.sort(bms);
        Arrays.sort(expected);

        // check if arrays are equal ignoring order
        assertTrue(Arrays.equals(expected, bms));
    }
/*
    @Test
    void testBestMetro7() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        BuildingID bid3 = new BuildingID(3);
        BuildingID bid4 = new BuildingID(4);
        BuildingID bid5 = new BuildingID(5);
        BuildingID bid6 = new BuildingID(6);
        Building[] buildings = new Building[]{
                new Building(bid1, 1000),
                new Building(bid2, 2000),
                new Building(bid3, 3000),
                new Building(bid4, 1000),
                new Building(bid5, 2000),
                new Building(bid6, 3000),
        };

        TrackID tid1 = new TrackID(1);
        TrackID tid2 = new TrackID(2);
        TrackID tid3 = new TrackID(3);
        TrackID tid4 = new TrackID(4);
        TrackID tid5 = new TrackID(5);
        TrackID tid6 = new TrackID(6);
        TrackID tid7 = new TrackID(7);
        Track[] tracks = new Track[]{
                new Track(tid1, bid1, bid2, 100, 50),
                new Track(tid2, bid1, bid3, 100, 100),
                new Track(tid3, bid2, bid3, 100, 200),
                new Track(tid4, bid4, bid5, 100, 50),
                new Track(tid5, bid4, bid6, 100, 100),
                new Track(tid6, bid5, bid6, 100, 200),
                new Track(tid7, bid5, bid2, 100, 300),
        };
        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[] expected = new TrackID[]{tid7, tid6, tid3, tid5, tid2}; // if your printed output has the same tid, you are good
        Arrays.sort(bms);
        Arrays.sort(expected);

        // check if arrays are equal ignoring order
        assertTrue(Arrays.equals(expected, bms));
    }

    @Test
    void testBestMetro8() {
        // Buildings
        BuildingID b1 = new BuildingID(1);
        BuildingID b2 = new BuildingID(2);
        BuildingID b3 = new BuildingID(3);
        BuildingID b4 = new BuildingID(4);
        BuildingID b5 = new BuildingID(5);
        BuildingID b6 = new BuildingID(6);

        Building[] buildings = new Building[]{
                new Building(b1, 10),
                new Building(b2, 10),
                new Building(b3, 10),
                new Building(b4, 10),
                new Building(b5, 10),
                new Building(b6, 10)
        };

        // Tracks
        TrackID t1 = new TrackID(1);
        TrackID t2 = new TrackID(2);
        TrackID t3 = new TrackID(3);
        TrackID t4 = new TrackID(4);
        TrackID t5 = new TrackID(5);
        TrackID t6 = new TrackID(6);
        TrackID t7 = new TrackID(7);
        TrackID t8 = new TrackID(8);
        Track[] tracks = new Track[]{
                new Track(t1, b1, b2, 8, 5),
                new Track(t2, b1, b4, 7, 5),
                new Track(t3, b2, b4, 6, 5),
                new Track(t4, b2, b3, 1, 5),
                new Track(t5, b4, b3, 4, 5),
                new Track(t6, b4, b5, 3, 5),
                new Track(t7, b3, b6, 2, 5),
                new Track(t8, b5, b6, 5, 5)
        };
        McMetro metro = new McMetro(tracks, buildings);
        TrackID[] bms = metro.bestMetroSystem();
        TrackID[] expected = new TrackID[]{t4, t7, t6, t5, t2};
        String result1 = "";
        String result2 = "";
        for (int i=0; i<bms.length; i++){
            result1 += "["+bms[i]+"], ";
        }
        for (int i=0; i<expected.length; i++){
            result2 += "["+expected[i]+"], ";
        }
        System.out.println("Expected: "+result2);
        System.out.println("Actual: "+result1);
        assertArrayEquals(expected, bms);
    }
*/
    @Test
    void testSearchForPassengersSimple() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {"John", "Johnny"};
        String[] expected = {"John", "Johnny"};
        mcMetro.addPassengers(passengers);

        ArrayList<String> found = mcMetro.searchForPassengers("Jo");
        assertArrayEquals(expected, found.toArray(new String[0]));
    }

    @Test
    void testSearchForPassengers() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {"Alex", "Bob", "Ally"};
        String[] expected = {"Alex", "Ally"};
        mcMetro.addPassengers(passengers);

        ArrayList<String> found = mcMetro.searchForPassengers("al");

        assertArrayEquals(expected, found.toArray(new String[0]));
    }

    @Test
    void testSearchForPassengers2() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {"Alex", "Bob", "Ally"};
        String[] expected = {"Alex", "Ally"};
        mcMetro.addPassengers(passengers);

        ArrayList<String> found = mcMetro.searchForPassengers("al");

        assertArrayEquals(expected, found.toArray(new String[0]));
    }

    @Test
    void testSearchForPassengers3() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {
                "Alex", "Bob", "Ally", "al", "Bobby","bObbert", "David",
                "Davie", "Davis"};
        String[] expected = {"Al", "Alex", "Ally"};
        mcMetro.addPassengers(passengers);

        ArrayList<String> found = mcMetro.searchForPassengers("al");

        //printing arrays to compare
        String actualResult ="";
        for(int i=0; i<found.size(); i++){
            actualResult += found.get(i)+", ";
        }
        String expectedResult ="";
        for(int i=0; i<expected.length; i++){
            expectedResult += expected[i]+", ";
        }
        System.out.println("Expected: " + expectedResult);
        System.out.println("Actual: " + actualResult);

        assertArrayEquals(expected, found.toArray(new String[0]));
    }
    @Test
    void testSearchForPassengers4() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {
                "Alex", "Bob", "Ally", "al", "Bobby","bObbert", "David",
                "Davie", "Davis",};
        String[] expected = {"Bob", "Bobbert", "Bobby"};
        mcMetro.addPassengers(passengers);
        String[] found = mcMetro.searchForPassengers("bob").toArray(new String[0]);
        Arrays.sort(found);
        Arrays.sort(expected);

        // check if arrays are equal ignoring order
        assertTrue(Arrays.equals(expected, found));

    }
/*    @Test
    void testSearchForPassengers5() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {
                "Alex", "Bob", "Ally", "al", "Bobby","bObbert", "David",
                "Davie", "Davis",};
        String[] expected = {"Davis", "David", "Davie"};
        mcMetro.addPassengers(passengers);

        String[] found = mcMetro.searchForPassengers("dav").toArray(new String[0]);
        Arrays.sort(found);
        Arrays.sort(expected);

        // check if arrays are equal ignoring order
        assertTrue(Arrays.equals(expected, found));
    }
    @Test
    void testSearchForPassengers6() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {
                "Alex", "Bob", "Ally", "al", "Bobby","bObbert", "David",
                "Davie", "Davis",};
        String[] expected = {};
        mcMetro.addPassengers(passengers);

        ArrayList<String> found = mcMetro.searchForPassengers("col");

        //printing arrays to compare
        String actualResult ="";
        for(int i=0; i<found.size(); i++){
            actualResult += found.get(i)+", ";
        }
        String expectedResult ="";
        for(int i=0; i<expected.length; i++){
            expectedResult += expected[i]+", ";
        }
        System.out.println("Extepted: " + expectedResult);
        System.out.println("Actual: " + actualResult);

        assertArrayEquals(expected, found.toArray(new String[0]));
    }
*/
    @Test
    void testHireTicketCheckers() {
        int[][] schedule = new int[4][2];
        schedule[0][0] = 1;
        schedule[0][1] = 2;
        schedule[1][0] = 2;
        schedule[1][1] = 3;
        schedule[2][0] = 3;
        schedule[2][1] = 4;
        schedule[3][0] = 1;
        schedule[3][1] = 3;

        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(3, toHire);
    }

    @Test
    void testHireTicketCheckers2() {
        int[][] schedule = new int[7][2];
        schedule[0][0] = 0;
        schedule[0][1] = 2;
        schedule[1][0] = 1;
        schedule[1][1] = 3;
        schedule[2][0] = 2;
        schedule[2][1] = 5;
        schedule[3][0] = 4;
        schedule[3][1] = 6;
        schedule[4][0] = 5;
        schedule[4][1] = 9;
        schedule[5][0] = 6;
        schedule[5][1] = 9;
        schedule[6][0] = 8;
        schedule[6][1] = 10;

        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(3, toHire);
    }

    @Test
    void testHireTicketCheckers3() {
        int[][] schedule = new int[7][2];
        schedule[0][0] = -1000;
        schedule[0][1] = 20000;
        schedule[1][0] = 1;
        schedule[1][1] = 3;
        schedule[2][0] = 2;
        schedule[2][1] = 5;
        schedule[3][0] = 4;
        schedule[3][1] = 6;
        schedule[4][0] = 5;
        schedule[4][1] = 9;
        schedule[5][0] = 6;
        schedule[5][1] = 9;
        schedule[6][0] = 8;
        schedule[6][1] = 10;

        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(3, toHire);
    }

    @Test
    void testHireTicketCheckers4() {
        int[][] schedule = new int[4][2];
        schedule[0][0] = 1;
        schedule[0][1] = 1;
        schedule[1][0] = 1;
        schedule[1][1] = 1;
        schedule[2][0] = 1;
        schedule[2][1] = 1;
        schedule[3][0] = 1;
        schedule[3][1] = 1;

        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(4, toHire);
    }

    @Test
    void testHireTicketCheckers5() {
        int[][] schedule = new int[][]{
                {1,2}, {2,3}, {3,4}, {1,3},
                {1,4}, {3,6}, {4,7}, {4,5},
                {5,6}, {4,8}, {2,4}, {6,7}
        };

        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(6, toHire);
    }

    @Test
    void testHireTicketCheckers6(){
        int[][] schedule = new int[][]{
                {0,2}, {1,3}, {2,5}, {4,6},
                {5,9}, {6,9}, {8,10}
        };

        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(3, toHire);
    }
    @Test
    void testHireTicketCheckers7(){
        int[][] schedule = new int[][]{{1,3}, {1,3}, {1,3}};
        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(1, toHire);
    }
}
public record Track(TrackID id, BuildingID startBuildingId, BuildingID endBuildingId, int cost, int capacity) implements Comparable<Track> {
    @Override
    public int compareTo(Track o) {
        return id.compareTo(o.id);
    }
}
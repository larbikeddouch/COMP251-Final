public record TrackID(int trackId) implements Comparable<TrackID> {
    @Override
    public int compareTo(TrackID o) {
        return trackId - o.trackId;
    }
}

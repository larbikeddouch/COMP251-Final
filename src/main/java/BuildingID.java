import java.io.Serializable;

public record BuildingID(int buildingID) implements Comparable<BuildingID>, Serializable {
    @Override
    public int compareTo(BuildingID o) {
        return buildingID - o.buildingID;
    }
}
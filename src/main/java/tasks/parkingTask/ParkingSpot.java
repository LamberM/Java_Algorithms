package tasks.parkingTask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;


@Data
@AllArgsConstructor
public class ParkingSpot {
    private String spotId;
    private Integer level;
    private Integer spotNumber;
    private SpotType type;
    private boolean isOccupied;
    private boolean isReserved;
    private Integer durationMinutes;

    public ParkingSpot(@NonNull String spotId, Integer level, Integer spotNumber, SpotType type) {
        this.spotId = validateSpotId(spotId);
        this.level = validateLevel(level);
        this.spotNumber = validateSpotNumber(spotNumber);
        this.type = type;
        this.isOccupied=false;
        this.isReserved=false;
        this.durationMinutes=0;
    }

    private String validateSpotId(String spotId) {
        if (spotId.isEmpty()) {
            throw new IllegalArgumentException("SpotId has not to be empty");
        }
        return spotId;
    }

    private Integer validateLevel(int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("Level has to be more than 0");
        }
        return level;
    }

    private Integer validateSpotNumber(int spotNumber) {
        if (spotNumber <= 0) {
            throw new IllegalArgumentException("SpotNumber has to be more than 0");
        }
        return spotNumber;
    }
}

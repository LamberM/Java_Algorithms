package tasks.parkingTask;

import lombok.Data;
import lombok.NonNull;

import java.util.regex.Pattern;


@Data
public class Vehicle {
    @NonNull
    private String licensePlate;
    private VehicleType vehicleType;
    private String ownerName;
    private String ownerPhone;

    public Vehicle(@NonNull String licensePlate, VehicleType vehicleType, String ownerName, String ownerPhone) {
        this.ownerPhone = validateOwnerPhone(ownerPhone);
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.licensePlate = validateLicensePlate(licensePlate);
    }

    private String validateLicensePlate(String licensePlate) {
        if (licensePlate.isEmpty()) {
            throw new IllegalArgumentException("License plate has not to be empty");
        }
        return licensePlate;
    }

    private String validateOwnerPhone(String ownerPhone) {
        if (ownerPhone.isEmpty()) {
            throw new IllegalArgumentException("Owner phone has not to be empty");
        }
        Pattern pattern = Pattern.compile("^\\d{3}-\\d{3}-\\d{3}$");
        if (!pattern.matcher(ownerPhone).matches()) {
            throw new IllegalArgumentException("Owner phone has to be like 222-222-222");
        }
        return ownerPhone;
    }

}

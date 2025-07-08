package tasks.parkingTask;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParkingManager {
    private List<ParkingSpot> parkingSpots = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<ParkingSession> parkingSessions = new ArrayList<>();
    private List<MonthlyPass> monthlyPasses = new ArrayList<>();
    private static final String PARKING_SESSION_ID = "PS_%d";
    private int startSessionNumber = 0;
    private int startSpotLevel = 1;
    private int startSpotNumber = 0;
    private long startMonthlyPassId = 0L;

    public void addParkingSpot(ParkingSpot spot) throws ParkingException {
        var spotId = spot.getSpotId();
        if (parkingSpotIsExist(spotId)) {
            throw new ParkingException("Parking spot exist");
        } else {
            parkingSpots.add(spot);
        }
    }

    public void removeParkingSpot(String spotId) throws ParkingException {
        if (!parkingSpotIsExist(spotId)) {
            throw new ParkingException("Parking spot does not exist");
        }
        if (parkingSpotIsReserved(spotId)) {
            throw new ParkingException("Parking is reserved");
        }
        parkingSpots = parkingSpots.stream().filter(parkingSpot -> !parkingSpot.getSpotId().equals(spotId)).toList();
    }

    public List<ParkingSpot> getAvailableSpots() {
        return parkingSpots;
    }

    public List<ParkingSpot> getAvailableSpotsByType(SpotType type) {
        return parkingSpots.stream().filter(parkingSpot -> parkingSpot.getType().equals(type)).toList();
    }

    public List<ParkingSpot> getSpotsByLevel(int level) {
        return parkingSpots.stream().filter(parkingSpot -> parkingSpot.getLevel().equals(level)).toList();
    }

    public Optional<ParkingSpot> findSpotById(String spotId) {
        return parkingSpots.stream().filter(parkingSpot -> parkingSpot.getSpotId().equals(spotId)).findAny();
    }

    public void registerVehicle(Vehicle vehicle) throws ParkingException {
        if (vehicleIsExist(vehicle.getLicensePlate())) {
            throw new ParkingException("Vehicle exists");
        } else {
            vehicles.add(vehicle);
        }
    }

    public Optional<Vehicle> findVehicleByLicense(String licensePlate) {
        return vehicles.stream().filter(vehicle -> vehicle.getLicensePlate().equals(licensePlate)).findAny();
    }

    public List<Vehicle> getVehiclesByType(VehicleType type) {
        return vehicles.stream().filter(vehicle -> vehicle.getVehicleType().equals(type)).toList();
    }

    public ParkingSession parkVehicle(String licensePlate, String spotId) throws ParkingException {
        if (!vehicleIsExist(licensePlate)) {
            throw new ParkingException("Vehicle does not exist");
        }
        if (!parkingSpotIsExist(spotId)) {
            throw new ParkingException("Parking spot does not exist");
        }
        if (parkingSpotIsReserved(spotId)) {
            throw new ParkingException("Parking spot is reserved");
        }
        var sessionId = generateParkingSessionId();
        var vehicle = findVehicleByLicense(licensePlate).get();
        var spot = findSpotById(spotId).get();
        spot.setReserved(true);
        var parkingSession = new ParkingSession(sessionId, vehicle, spot, true);
        parkingSessions.add(parkingSession);
        return parkingSession;
    }

    public BigDecimal exitVehicle(String sessionId, PaymentMethod paymentMethod) throws ParkingException {
        if (!parkingSessionIsExist(sessionId)) {
            throw new ParkingException("Session doesn't exist");
        }
        if (!parkingSessionIsActive(sessionId)) {
            throw new ParkingException("Session doesn't be active");
        }
        var foundParkingSession = parkingSessions.stream().filter(parkingSession -> parkingSession.getSessionId().equals(sessionId)).findFirst().get();
        foundParkingSession.setActive(false);
        foundParkingSession.getSpot().setReserved(false);
        foundParkingSession.getSpot().setOccupied(false);
        foundParkingSession.setPaymentMethod(paymentMethod);
        return foundParkingSession.calculateCost();
    }

    public void reserveSpot(String spotId, String licensePlate, SpotType spotType, int durationMinutes) throws ParkingException {
        if (!parkingSpotIsExist(spotId)) {
            throw new ParkingException("Parking spot doesn't exist");
        }
        if (!vehicleIsExist(licensePlate)) {
            throw new ParkingException("Vehicle doesn't exist");
        }
        var filteredParkingSpot = parkingSpots.stream().filter(parkingSpot -> parkingSpot.getSpotId().equals(spotId)).findFirst().get();
        filteredParkingSpot.setReserved(true);
        filteredParkingSpot.setType(spotType);
        filteredParkingSpot.setDurationMinutes(durationMinutes);
        parkingSpots.removeIf(parkingSpot -> parkingSpot.getSpotId().equals(spotId));
        parkingSpots.add(filteredParkingSpot);
    }

    public void cancelReservation(String spotId) throws ParkingException {
        if (!parkingSpotIsExist(spotId)) {
            throw new ParkingException("Parking spot doesn't exist");
        } else {
            var filteredParkingSpot = parkingSpots.stream().filter(parkingSpot -> parkingSpot.getSpotId().equals(spotId)).findFirst().get();
            filteredParkingSpot.setReserved(false);
            parkingSpots.removeIf(parkingSpot -> parkingSpot.getSpotId().equals(spotId));
            parkingSpots.add(filteredParkingSpot);
        }
    }

    public MonthlyPass purchaseMonthlyPass(String licensePlate, SpotType spotType) throws ParkingException {
        if (!vehicleIsExist(licensePlate)) {
            throw new ParkingException("Vehicle does not exist");
        }
        var passId = generateMonthlyPassId();
        var vehicle = findVehicleByLicense(licensePlate).get();
        var monthlyPass = new MonthlyPass(passId, vehicle, LocalDate.now(), spotType);
        monthlyPasses.add(monthlyPass);
        return monthlyPass;
    }

    public boolean hasValidMonthlyPass(String licensePlate, SpotType spotType) {
        return monthlyPasses.stream().anyMatch(monthlyPass -> monthlyPass.getVehicle().getLicensePlate().equals(licensePlate) && monthlyPass.getSpotType().equals(spotType) && monthlyPass.isValid());
    }

    public List<MonthlyPass> getActiveMonthlyPasses() {
        return monthlyPasses.stream().filter(MonthlyPass::isValid).toList();
    }

    public List<ParkingSession> getActiveSessions() {
        return parkingSessions.stream().filter(ParkingSession::isActive).toList();
    }

    public List<ParkingSession> getSessionsByVehicle(String licensePlate) {
        return parkingSessions.stream().filter(parkingSession -> parkingSession.getVehicle().getLicensePlate().equals(licensePlate)).toList();
    }

    public BigDecimal getTotalRevenue() {
        return parkingSessions.stream().map(ParkingSession::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<SpotType, Integer> getOccupancyByType() {
        Map<SpotType, Integer> result = new HashMap<>();
        var standardSize = parkingSpots.stream().filter(parkingSpot -> parkingSpot.getType().equals(SpotType.STANDARD) && parkingSpot.isOccupied()).toList().size();
        var compactSize = parkingSpots.stream().filter(parkingSpot -> parkingSpot.getType().equals(SpotType.COMPACT) && parkingSpot.isOccupied()).toList().size();
        var disabledSize = parkingSpots.stream().filter(parkingSpot -> parkingSpot.getType().equals(SpotType.DISABLED) && parkingSpot.isOccupied()).toList().size();
        var electricSize = parkingSpots.stream().filter(parkingSpot -> parkingSpot.getType().equals(SpotType.ELECTRIC) && parkingSpot.isOccupied()).toList().size();
        var vipSize = parkingSpots.stream().filter(parkingSpot -> parkingSpot.getType().equals(SpotType.VIP) && parkingSpot.isOccupied()).toList().size();
        result.put(SpotType.STANDARD, standardSize);
        result.put(SpotType.COMPACT, compactSize);
        result.put(SpotType.DISABLED, disabledSize);
        result.put(SpotType.ELECTRIC, electricSize);
        result.put(SpotType.VIP, vipSize);
        return result;
    }


    private Long generateMonthlyPassId() {
        startMonthlyPassId = startMonthlyPassId + 1L;
        return startMonthlyPassId;
    }

    private Integer generateParkingLevel(Integer spotNumber) {
        if (spotNumber >= 100) {
            startSpotLevel++;
            startSpotNumber = 0;
        }
        return startSpotLevel;
    }

    private Integer generateParkingSpotNumber() {
        startSpotNumber++;
        return startSpotNumber;
    }

    private String generateParkingSessionId() {
        startSessionNumber++;
        return String.format(PARKING_SESSION_ID, startSessionNumber);
    }

    private boolean parkingSessionIsExist(String sessionId) {
        return parkingSessions.stream().anyMatch(parkingSession -> parkingSession.getSessionId().equals(sessionId));
    }

    private boolean parkingSessionIsActive(String sessionId) {
        return parkingSessions.stream().anyMatch(parkingSession -> parkingSession.getSessionId().equals(sessionId) && parkingSession.isActive());
    }

    private boolean parkingSpotIsExist(String spotId) {
        return parkingSpots.stream().anyMatch(parkingSpot -> parkingSpot.getSpotId().equals(spotId));
    }

    private boolean parkingSpotIsReserved(String spotId) {
        return parkingSpots.stream().anyMatch(parkingSpot -> parkingSpot.getSpotId().equals(spotId) && parkingSpot.isReserved());
    }

    private boolean vehicleIsExist(String licensePlate) {
        return vehicles.stream().anyMatch(vehicle -> vehicle.getLicensePlate().equals(licensePlate));
    }

    public static class ParkingException extends Exception {
        public ParkingException(String message) {
            super(message);
        }
    }
}

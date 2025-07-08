package tasks.parkingTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingManagerTest {
    private ParkingManager parking;
    private ParkingSpot standardSpot;
    private ParkingSpot vipSpot;
    private ParkingSpot disabledSpot;
    private Vehicle car;
    private Vehicle motorcycle;
    private Vehicle electricCar;

    @BeforeEach
    void setUp() {
        parking = new ParkingManager();

        standardSpot = new ParkingSpot("A1-001", 1, 1, SpotType.STANDARD);
        vipSpot = new ParkingSpot("A1-002", 1, 2, SpotType.VIP);
        disabledSpot = new ParkingSpot("A1-003", 1, 3, SpotType.DISABLED);

        car = new Vehicle("WW12345", VehicleType.CAR, "Jan Kowalski", "123-456-789");
        motorcycle = new Vehicle("WW67890", VehicleType.MOTORCYCLE, "Anna Nowak", "987-654-321");
        electricCar = new Vehicle("WE11111", VehicleType.ELECTRIC_CAR, "Piotr Wiśniewski", "555-444-333");
    }

    @Test
    void shouldAddAndRetrieveParkingSpots() throws ParkingManager.ParkingException {
        parking.addParkingSpot(standardSpot);
        parking.addParkingSpot(vipSpot);

        Optional<ParkingSpot> found = parking.findSpotById("A1-001");
        assertTrue(found.isPresent());
        assertEquals(SpotType.STANDARD, found.get().getType());

        assertEquals(2, parking.getAvailableSpots().size());
    }

    @Test
    void shouldNotAddDuplicateSpots() {
        assertDoesNotThrow(() -> parking.addParkingSpot(standardSpot));

        ParkingManager.ParkingException exception = assertThrows(
                ParkingManager.ParkingException.class,
                () -> parking.addParkingSpot(standardSpot)
        );
        assertEquals("Parking spot exist", exception.getMessage());
    }

    @Test
    void shouldRegisterAndFindVehicles() throws ParkingManager.ParkingException {
        parking.registerVehicle(car);
        parking.registerVehicle(motorcycle);

        Optional<Vehicle> found = parking.findVehicleByLicense("WW12345");
        assertTrue(found.isPresent());
        assertEquals("Jan Kowalski", found.get().getOwnerName());

        List<Vehicle> cars = parking.getVehiclesByType(VehicleType.CAR);
        assertEquals(1, cars.size());
    }

    @Test
    void shouldParkAndExitVehicle() throws ParkingManager.ParkingException {
        standardSpot.setOccupied(true);
        parking.addParkingSpot(standardSpot);
        parking.registerVehicle(car);

        ParkingSession session = parking.parkVehicle("WW12345", "A1-001");
        assertNotNull(session);
        assertEquals(car, session.getVehicle());
        assertEquals(standardSpot, session.getSpot());
        assertTrue(session.isActive());
        assertTrue(standardSpot.isOccupied());

        assertEquals(1, parking.getActiveSessions().size());

        BigDecimal cost = parking.exitVehicle(session.getSessionId(), PaymentMethod.CARD);
        assertNotNull(cost);
        assertTrue(cost.compareTo(BigDecimal.ZERO) >= 0);
        assertFalse(standardSpot.isOccupied());
        assertFalse(session.isActive());
    }

    @Test
    void shouldNotParkInOccupiedSpot() throws ParkingManager.ParkingException {
        parking.addParkingSpot(standardSpot);
        parking.registerVehicle(car);
        parking.registerVehicle(motorcycle);

        parking.parkVehicle("WW12345", "A1-001");

        ParkingManager.ParkingException exception = assertThrows(
                ParkingManager.ParkingException.class,
                () -> parking.parkVehicle("WW67890", "A1-001")
        );
        assertEquals("Parking spot is reserved", exception.getMessage());
    }

    @Test
    void shouldCalculateCostCorrectly() throws ParkingManager.ParkingException {
        parking.addParkingSpot(standardSpot); // 2.50 zł/godz
        parking.registerVehicle(car);

        ParkingSession sessionStart = parking.parkVehicle("WW12345", "A1-001");
        sessionStart.setEndTime(LocalTime.now().plusHours(2));
        BigDecimal sessionEnd = parking.exitVehicle(sessionStart.getSessionId(),PaymentMethod.MOBILE_APP);

        BigDecimal expectedCost = BigDecimal.valueOf(5.00); // 2h * 2.50
        assertEquals(expectedCost,sessionEnd);
    }

    @Test
    void shouldHandleDisabledSpotsFreeOfCharge() throws ParkingManager.ParkingException {
        parking.addParkingSpot(disabledSpot);
        parking.registerVehicle(car);

        ParkingSession session = parking.parkVehicle("WW12345", "A1-003");
        BigDecimal cost = session.calculateCost();
        assertEquals(0, cost.compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldReserveAndCancelSpot() throws ParkingManager.ParkingException {
        parking.addParkingSpot(standardSpot);
        parking.registerVehicle(car);
// todo: do naprawy
        parking.reserveSpot("A1-001", "WW12345", SpotType.STANDARD, 60); // 1 godzina
        assertTrue(standardSpot.isReserved());

        parking.cancelReservation("A1-001");
        assertFalse(standardSpot.isReserved());
    }

    @Test
    void shouldPurchaseAndValidateMonthlyPass() throws ParkingManager.ParkingException {
        parking.registerVehicle(car);

        MonthlyPass pass = parking.purchaseMonthlyPass("WW12345", SpotType.STANDARD);
        assertNotNull(pass);
        assertEquals(car, pass.getVehicle());
        assertTrue(pass.isValid());

        assertTrue(parking.hasValidMonthlyPass("WW12345", SpotType.STANDARD));
        assertFalse(parking.hasValidMonthlyPass("WW12345", SpotType.VIP));
    }

    @Test
    void shouldGetAvailableSpotsByType() throws ParkingManager.ParkingException {
        parking.addParkingSpot(standardSpot);
        parking.addParkingSpot(vipSpot);
        parking.addParkingSpot(disabledSpot);

        List<ParkingSpot> standardSpots = parking.getAvailableSpotsByType(SpotType.STANDARD);
        assertEquals(1, standardSpots.size());

        List<ParkingSpot> vipSpots = parking.getAvailableSpotsByType(SpotType.VIP);
        assertEquals(1, vipSpots.size());
    }

    @Test
    void shouldGetSpotsByLevel() throws ParkingManager.ParkingException {
        parking.addParkingSpot(standardSpot); // poziom 1
        parking.addParkingSpot(vipSpot);      // poziom 1

        ParkingSpot level2Spot = new ParkingSpot("B2-001", 2, 1, SpotType.STANDARD);
        parking.addParkingSpot(level2Spot);

        List<ParkingSpot> level1Spots = parking.getSpotsByLevel(1);
        assertEquals(2, level1Spots.size());

        List<ParkingSpot> level2Spots = parking.getSpotsByLevel(2);
        assertEquals(1, level2Spots.size());
    }

    @Test
    void shouldProvideOccupancyStatistics() throws ParkingManager.ParkingException {
        standardSpot.setOccupied(true);
        parking.addParkingSpot(standardSpot);
        parking.addParkingSpot(vipSpot);
        parking.registerVehicle(car);

        parking.parkVehicle("WW12345", "A1-001");

        Map<SpotType, Integer> occupancy = parking.getOccupancyByType();
        assertEquals(1, occupancy.get(SpotType.STANDARD));
        assertEquals(0, occupancy.getOrDefault(SpotType.VIP, 0));
    }

    @Test
    void shouldValidateVehicleFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("", VehicleType.CAR, "Name", "123-456-789"));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("WW12345", VehicleType.CAR, "Name", "123456789"));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("WW12345", VehicleType.CAR, "Name", "invalid-phone"));
    }

    @Test
    void shouldValidateParkingSpotFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new ParkingSpot("", 1, 1, SpotType.STANDARD));
        assertThrows(IllegalArgumentException.class,
                () -> new ParkingSpot("A1-001", 0, 1, SpotType.STANDARD));
        assertThrows(IllegalArgumentException.class,
                () -> new ParkingSpot("A1-001", 1, 0, SpotType.STANDARD));
    }

    @Test
    void shouldCalculateMonthlyPassCost() throws ParkingManager.ParkingException {
        parking.registerVehicle(car);

        MonthlyPass standardPass = parking.purchaseMonthlyPass("WW12345", SpotType.STANDARD);
        // Koszt = 2.50 * 24h * 30 dni * 0.6 (zniżka) = 1080 zł
        BigDecimal expectedCost = BigDecimal.valueOf(1080.00);
        assertEquals(expectedCost, standardPass.getCost());
    }

    @Test
    void shouldNotRemoveOccupiedSpot() throws ParkingManager.ParkingException {
        parking.addParkingSpot(standardSpot);
        parking.registerVehicle(car);
        parking.parkVehicle("WW12345", "A1-001");

        ParkingManager.ParkingException exception = assertThrows(
                ParkingManager.ParkingException.class,
                () -> parking.removeParkingSpot("A1-001")
        );
        assertEquals("Parking is reserved", exception.getMessage());
    }
}

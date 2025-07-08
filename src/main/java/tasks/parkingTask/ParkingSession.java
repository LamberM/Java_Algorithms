package tasks.parkingTask;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class ParkingSession {
    private String sessionId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalTime startTime;
    private LocalTime endTime;
    private PaymentMethod paymentMethod;
    private BigDecimal totalCost;
    private boolean isPaid;
    private boolean isActive;

    public ParkingSession(String sessionId, Vehicle vehicle, ParkingSpot spot, boolean isActive) {
        this.sessionId = sessionId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.startTime = LocalTime.now();
        this.endTime = LocalTime.now();
        this.isActive = isActive;
        this.totalCost = BigDecimal.ZERO;
        this.isPaid = false;
    }

    public ParkingSession(PaymentMethod paymentMethod, boolean isPaid) {
        this.endTime = LocalTime.now();
        this.paymentMethod = paymentMethod;
        this.totalCost = calculateCost();
        this.isPaid = isPaid;
    }

    public BigDecimal calculateCost() {
        var hour = endTime.getHour() - startTime.getHour();
        var minutes = endTime.getMinute() - startTime.getMinute();
        double cost;
        if (isHalfHour(minutes)) {
            cost = (hour + 0.5) * spot.getType().getHourlyRate().doubleValue();
        } else {
            cost = hour * spot.getType().getHourlyRate().doubleValue();
        }
        return BigDecimal.valueOf(cost);
    }

    public int getDurationInMinutes() {
        var hour = endTime.getHour() - startTime.getHour();
        return (hour * 60) + (endTime.getMinute() - startTime.getMinute());
    }

    private boolean isHalfHour(int minutes) {
        return minutes >= 30;
    }
}

package tasks.parkingTask;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MonthlyPass {
    private Long passId;
    private Vehicle vehicle;
    private LocalDate startDate;
    private LocalDate endDate;
    private SpotType spotType;
    private BigDecimal cost;

    public MonthlyPass(Long passId, Vehicle vehicle, LocalDate startDate, SpotType spotType) {
        this.passId = passId;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = startDate.plusDays(30);
        this.spotType = spotType;
        //(spotType* 24h * 30 dni * 0.6)
        this.cost = BigDecimal.valueOf(spotType.getHourlyRate().doubleValue() * 24 * 30 * 0.6);
    }

    public boolean isValid() {
        return LocalDate.now().isBefore(endDate);
    }

    public boolean isExpired() {
        return !isValid();
    }

    public int daysRemaining() {
        return LocalDate.now().getDayOfYear() - endDate.getDayOfYear();
    }
}

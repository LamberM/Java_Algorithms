package tasks.parkingTask;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum SpotType {
    STANDARD(2.50),      // 2.50 zł/godz
    COMPACT(2.00),       // 2.00 zł/godz
    DISABLED(0.00),      // bezpłatne
    ELECTRIC(3.00),      // 3.00 zł/godz + ładowanie
    VIP(5.00);           // 5.00 zł/godz

    private final BigDecimal hourlyRate;

    SpotType(double hourlyRate) {
        this.hourlyRate = BigDecimal.valueOf(hourlyRate);
    }

}

package com.fdmgroup.pilotbank2.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;

@Entity
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PremiumVisa extends Account {

    @Column(name = "CASH_BACK_RATE")
    @Builder.Default
    private BigDecimal cashBackRate = PREMIUM_VISA_CASH_BACK_RATE;

    @Column(name = "CASH_BACK_AMOUNT")
    @Builder.Default
    private BigDecimal cashBackAmount = BigDecimal.valueOf(0);

    @Column(name = "INTEREST_RATE")
    @Builder.Default
    private BigDecimal interestRate = PREMIUM_VISA_INTEREST_RATE;

    @Column(name = "MON_CHARGED_INTEREST")
    @Builder.Default
    private BigDecimal monthlyChargedInterest = BigDecimal.valueOf(0);

    @Column(name = "CREDIT_LIMIT")
    @Builder.Default
    private BigDecimal creditLimit = PREMIUM_VISA_CREDIT_LIMIT;

    @Column(name = "LIMIT_INCREASE_REQ_DT")
    private LocalDateTime lastRequestedLimitIncrease;

    @Override
    public String toString() {
        return super.toString() + " PremiumVisa [" +
                "cashBackRate=" + cashBackRate +
                ", cashBackAmount=" + cashBackAmount +
                ", interestRate=" + interestRate +
                ", monthlyChargedInterest=" + monthlyChargedInterest +
                ", creditLimit=" + creditLimit +
                ", lastRequestedLimitIncrease=" + lastRequestedLimitIncrease +
                "]";
    }
}

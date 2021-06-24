package com.fdmgroup.pilotbank2.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FirstClassChecking extends Account {

    @Column(name = "MONTHLY_SRVC_FEE_FL")
    @Builder.Default
    private Boolean  hasMonthlyServiceFee = false;

    @Column(name = "MONTHLY_SRVC_FEE")
    @Builder.Default
    private BigDecimal monthlyServiceFee = FIRST_CLASS_CHECKING_MONTHLY_FEE;

    @Column(name = "MONTHLY_MIN_BAL")
    @Builder.Default
    private BigDecimal monthlyMinimumBalance = FIRST_CLASS_CHECKING_MINIMUM_BALANCE;

    @Override
    public String toString() {
        return super.toString() + " FirstClassChecking [" +
                "hasMonthlyServiceFee=" + hasMonthlyServiceFee +
                ", monthlyServiceFee=" + monthlyServiceFee +
                ", monthlyMinimumBalance=" + monthlyMinimumBalance +
                "]";
    }
}

package com.fdmgroup.pilotbank2.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.STUDENT_MONTHLY_FEE;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Student extends Account {

    @Column(name = "ELIGIBILITY_FL")
    @Builder.Default
    private Boolean isEligibleStudent = true;

    @Column(name = "MONTHLY_SRVC_FEE")
    @Builder.Default
    private BigDecimal monthlyServiceFee = STUDENT_MONTHLY_FEE;

    @Override
    public String toString() {
        return super.toString() + " Student [" +
                "isEligibleStudent=" + isEligibleStudent +
                ", monthlyServiceFee=" + monthlyServiceFee +
                "]";
    }
}

package com.fdmgroup.pilotbank2.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends User {

    private BigDecimal income;

    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="MAIN_ACCT_ID")
    @JsonManagedReference(value = "customer-to-main-account")
    private Account mainAccount;

    @OneToMany(mappedBy = "ownedAccountCustomer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference(value = "customer-to-owned-accounts")
    private List<Account> ownedAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference(value = "customer-to-payee")
    private List<Payee> payees = new ArrayList<>();

    @Override
    public String toString() {
        return super.toString() + "Customer [income=" + income + "]";
   }

   public void addToOwnedAccounts(Account account){
        this.ownedAccounts.add(account);
        account.setOwnedAccountCustomer(this);
   }

   public void removeFromOwnedAccounts(Account account) {
        this.ownedAccounts.remove(account);
        account.setOwnedAccountCustomer(null);
   }

   public void addToPayees(Payee payee){
        this.payees.add(payee);
        payee.setCustomer(this);
   }

   public void removePayee(Payee payee){
        this.payees.remove(payee);
        payee.setActive(false);
   }

}

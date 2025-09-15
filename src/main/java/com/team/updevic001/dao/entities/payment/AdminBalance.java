package com.team.updevic001.dao.entities.payment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Builder
@Getter
@Setter
@Entity
@Table(name = "admin_balances")
public class AdminBalance {


    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    @Builder.Default
    BigDecimal totalBalance = BigDecimal.ZERO;

    @Builder.Default
    BigDecimal income = BigDecimal.ZERO;

    @Builder.Default
    BigDecimal expenditure = BigDecimal.ZERO;

//    @Builder.Default
//    BigDecimal availableBalance = BigDecimal.ZERO;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<AdminPaymentTransaction> transactions = new ArrayList<>();
}

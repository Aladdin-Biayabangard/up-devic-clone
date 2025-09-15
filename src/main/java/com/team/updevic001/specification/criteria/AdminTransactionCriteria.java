package com.team.updevic001.specification.criteria;

import com.team.updevic001.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminTransactionCriteria {

    TransactionType transactionType;
    LocalDate dateFrom;
    LocalDate toDate;
}

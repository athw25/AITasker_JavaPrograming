package com.aitasker.payment.entity;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction  extends BaseEntity {
    private String transactionCode;
    private Double  amount;
    private LocalDateTime transactionDate;
}

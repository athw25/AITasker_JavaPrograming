package com.aitasker.payment.entity;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction  extends BaseEntity {
}// Transaction.java

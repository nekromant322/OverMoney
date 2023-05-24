package com.override.orchestrator_service.model;

import com.override.orchestrator_service.util.PostgresIdUUIDType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.UUID;

@Entity
@TypeDefs({@TypeDef(name = "pg-id-uuid", typeClass = PostgresIdUUIDType.class) })
@Table(name="transactions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    /*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UUID")*/
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UUID")
    @Type(type="pg-id-uuid", parameters = @org.hibernate.annotations.Parameter(name = "column", value = "id"))
    @Column(unique = true)
    private UUID id;

    @Column
    private String message;

    @ManyToOne
    @Nullable
    private Category category;

    @Column
    private Float amount;

    @ManyToOne
    private OverMoneyAccount account;
}

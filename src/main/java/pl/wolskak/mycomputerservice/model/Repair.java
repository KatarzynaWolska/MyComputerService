package pl.wolskak.mycomputerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "repairs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "repair_id")
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "computer_damage_id", referencedColumnName = "computer_damage_id")
    private ComputerDamage computerDamage;

    @Column(name = "repairer_remarks")
    private String repairerRemarks;

    @Column(name = "price")
    private String price;

    @Column(name="invoice")
    @Lob
    private byte[] invoice;

    @Column(name = "notification")
    private Boolean showNotification;

    public Repair(ComputerDamage computerDamage, String repairerRemarks, String price) {
        this.computerDamage = computerDamage;
        this.repairerRemarks = repairerRemarks;
        this.price = price;
    }
}

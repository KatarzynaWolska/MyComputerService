package pl.wolskak.mycomputerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "computer_damages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComputerDamage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "computer_damage_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "computer_id")
    private Computer computer;

    @Column(name = "topic")
    private String topic;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "damage_photo")
    private byte[] damagePhoto;

    @Column(name = "status")
    private ComputerDamageStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Repairer repairer;

    @OneToOne(mappedBy = "computerDamage")
    private Repair repair;

    public ComputerDamage(Computer computer, String topic, String description, byte[] damagePhoto) {
        this.computer = computer;
        this.topic = topic;
        this.description = description;
        this.damagePhoto = damagePhoto;
        this.status = ComputerDamageStatus.REPORTED;
    }

    public String generateBase64Image()
    {
        return Base64.encodeBase64String(this.damagePhoto);
    }

    public void update(Computer computer, String topic, String description, byte[] damagePhoto) {
        this.computer = computer;
        this.topic = topic;
        this.description = description;

        if (damagePhoto != null) {
            this.damagePhoto = damagePhoto;
        }
    }

    public void assignToRepairer(Repairer repairer) {
        this.repairer = repairer;
        this.status = ComputerDamageStatus.ASSIGNED_TO_REPAIRER;
    }
}

package pl.wolskak.mycomputerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "computers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Computer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "computer_id")
    private int id;

    @Column(name = "type")
    private String type;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "year_of_production")
    private String yearOfProduction;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "processor")
    private String processor;

    @Column(name = "graphics_card")
    private String graphicsCard;

    @Column(name = "ram_memory")
    private String ramMemory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Customer customer;

    @OneToMany(mappedBy="computer",
                fetch = FetchType.LAZY,
                cascade = CascadeType.ALL,
                orphanRemoval = true)
    private Set<ComputerDamage> damages;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    public Computer(String type, String brand, String model, String yearOfProduction, String operatingSystem, String processor, String graphicsCard, String ramMemory, Customer customer, byte[] photo) {
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.yearOfProduction = yearOfProduction;
        this.operatingSystem = operatingSystem;
        this.processor = processor;
        this.graphicsCard = graphicsCard;
        this.ramMemory = ramMemory;
        this.customer = customer;
        this.photo = photo;
    }

    public String generateBase64Image()
    {
        return Base64.encodeBase64String(this.photo);
    }

    public void update(String type, String brand, String model, String yearOfProduction, String operatingSystem, String processor, String graphicsCard, String ramMemory, Customer customer, byte[] photo) {
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.yearOfProduction = yearOfProduction;
        this.operatingSystem = operatingSystem;
        this.processor = processor;
        this.graphicsCard = graphicsCard;
        this.ramMemory = ramMemory;
        this.customer = customer;

        if (photo != null) {
            this.photo = photo;
        }
    }
}

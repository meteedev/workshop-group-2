package com.kampus.kbazaar.kpoint;

import com.kampus.kbazaar.shopper.Shopper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "kpoint")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "shopper_id", referencedColumnName = "id")
    private Shopper shopper;

    @Column(name = "point")
    private Integer point;
}

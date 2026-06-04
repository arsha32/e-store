package com.example.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    
    @NotBlank
    private String buildingName;
    
    @NotBlank
    private String street;
    
    @NotBlank
    private String city;
   
    @NotBlank
    private String state;
    
    @NotBlank
    private String country;
    
    @NotBlank
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Address(@NotBlank String buildingName, @NotBlank String street, @NotBlank String city,
            @NotBlank String state, @NotBlank String country, @NotBlank String pincode) {
        this.buildingName = buildingName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}

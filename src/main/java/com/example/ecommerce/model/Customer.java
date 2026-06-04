package com.example.ecommerce.model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint; 
import jakarta.validation.constraints.Email;
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
@Table(name="Customers",
    uniqueConstraints =  {
        @UniqueConstraint( columnNames = { "email" }),
        @UniqueConstraint( columnNames = { "customer_Name" })
    }
)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_id")
    private Long customerId;

    @NotBlank
    @Column(name="customer_Name")
    private String customerName;

    @NotBlank
    @Email 
    @Column(name="email")
    private String email;

    @NotBlank
    @Column(name="password")
    private String password;

    public Customer(@NotBlank String customerName, @NotBlank @Email String email, @NotBlank String password) {
        this.customerName = customerName;
        this.email = email;
        this.password = password;
    }

    @OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
    
    @ManyToMany( cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "customer_Role",
        joinColumns = @JoinColumn(name="customer_id"),
        inverseJoinColumns = @JoinColumn(name="role_id123")
    )
    private Set<Role> roles = new HashSet<>();;
    
    @OneToMany(mappedBy = "customerProducts" , cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    @OneToOne(mappedBy = "customer" , cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Carts carts;
}

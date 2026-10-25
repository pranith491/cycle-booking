package com.cyclebooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String customerName;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String cycleName;
    @Column(nullable = false) private int quantity;
    @Column(nullable = false) private int unitPrice;
    @Column(nullable = false) private int totalPrice;
    @Column(nullable = false) private LocalDateTime bookedAt;

    public Booking() {}
    public Booking(Long userId, String customerName, String email, String cycleName, int quantity, int unitPrice) {
        this.userId=userId; this.customerName=customerName; this.email=email;
        this.cycleName=cycleName; this.quantity=quantity; this.unitPrice=unitPrice;
        this.totalPrice=quantity*unitPrice; this.bookedAt=LocalDateTime.now();
    }
    public Long getId(){return id;} public Long getUserId(){return userId;}
    public String getCustomerName(){return customerName;} public String getEmail(){return email;}
    public String getCycleName(){return cycleName;} public int getQuantity(){return quantity;}
    public int getUnitPrice(){return unitPrice;} public int getTotalPrice(){return totalPrice;}
    public LocalDateTime getBookedAt(){return bookedAt;}
}

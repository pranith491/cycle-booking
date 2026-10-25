package com.cyclebooking.controller;

import com.cyclebooking.model.*;
import com.cyclebooking.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingRepository bookings;
    private final UserRepository users;
    public BookingController(BookingRepository bookings, UserRepository users) {
        this.bookings=bookings; this.users=users;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
        try {
            Long userId = Long.valueOf(String.valueOf(body.get("userId")));
            String cycle = String.valueOf(body.getOrDefault("cycleName","")).trim();
            int quantity = Integer.parseInt(String.valueOf(body.get("quantity")));
            int price = Integer.parseInt(String.valueOf(body.get("unitPrice")));
            Optional<AppUser> user = users.findById(userId);
            if (user.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message","Please log in again."));
            if (cycle.isBlank() || quantity < 1 || quantity > 20 || price < 1)
                return ResponseEntity.badRequest().body(Map.of("message","Choose a cycle and quantity from 1 to 20."));
            AppUser u=user.get();
            Booking saved=bookings.save(new Booking(u.getId(),u.getName(),u.getEmail(),cycle,quantity,price));
            return ResponseEntity.ok(asMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message","Invalid booking details."));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> mine(@PathVariable Long userId) {
        if (!users.existsById(userId)) return ResponseEntity.badRequest().body(Map.of("message","User not found."));
        return ResponseEntity.ok(bookings.findByUserIdOrderByBookedAtDesc(userId).stream().map(this::asMap).toList());
    }

    // Demo dashboard endpoint: useful for viewing all records during local development.
    @GetMapping("/all")
    public List<Map<String,Object>> allBookings() {
        return bookings.findAll().stream().map(this::asMap).toList();
    }

    private Map<String,Object> asMap(Booking b) {
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("id",b.getId()); m.put("customerName",b.getCustomerName()); m.put("email",b.getEmail());
        m.put("cycleName",b.getCycleName()); m.put("quantity",b.getQuantity()); m.put("unitPrice",b.getUnitPrice());
        m.put("totalPrice",b.getTotalPrice()); m.put("bookedAt",b.getBookedAt());
        return m;
    }
}

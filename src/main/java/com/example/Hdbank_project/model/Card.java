package com.example.Hdbank_project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Không đước để trống")
    private String fullName;

    @Column(nullable = false, length = 12)
    @NotBlank(message = "Không đước để trống")
    private String idNumber;

    @Column(nullable = true, length = 18, unique = true)
    private String numberCard;

    @NotNull(message = "Không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    @NotNull(message = "Không được để trống")
    @Column(nullable = false)
    private LocalDate issuedDate;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    private String approverUsername;

    private String requestedBy;

    private String approvedBy;

    private LocalDateTime approvalTime;

    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}

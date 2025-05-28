package com.example.Hdbank_project.dto;

import com.example.Hdbank_project.model.CardType;

import java.time.LocalDate;

public class CardDTO {
    private CardType cardType;
    private String fullName;
    private String idNumber;
    private LocalDate issuedDate;
    private String notes;
}

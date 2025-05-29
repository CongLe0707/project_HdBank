package com.example.Hdbank_project.controller;

import com.example.Hdbank_project.config.JwtUtils;
import com.example.Hdbank_project.dto.CreateCardRequest;
import com.example.Hdbank_project.dto.RejectReasonDTO;
import com.example.Hdbank_project.model.Card;
import com.example.Hdbank_project.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(JwtUtils jwtUtils, CardService cardService) {
        this.cardService = cardService;
    }

    // 2. Tạo yêu cầu phát hành thẻ - ROLE_USER
    @PostMapping("/request")
    public ResponseEntity<?> createRequest(@Valid @RequestBody CreateCardRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Card card = Card.builder()
                .cardType(request.getCardType())
                .fullName(request.getFullName())
                .idNumber(request.getIdNumber())
                .issuedDate(request.getIssuedDate())
                .notes(request.getNotes())
                .build();
        Card created = cardService.createRequest(card, userDetails.getUsername());
        return ResponseEntity.ok(created);
    }

    // 3. Lấy danh sách yêu cầu của user - ROLE_USER
    @GetMapping("/my-requests")
    public ResponseEntity<List<Card>> getMyRequests() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Card> requests = cardService.getMyRequests(userDetails.getUsername());
        return ResponseEntity.ok(requests);
    }

    // 4. Lấy danh sách yêu cầu chờ duyệt - ROLE_APPROVER
    @GetMapping("/pending")
    public ResponseEntity<List<Card>> getPendingRequests() {
        List<Card> requests = cardService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cardService.approveRequest(id, userDetails.getUsername())
                .map(card -> ResponseEntity.ok(card))
                .orElse(ResponseEntity.badRequest().build());
    }

    // 6. Từ chối yêu cầu - ROLE_APPROVER
    @PostMapping("/reject/{id}")
    public ResponseEntity<Card> rejectRequest(@PathVariable Long id, @RequestBody RejectReasonDTO reasonDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return cardService.rejectRequest(id, reasonDto.getReason(), userDetails.getUsername())
                .map(card -> ResponseEntity.ok(card))
                .orElse(ResponseEntity.badRequest().build());
    }
}

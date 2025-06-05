package com.example.Hdbank_project.controller;

import com.example.Hdbank_project.config.JwtUtils;
import com.example.Hdbank_project.dto.CardSearchRequest;
import com.example.Hdbank_project.dto.CreateCardRequest;
import com.example.Hdbank_project.dto.RejectReasonDTO;
import com.example.Hdbank_project.model.Card;
import com.example.Hdbank_project.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        return ResponseEntity.ok(Map.of(
                "message", "Tạo yêu cầu phát hành thẻ thành công",
                "data", created
        ));
    }
    // 3. Lấy danh sách yêu cầu của user - ROLE_USER
    @GetMapping("/my-requests")
    public ResponseEntity<?> getMyRequests() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Card> requests = cardService.getMyRequests(userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách yêu cầu thành công",
                "data", requests
        ));
    }

    // 4. Lấy danh sách yêu cầu chờ duyệt - ROLE_APPROVER
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        List<Card> requests = cardService.getPendingRequests();
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách yêu cầu chờ duyệt thành công",
                "data", requests
        ));
    }

    // 5. Phê duyệt yêu cầu - ROLE_APPROVER
    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cardService.approveRequest(id, userDetails.getUsername())
                .map(card -> ResponseEntity.ok(Map.of(
                        "message", "Phê duyệt yêu cầu thành công",
                        "data", card
                )))
                .orElse(ResponseEntity.badRequest().body(Map.of(
                        "message", "User đã có thẻ cùng loại hoặc không thể phê duyệt"
                )));
    }

    // 6. Từ chối yêu cầu - ROLE_APPROVER
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestBody RejectReasonDTO reasonDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cardService.rejectRequest(id, reasonDto.getReason(), userDetails.getUsername())
                .map(card -> ResponseEntity.ok(Map.of(
                        "message", "Từ chối yêu cầu thành công",
                        "data", card
                )))
                .orElse(ResponseEntity.badRequest().body(Map.of(
                        "message", "Không thể từ chối yêu cầu"
                )));
    }

    // 7. Tìm kiếm thẻ
    @PostMapping("/search")
    public ResponseEntity<?> searchCard(@RequestBody CardSearchRequest request) {
        Optional<Card> cardOpt = cardService.searchCard(request.getNumberCard(), request.getIdNumber());
        if (cardOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Không tìm thấy thông tin thẻ."
            ));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Tìm thấy thông tin thẻ",
                "data", cardOpt.get()
        ));
    }
}

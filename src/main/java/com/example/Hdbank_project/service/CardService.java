package com.example.Hdbank_project.service;

import com.example.Hdbank_project.model.Card;
import com.example.Hdbank_project.model.RequestStatus;
import com.example.Hdbank_project.repository.CardRepository;
import com.example.Hdbank_project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
    }

    // Tạo yêu cầu phát hành thẻ
    public Card createRequest(Card card, String requestedBy) {
        card.setStatus(RequestStatus.PENDING);
        card.setRequestedBy(requestedBy); // Lưu username người gửi
        card.setApprovalTime(null);
        card.setApprovedBy(null);
        card.setRejectReason(null);
        return cardRepository.save(card);
    }

    // Lấy các yêu cầu đã gửi của người dùng
    public List<Card> getMyRequests(String username) {
        return cardRepository.findByRequestedBy(username);
    }

    // Lấy các yêu cầu đang chờ duyệt
    public List<Card> getPendingRequests() {
        return cardRepository.findByStatus(RequestStatus.PENDING);
    }

    // Phê duyệt yêu cầu
    public Optional<Card> approveRequest(Long id, String approverUsername) {
        Optional<Card> cardOpt = cardRepository.findById(id);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            card.setStatus(RequestStatus.APPROVED);
            card.setApprovedBy(approverUsername);
            card.setApprovalTime(LocalDateTime.now());
            Card savedCard = cardRepository.save(card);
            return Optional.of(savedCard);
        }
        return Optional.empty();
    }
    // Từ chối yêu cầu
    public Optional<Card> rejectRequest(Long id, String reason, String approver) {
        Optional<Card> optional = cardRepository.findById(id);
        if (optional.isPresent()) {
            Card card = optional.get();
            if (card.getStatus() == RequestStatus.PENDING) {
                card.setStatus(RequestStatus.REJECTED);
                card.setApprovalTime(LocalDateTime.now());
                card.setApprovedBy(approver);
                card.setRejectReason(reason);
                cardRepository.save(card);
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }
}

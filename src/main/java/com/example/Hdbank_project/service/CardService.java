package com.example.Hdbank_project.service;

import com.example.Hdbank_project.model.Card;
import com.example.Hdbank_project.model.CardType;
import com.example.Hdbank_project.model.RequestStatus;
import com.example.Hdbank_project.repository.CardRepository;
import com.example.Hdbank_project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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

    public boolean userHasApprovedCardOfType(String username, CardType cardType) {
        return cardRepository.existsByRequestedByAndCardTypeAndStatus(username, cardType, RequestStatus.APPROVED);
    }


    public boolean hasActiveRequest(String username, CardType cardType) {
        // Chỉ coi PENDING và APPROVED là active, REJECTED không tính
        List<RequestStatus> activeStatuses = List.of(RequestStatus.PENDING, RequestStatus.APPROVED);
        return cardRepository.existsByRequestedByAndCardTypeAndStatusIn(username, cardType, activeStatuses);
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
            if (card.getUser() != null) {
                boolean exists = cardRepository.existsByUserAndCardTypeAndIdNot(card.getUser(), card.getCardType(), card.getId());
                if (exists) {
                    return Optional.empty();
                }
            }
            card.setStatus(RequestStatus.APPROVED);
            card.setApprovedBy(approverUsername);
            card.setApprovalTime(LocalDateTime.now());
            String uniqueCardNumber = generateUnique18DigitCardNumber();
            card.setNumberCard(uniqueCardNumber);
            Card savedCard = cardRepository.save(card);
            return Optional.of(savedCard);
        }
        return Optional.empty();
    }

    private String generateUnique18DigitCardNumber() {
        String cardNumber;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 18; i++) {
                int digit = ThreadLocalRandom.current().nextInt(0, 10);
                // Đảm bảo số đầu tiên không phải 0
                if (i == 0 && digit == 0) {
                    digit = ThreadLocalRandom.current().nextInt(1, 10);
                }
                sb.append(digit);
            }
            cardNumber = sb.toString();
        } while (cardRepository.existsByNumberCard(cardNumber));
        return cardNumber;
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

    public Optional<Card> searchCard(String numberCard, String idNumber) {
        if (numberCard != null && !numberCard.isEmpty()) {
            return cardRepository.findByNumberCard(numberCard);
        }
        if (idNumber != null && !idNumber.isEmpty()) {
            List<Card> cards = cardRepository.findByIdNumber(idNumber);
            if (!cards.isEmpty()) {
                // Ví dụ trả về thẻ mới nhất
                Card latestCard = cards.get(0);
                return Optional.of(latestCard);
            }
        }
        return Optional.empty();
    }

}

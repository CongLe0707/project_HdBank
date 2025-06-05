package com.example.Hdbank_project.repository;


import com.example.Hdbank_project.model.Card;
import com.example.Hdbank_project.model.CardType;
import com.example.Hdbank_project.model.RequestStatus;
import com.example.Hdbank_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByRequestedBy(String user);
    List<Card> findByStatus(RequestStatus status);
    // Kiểm tra user đã có thẻ cùng loại, ngoại trừ thẻ đang xét
    boolean existsByUserAndCardTypeAndIdNot(User user, CardType cardType, Long excludeId);

    // Kiểm tra số thẻ có tồn tại chưa (để sinh số mới)
    boolean existsByNumberCard(String numberCard);


    List<Card> findByIdNumber(String idNumber);
    Optional<Card> findByNumberCard(String numberCard);

    boolean existsByRequestedByAndCardTypeAndStatus(String requestedBy, CardType cardType, RequestStatus status);


    boolean existsByRequestedByAndCardTypeAndStatusIn(String requestedBy, CardType cardType, List<RequestStatus> statuses);

}
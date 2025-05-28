package com.example.Hdbank_project.repository;


import com.example.Hdbank_project.model.Card;
import com.example.Hdbank_project.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByRequestedBy(String user);
    List<Card> findByStatus(RequestStatus status);
}
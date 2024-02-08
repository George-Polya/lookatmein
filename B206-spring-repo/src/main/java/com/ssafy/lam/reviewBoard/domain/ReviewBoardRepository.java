package com.ssafy.lam.reviewBoard.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewBoardRepository extends JpaRepository<ReviewBoard, Long> {
    List<ReviewBoard> findByIsdeletedFalse();
    List<ReviewBoard> findByComplainTrue();
    public List<ReviewBoard> findByUserUserSeqAndIsdeletedFalse(Long userSeq);
}

package com.aitasker.message.repository;

import com.aitasker.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByProjectIdOrderBySentAtAsc(Long projectId);
}

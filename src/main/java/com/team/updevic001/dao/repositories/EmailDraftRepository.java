package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.EmailDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmailDraftRepository extends JpaRepository<EmailDraft, Long>,
        JpaSpecificationExecutor<EmailDraft> {
}

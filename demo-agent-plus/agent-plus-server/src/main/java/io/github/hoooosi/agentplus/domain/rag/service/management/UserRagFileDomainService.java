package io.github.hoooosi.agentplus.domain.rag.service.management;

import org.springframework.stereotype.Service;
import io.github.hoooosi.agentplus.domain.rag.model.UserRagFileEntity;
import io.github.hoooosi.agentplus.domain.rag.repository.UserRagFileRepository;

@Service
public class UserRagFileDomainService {

    private final UserRagFileRepository userRagFileRepository;

    public UserRagFileDomainService(UserRagFileRepository userRagFileRepository) {
        this.userRagFileRepository = userRagFileRepository;
    }

    public UserRagFileEntity getById(String id) {
        return userRagFileRepository.selectById(id);
    }
}

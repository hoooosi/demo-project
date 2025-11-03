package io.github.hoooosi.agentplus.application.memory.assembler;


import io.github.hoooosi.agentplus.domain.memory.model.CandidateMemory;
import io.github.hoooosi.agentplus.domain.memory.model.MemoryType;
import io.github.hoooosi.agentplus.interfaces.dto.memory.CreateMemoryRequest;

public class MemoryCommandAssembler {

    public static CandidateMemory toCandidate(CreateMemoryRequest req) {
        CandidateMemory cm = new CandidateMemory();
        cm.setType(MemoryType.safeOf(req.getType()));
        cm.setText(req.getText());
        cm.setImportance(req.getImportance());
        cm.setTags(req.getTags());
        cm.setData(req.getData());
        return cm;
    }
}

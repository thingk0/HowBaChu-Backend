package com.HowBaChu.howbachu.service.impl;

import com.HowBaChu.howbachu.domain.dto.opin.OpinRequestDto;
import com.HowBaChu.howbachu.domain.dto.opin.OpinResponseDto;
import com.HowBaChu.howbachu.domain.entity.Opin;
import com.HowBaChu.howbachu.domain.entity.Vote;
import com.HowBaChu.howbachu.exception.CustomException;
import com.HowBaChu.howbachu.exception.constants.ErrorCode;
import com.HowBaChu.howbachu.repository.OpinRepository;
import com.HowBaChu.howbachu.repository.VoteRepository;
import com.HowBaChu.howbachu.service.OpinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpinServiceImpl implements OpinService {

    private final VoteRepository voteRepository;
    private final OpinRepository opinRepository;

    @Override
    @Transactional
    public Long createOpin(OpinRequestDto requestDto, String email, Long parentId) {

        Vote vote = voteRepository.findVoteByEmail(email).orElseThrow(
            () -> new CustomException(ErrorCode.VOTE_NOT_FOUND)
        );

        Opin opin;

        if (parentId == null) {
            opin = Opin.of(requestDto.getContent(), vote);
        } else {
            Opin parentOpin = opinRepository.findById(parentId).orElseThrow(
                () -> new CustomException(ErrorCode.OPIN_NOT_FOUND)
            );
            opin = Opin.of(requestDto.getContent(), vote, parentOpin);
        }

        return opinRepository.save(opin).getId();
    }

    @Override
    public List<OpinResponseDto> getOpinList() {
        return opinRepository.fetchOpinList();
    }

    @Override
    public List<OpinResponseDto> getOpinChildList(Long parentId) {
        return opinRepository.fetchOpinChildList(parentId);
    }

    @Override
    @Transactional
    public Long removeOpin(Long opinId, String email) {
        opinRepository.delete(findOpinByIdAndEmail(opinId, email));
        return opinId;
    }

    @Override
    @Transactional
    public Long updateOpin(OpinRequestDto requestDto, Long opinId, String email) {
        Opin opin = findOpinByIdAndEmail(opinId, email);
        opin.updateContent(requestDto.getContent());
        return opinId;
    }

    private Opin findOpinByIdAndEmail(Long opinId, String email) {
        Opin opin = opinRepository.findByOpinIdAndEmail(opinId, email).orElseThrow(
            () -> new CustomException(ErrorCode.OPIN_MISS_MATCH)
        );
        return opin;
    }

}

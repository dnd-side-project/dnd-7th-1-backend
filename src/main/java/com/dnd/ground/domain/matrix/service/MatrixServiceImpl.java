package com.dnd.ground.domain.matrix.service;

import com.amazonaws.util.StringUtils;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.MatrixUserCond;
import com.dnd.ground.domain.matrix.dto.MatrixCond;
import com.dnd.ground.domain.matrix.dto.MatrixRequestDto;
import com.dnd.ground.domain.matrix.dto.MatrixResponseDto;
import com.dnd.ground.domain.matrix.repository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.ExerciseRecordException;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @description 영역 조회와 관련한 Service 구현체
 * @author  박찬호
 * @since   2023.03.12
 * @updated 1. 특정 영역 검색 API 생성
 *          - 2023.03.12 박찬호
 */

@Service
@RequiredArgsConstructor
public class MatrixServiceImpl implements MatrixService {

    private final MatrixRepository matrixRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;

    @Override
    public List<MatrixResponseDto> getMatrix(MatrixRequestDto request) {
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> users = new ArrayList<>();

        if (request.getType().equals(MatrixUserCond.ALL)) {
            users.addAll(friendService.getFriends(user));
            users.addAll(challengeRepository.findUCInProgress(user));
            MatrixCond condition = new MatrixCond(request, user, new HashSet<>(users));

            return matrixRepository.findMatrix(condition);
        } else if (request.getType().equals(MatrixUserCond.CHALLENGE)) {
            String uuid = request.getUuid();
            if (StringUtils.isNullOrEmpty(uuid)) throw new ExerciseRecordException(ExceptionCodeSet.CHALLENGE_UUID_INVALID);
            MatrixCond condition = new MatrixCond(UuidUtil.hexToBytes(uuid));

            return matrixRepository.findChallengeMatrix(condition);
        } else {
            throw new ExerciseRecordException(ExceptionCodeSet.MATRIX_TYPE_INVALID);
        }
    }
}
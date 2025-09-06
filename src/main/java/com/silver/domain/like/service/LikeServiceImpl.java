package com.silver.domain.like.service;

import com.silver.domain.like.dto.response.LikeToggleResponseDto;
import com.silver.domain.like.dto.response.LikedSnackResponseDto;
import com.silver.domain.like.entity.Like;
import com.silver.domain.like.repository.LikeRepository;
import com.silver.domain.snack.entity.Snack;
import com.silver.domain.snack.repository.SnackHashtagRepository;
import com.silver.domain.snack.repository.SnackRepository;
import com.silver.domain.user.entity.User;
import com.silver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final SnackRepository snackRepository;
    private final SnackHashtagRepository snackHashtagRepository;


    @Override
    @Transactional
    public LikeToggleResponseDto toggleLike(Long userId, Long snackId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        Snack snack = snackRepository.findById(snackId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 간식: " + snackId));

        boolean liked;
        if (likeRepository.existsByUserAndSnack(user, snack)) {
            likeRepository.deleteByUserAndSnack(user, snack);
            liked = false;
        } else {
            likeRepository.save(Like.builder().user(user).snack(snack).build());
            liked = true;
        }
        long likeCount = likeRepository.countBySnack(snack);

        return LikeToggleResponseDto.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikedSnackResponseDto> getMyLikedSnacks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));

        return likeRepository.findByUserOrderByIdDesc(user).stream()
                .map(like -> {
                    Snack s = like.getSnack();
                    var tags = new HashSet<>(snackHashtagRepository.findHashtagNamesBySnackId(s.getId()));
                    return LikedSnackResponseDto.builder()
                            .snackId(s.getId())
                            .name(s.getName())
                            .snackCategory(s.getSnackCategory())
                            .manufacturer(s.getManufacturer())
                            .hashtags(tags)
                            .imageUrl(s.getImageUrl())
                            .build();
                })
                .toList();
    }
}

package com.silver.domain.snack.service;

import com.silver.domain.snack.dto.response.SnackDetailResponseDTO;
import com.silver.domain.snack.dto.response.SnackResponseDTO;
import com.silver.domain.snack.entity.Snack;
import com.silver.domain.snack.repository.SnackHashtagRepository;
import com.silver.domain.snack.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnackService {
    private final SnackRepository snackRepository;
    private final SnackHashtagRepository snackHashtagRepository;

    public Page<SnackResponseDTO> getAllSnacks(Pageable pageable, String keyword, String category, List<String> hashtags) {
        // 해시태그 파라미터가 비어있지 않은지 확인
        boolean hasHashtags = hashtags != null && !hashtags.isEmpty();

        // 새로 만든 통합 검색 메서드 호출
        Page<Snack> snackPage = snackRepository.findSnacksByFilters(keyword, category, hashtags, hasHashtags, pageable);

        List<SnackResponseDTO> dtoList = snackPage.getContent().stream()
                .map(snack -> {
                    var tags = new HashSet<>(snackHashtagRepository.findHashtagNamesBySnackId(snack.getId()));
                    return SnackResponseDTO.from(snack, tags);
                })
                .toList();

        return new PageImpl<>(dtoList, pageable, snackPage.getTotalElements());
    }

    public SnackDetailResponseDTO getSnackById(Long id) {
        Snack snack = snackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 간식을 찾을 수 없습니다: " + id));

        var tags = new HashSet<>(snackHashtagRepository.findHashtagNamesBySnackId(id));
        return SnackDetailResponseDTO.from(snack, tags);
    }
}
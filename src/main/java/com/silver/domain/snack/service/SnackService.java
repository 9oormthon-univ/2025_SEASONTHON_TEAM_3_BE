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
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnackService {
    private final SnackRepository snackRepository;
    private final SnackHashtagRepository snackHashtagRepository;

    public Page<SnackResponseDTO> getAllSnacks(Pageable pageable, String keyword, String category) {
        Page<Snack> snackPage;

        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasCategory = StringUtils.hasText(category);

        if (hasKeyword && hasCategory) {
            // 키워드와 카테고리 모두 있는 경우
            snackPage = snackRepository.findByNameContainingAndSnackCategory(keyword, category, pageable);
        } else if (hasKeyword) {
            // 키워드만 있는 경우
            snackPage = snackRepository.findByNameContaining(keyword, pageable);
        } else if (hasCategory) {
            // 카테고리만 있는 경우
            snackPage = snackRepository.findBySnackCategory(category, pageable);
        } else {
            // 검색 조건이 없는 경우 (전체 조회)
            snackPage = snackRepository.findAll(pageable);
        }
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
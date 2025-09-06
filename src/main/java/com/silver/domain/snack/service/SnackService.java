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

    public Page<SnackResponseDTO> getAllSnacks(Pageable pageable) {
        Page<Snack> snackPage = snackRepository.findAll(pageable);

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
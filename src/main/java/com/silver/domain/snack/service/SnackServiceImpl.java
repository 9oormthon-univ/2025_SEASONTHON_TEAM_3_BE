package com.silver.domain.snack.service;

import com.silver.domain.snack.dto.response.SnackDetailResponseDTO;
import com.silver.domain.snack.dto.response.SnackResponseDTO;
import com.silver.domain.snack.entity.Snack;
import com.silver.domain.snack.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnackServiceImpl implements SnackService {

    private final SnackRepository snackRepository;

    @Override
    public Page<SnackResponseDTO> getAllSnacks(Pageable pageable) {
        Page<Snack> snackPage = snackRepository.findAll(pageable);
        return snackPage.map(SnackResponseDTO::from);
    }

    @Override
    public SnackDetailResponseDTO getSnackById(Long id) {
        Snack snack = snackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 간식을 찾을 수 없습니다: " + id));
        return SnackDetailResponseDTO.from(snack);
    }
}
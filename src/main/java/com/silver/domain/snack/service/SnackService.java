package com.silver.domain.snack.service;

import com.silver.domain.snack.dto.response.SnackDetailResponseDTO;
import com.silver.domain.snack.dto.response.SnackResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SnackService {
    Page<SnackResponseDTO> getAllSnacks(Pageable pageable);
    SnackDetailResponseDTO getSnackById(Long id);
}
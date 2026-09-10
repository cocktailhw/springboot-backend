package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend.domain.MinwonStatus;
import com.example.backend.vo.MinwonRequest;
import com.example.backend.vo.MinwonResponse;

public interface MinwonService {

    MinwonResponse applyMinwon(MinwonRequest request);

    List<MinwonResponse> getMyMinwons();

    Page<MinwonResponse> getAllMinwons(Pageable pageable);

    MinwonResponse updateMinwonStatus(Long id, MinwonStatus status);
}

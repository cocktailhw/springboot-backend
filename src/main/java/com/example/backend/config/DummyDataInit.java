package com.example.backend.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;
import com.example.backend.repository.PortalItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 행복시청 행정 공지 더미 데이터 초기화.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataInit implements ApplicationRunner {

    private final PortalItemRepository portalItemRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (portalItemRepository.count() > 0) {
            return;
        }

        List<PortalItem> items = List.of(
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[공지] 2026년도 행복시 주민등록 사실조사 실시 안내")
                        .content("행복시에서는 「주민등록법」에 따라 2026년도 주민등록 사실조사를 실시합니다. "
                                + "조사 기간 중 방문조사원의 신분증을 반드시 확인하여 주시고, 문의는 자치행정과로 연락 바랍니다.")
                        .department("자치행정과")
                        .status("게시")
                        .viewCount(412)
                        .build(),
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[보도] 행복시, 정주여건 개선을 위한 대중교통 노선 개편")
                        .content("행복시는 시민 교통편의 향상을 위해 시내버스 노선을 개편합니다. "
                                + "주요 변경 노선과 시행일은 시청 누리집 및 버스정류장 안내문을 통해 확인할 수 있습니다.")
                        .department("공보관")
                        .status("게시")
                        .viewCount(687)
                        .build(),
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[고시] 행복 제3지구 도시계획시설 결정 및 지형도면 고시")
                        .content("「국토의 계획 및 이용에 관한 법률」에 따라 행복 제3지구 도시계획시설(도로·공원) 결정 및 "
                                + "지형도면을 고시합니다. 관계 도면은 도시계획과에서 열람할 수 있습니다.")
                        .department("도시계획과")
                        .status("게시")
                        .viewCount(298)
                        .build(),
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[공지] 지방세 세교부 및 전자납부 시스템 점검 안내")
                        .content("지방세 세교부·전자납부 시스템 안정화 점검을 실시합니다. "
                                + "점검 시간 동안 위택스 및 지방세 납부 서비스 이용이 일시 중단될 수 있으니 양해 부탁드립니다.")
                        .department("세정과")
                        .status("게시")
                        .viewCount(534)
                        .build(),
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[보도] 시민과 함께하는 2026 행복시 주민참여예산 공모")
                        .content("2026년도 주민참여예산 사업을 공모합니다. "
                                + "행복시 거주 시민이면 누구나 제안할 수 있으며, 접수 기간과 제출 방법은 기획예산과 안내문을 참고하시기 바랍니다.")
                        .department("기획예산과")
                        .status("게시")
                        .viewCount(756)
                        .build(),
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[공지] 행복시 민원여권과 여권 발급 대기시간 안내")
                        .content("성수기 여권 신청 증가에 따라 방문 예약제를 운영합니다. "
                                + "사전 예약 후 방문하시면 대기시간을 단축할 수 있으며, 긴급 발급은 별도 창구를 이용해 주시기 바랍니다.")
                        .department("민원여권과")
                        .status("게시")
                        .viewCount(623)
                        .build(),
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[안내] 행복시 생활폐기물 배출 요일제 변경 시행")
                        .content("깨끗하고 쾌적한 도시 환경 조성을 위해 생활폐기물 배출 요일제를 변경·시행합니다. "
                                + "공동주택 및 단독주택 배출 요령은 환경과 누리집에서 확인하시기 바랍니다.")
                        .department("환경과")
                        .status("게시")
                        .viewCount(891)
                        .build(),
                PortalItem.builder()
                        .type(ItemType.NOTICE)
                        .title("[공지] 행복시 공공건축물 내진성능평가 결과 공개")
                        .content("시민 안전 확보를 위해 관내 공공건축물 내진성능평가 결과를 공개합니다. "
                                + "대상 시설 목록과 보강 계획은 안전총괄과에서 열람·문의하실 수 있습니다.")
                        .department("안전총괄과")
                        .status("게시")
                        .viewCount(367)
                        .build()
        );

        portalItemRepository.saveAll(items);
        log.info("[DummyDataInit] 행복시청 공지 더미 데이터 {}건 초기화 완료", items.size());
    }
}

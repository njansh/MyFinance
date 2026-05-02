package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;
import java.util.UUID;

public interface GetFinancialDashboardKpisPort {
    KpiDashboardResponse execute(UUID userId, Integer month, Integer year);
}
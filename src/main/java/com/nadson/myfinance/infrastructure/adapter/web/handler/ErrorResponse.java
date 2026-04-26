package com.nadson.myfinance.infrastructure.adapter.web.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
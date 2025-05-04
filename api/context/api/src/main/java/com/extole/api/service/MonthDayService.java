package com.extole.api.service;

import java.time.MonthDay;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface MonthDayService {

    MonthDay valueOf(String val) throws InvalidMonthDayException;

    MonthDay valueOf(int month, int dayOfMonth);

}

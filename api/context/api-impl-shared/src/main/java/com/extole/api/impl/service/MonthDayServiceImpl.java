package com.extole.api.impl.service;

import java.time.MonthDay;

import com.extole.api.service.InvalidMonthDayException;
import com.extole.api.service.MonthDayService;

public class MonthDayServiceImpl implements MonthDayService {

    @Override
    public MonthDay valueOf(String val) throws InvalidMonthDayException {
        try {
            return MonthDay.parse(val);
        } catch (Exception e) {
            throw new InvalidMonthDayException("Invalid number " + val, e);
        }
    }

    @Override
    public MonthDay valueOf(int month, int dayOfMonth) {
        return MonthDay.of(month, dayOfMonth);
    }

}

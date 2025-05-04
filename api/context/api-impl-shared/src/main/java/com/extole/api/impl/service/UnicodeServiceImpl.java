package com.extole.api.impl.service;

import java.text.Normalizer;

import com.extole.api.service.UnicodeService;

public class UnicodeServiceImpl implements UnicodeService {
    @Override
    public String nfdNormalized(CharSequence src) {
        return Normalizer.normalize(src, Normalizer.Form.NFD);
    }

    @Override
    public boolean isNfdNormalized(CharSequence src) {
        return Normalizer.isNormalized(src, Normalizer.Form.NFD);
    }
}

package com.extole.common.security;

public interface SecureEmailService {

    String secureEmail(String email) throws EncrypterException;

    String readSecureEmail(String secureEmail) throws EncrypterException;

}

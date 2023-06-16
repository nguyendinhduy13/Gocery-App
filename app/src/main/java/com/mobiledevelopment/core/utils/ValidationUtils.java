package com.mobiledevelopment.core.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.validator.routines.EmailValidator;

public final class ValidationUtils {
    private ValidationUtils() {
        throw new AssertionError();
    }

    public static boolean IsEmailValid(String email) {
        return EmailValidator.getInstance(true).isValid(email);
    }

    public static boolean IsPhoneNumberValid(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        boolean isValid = false;
        try {
            Phonenumber.PhoneNumber vietnamNumberProto = phoneUtil.parse(phoneNumber, "VN");
            isValid = phoneUtil.isValidNumber(vietnamNumberProto); // returns true
        } catch (NumberParseException e) {
            System.err.println(
                    "ValidationUtils.IsPhoneNumberValid() - NumberParseException was thrown: " + e);
        }
        return isValid;
    }
}

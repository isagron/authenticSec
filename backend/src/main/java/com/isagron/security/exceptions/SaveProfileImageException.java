package com.isagron.security.exceptions;

public class SaveProfileImageException extends SecurityAppException{
    public SaveProfileImageException(String userName) {
        super(AppErrorCode.SAVE_IMAGE, String.format(AppErrorCode.SAVE_IMAGE.defaultMessageFormat(), userName));
    }
}

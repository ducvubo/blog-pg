package com.blog.blog_pg.exception;

public class BadRequestError extends RuntimeException {
   //gán errorCode mặc định và khi sử dụng thì truyền message vào
    public BadRequestError(String message) {
        super(message);
        this.errorCode = ErrorCode.BADREQUEST;
    }

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}

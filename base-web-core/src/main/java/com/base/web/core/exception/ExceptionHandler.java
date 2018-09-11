package com.base.web.core.exception;

import com.base.web.core.message.ResponseMessage;

public interface ExceptionHandler {

   <T extends Throwable> boolean support(Class<T> e);

    ResponseMessage handle(Throwable e);
}
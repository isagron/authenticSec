package com.isagron.security.services.validators;

import java.util.function.Supplier;

public class ValidationResultHandling {

    private boolean result;

    private ValidationResultHandling(){
        this.result = true;
    }

    private ValidationResultHandling(boolean result){
        this.result = result;
    }

    public void onError(ValidationHandler handler) {
        if (!this.result) {
            handler.handle();
        }
    }

    public void onSuccess(ValidationHandler handler) {
        if (this.result) {
            handler.handle();
        }
    }

    public <X extends Throwable> void onErrorThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (!result) {
            throw exceptionSupplier.get();
        }
    }

    public static ValidationResultHandling valid() {
        return new ValidationResultHandling(true);
    }

    public static ValidationResultHandling invalid() {
        return new ValidationResultHandling(false);
    }
}

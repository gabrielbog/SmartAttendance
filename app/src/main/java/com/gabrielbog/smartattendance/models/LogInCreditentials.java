package com.gabrielbog.smartattendance.models;

import com.gabrielbog.smartattendance.models.responses.LogInResponse;

public class LogInCreditentials {
    private static LogInCreditentials inst = null;
    private LogInResponse logInResponse;

    private LogInCreditentials(LogInResponse logInResponse) {
        this.logInResponse = logInResponse;
    }

    public static LogInCreditentials createInstance(LogInResponse logInResponse) {
        if(inst == null) {
            inst = new LogInCreditentials(logInResponse);
        }
        return inst;
    }

    public static LogInCreditentials getInstance() {
        return inst;
    }

    public LogInResponse getLogInResponse() {
        return logInResponse;
    }
}

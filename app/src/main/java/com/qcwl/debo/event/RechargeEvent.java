package com.qcwl.debo.event;

public class RechargeEvent {
    private String message;
    public RechargeEvent(String message){
        this.message=message;
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
}

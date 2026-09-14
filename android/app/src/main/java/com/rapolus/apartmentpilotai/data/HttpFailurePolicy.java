package com.rapolus.apartmentpilotai.data;

/** Maps transport failures to user-facing navigation without interpreting server authority. */
public final class HttpFailurePolicy {
    public enum Destination {
        INLINE,
        OFFLINE,
        SESSION_EXPIRED,
        ACCESS_DENIED,
        NOT_FOUND,
        ERROR
    }

    private HttpFailurePolicy() {
    }

    public static Destination destination(int status,boolean authenticatedRequest) {
        if(status==0)return Destination.OFFLINE;
        if(status==401&&authenticatedRequest)return Destination.SESSION_EXPIRED;
        if(status==403&&authenticatedRequest)return Destination.ACCESS_DENIED;
        if(status==404&&authenticatedRequest)return Destination.NOT_FOUND;
        if(status>=500)return Destination.ERROR;
        return Destination.INLINE;
    }
}

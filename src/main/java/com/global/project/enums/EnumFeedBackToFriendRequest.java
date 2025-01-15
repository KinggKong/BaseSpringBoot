package com.global.project.enums;

public enum EnumFeedBackToFriendRequest {
    AGREE, //0
    REFUSE; //1

    public static boolean isValidFeedbackStatus(int feedbackStatus) {
        for (EnumFeedBackToFriendRequest item : EnumFeedBackToFriendRequest.values()) {
            if (item.ordinal() == feedbackStatus) {
                return true;
            }
        }
        return false;
    }
}
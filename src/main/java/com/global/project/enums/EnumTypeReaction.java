package com.global.project.enums;

import lombok.Getter;

@Getter
public enum EnumTypeReaction {
    LIKE,
    LOVE,
    CARE,
    HAHA,
    WOW,
    SAD,
    ANGRY;

//    public static boolean isValidTypeReaction(int typeReaction) {
//        for (EnumTypeReaction item : EnumTypeReaction.values()) {
//            if (item.ordinal() == typeReaction) {
//                return true;
//            }
//        }
//        return false;
//    }
}

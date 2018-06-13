package com.whaletail;

public class Util {

    public static boolean equals(int value, int... ints) {
        for (int i : ints) {
            if (i == value) return true;
        }
        return false;
    }

}

package com.epobedinsky.test.util;

import java.util.Date;

public final class Util {
    public static long now() {
        return new Date().getTime();
    }

    private Util() {

    }
}

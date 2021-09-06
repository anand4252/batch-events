package com.techopact;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    /**
     * Throws exception 50% of the times.
     */
    public void throwExceptionRandomly() {
        final int random = (int) (Math.random() * 100);
        if (random % 2 == 0) {
            System.out.println("Throw an divide by zero exception");
            int a = 6;
            int b = 0;
            System.out.println(a / b);
        }
    }
}

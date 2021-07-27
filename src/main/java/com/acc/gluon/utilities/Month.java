package com.acc.gluon.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Month {
    public static final SimpleDateFormat monthDateFormatter = new SimpleDateFormat("yyyyMM");
    public static final SimpleDateFormat jsonDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public static int current() {
        return Integer.parseInt(monthDateFormatter.format(new Date()));
    }

    public static String monthFromInt(int luna) {
        int month = luna % 100;
        return month < 10 ? "0" + month : String.valueOf(month);
    }

    public static int prev(int luna) {
        int month = luna % 100;
        int year = luna / 100;
        if (month == 1) {
            --year;
            month = 12;
        } else {
            --month;
        }

        return month + year * 100;
    }

    public static int next(int luna) {
        int month = luna % 100;
        int year = luna / 100;
        if (month == 12) {
            ++year;
            month = 1;
        } else {
            ++month;
        }

        return month + year * 100;
    }

    public static int delta(int luna, int delta) {
        if (delta == 0) {
            return luna;
        } else {
            int lm = luna % 100;
            int l_year = luna / 100 + delta / 12;
            int l_mm = lm + delta % 12;
            if (l_mm < 1) {
                --l_year;
                l_mm += 12;
            } else if (l_mm > 12) {
                ++l_year;
                l_mm -= 12;
            }

            return l_year * 100 + l_mm;
        }
    }

}

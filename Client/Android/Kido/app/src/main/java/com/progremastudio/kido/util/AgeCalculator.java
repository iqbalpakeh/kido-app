/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.util;

import java.util.Calendar;

/**
 * Created by iqbalpakeh on 7/9/14.
 */
public class AgeCalculator {
    /**
     * Refer to this website:
     * http://www.codeproject.com/Articles/28837/Calculating-Duration-Between-Two-Dates-in-Years-Mo
     */
    private int[] monthDay = {31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int year, month, day, increment;
    private Calendar birthday;
    private Calendar now;

    public AgeCalculator(Calendar birthday) {
        this.birthday = birthday;
        this.now = Calendar.getInstance();
        this.increment = 0;
    }

    public AgeCalculator(Calendar lowCalendar, Calendar highCalendar) {
        this.birthday = lowCalendar;
        this.now = highCalendar;
        this.increment = 0;
    }

    public String getFormattedBirthday() {
        dayCalculation();
        monthCalculation();
        yearCalculation();
        return buildDateFormat(year, month, day);
    }

    private String buildDateFormat(int year, int month, int day) {
        return (String.valueOf(year) + " years, " +
                String.valueOf(month) + " months and " +
                String.valueOf(day) + " days");
    }

    public int getMonthDifferent() {
        dayCalculation();
        monthCalculation();
        yearCalculation();
        return (12 * year + month);
    }

    private void dayCalculation() {
        // assumption : "now" is always bigger than "birthday"
        if (birthday.get(Calendar.DATE) > now.get(Calendar.DATE)) {
            increment = monthDay[birthday.get(Calendar.MONTH)];
        }
        // februaries check
        if (increment == -1) {
            if (isLeapYear(birthday.get(Calendar.YEAR))) {
                increment = 29;
            } else {
                increment = 28;
            }
        }
        if (increment != 0) {
            day = (now.get(Calendar.DATE) + increment) - birthday.get(Calendar.DATE);
            increment = 1;
        } else {
            day = now.get(Calendar.DATE) - birthday.get(Calendar.DATE);
        }
    }

    private void monthCalculation() {
        if ((birthday.get(Calendar.MONTH) + increment) > now.get(Calendar.MONTH)) {
            month = (now.get(Calendar.MONTH) + 12) - (birthday.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = now.get(Calendar.MONTH) - (birthday.get(Calendar.MONTH) + increment);
            increment = 0;
        }
    }

    private void yearCalculation() {
        year = now.get(Calendar.YEAR) - (birthday.get(Calendar.YEAR) + increment);
    }

    private boolean isLeapYear(int year) {
        /**
         * Refer to Wikipedia:
         * http://en.wikipedia.org/wiki/Leap_year
         *
         *      if (year is not divisible by 4) then (it is a common year)
         *      else
         *      if (year is not divisible by 100) then (it is a leap year)
         *      else
         *      if (year is not divisible by 400) then (it is a common year)
         *      else (it is a leap year)
         */
        if ((year % 4) != 0) {
            return false;
        } else if ((year % 100) != 0) {
            return true;
        } else if ((year % 400) != 0) {
            return false;
        } else {
            return true;
        }
    }
}

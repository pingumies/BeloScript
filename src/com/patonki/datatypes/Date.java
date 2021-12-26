package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

import java.time.LocalDateTime;

/**
 * Datatyyppi, joka tallentaa tietoa päivämäärään liittyen
 */
public class Date extends DataType{
    private LocalDateTime date;
    private String[] weekdays = {"maanantai","tiistai","keskiviikko","torstai","perjantai","lauantai","sunnuntai"};
    public Date() {
        date = LocalDateTime.now();
    }

    @Override
    public BeloClass classFunction(BeloClass[] params) {
        String command = params[1].toString();
        switch (command) {
            case "day":
                return new BeloDouble(date.getDayOfMonth());
            case "year":
                return new BeloDouble(date.getYear());
            case "weekday":
                return new BeloString(weekdays[date.getDayOfWeek().getValue()-1]);
        }
        throw new IllegalArgumentException("Not a member/method of class: "+ command);
    }
}

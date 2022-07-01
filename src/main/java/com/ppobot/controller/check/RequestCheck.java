package com.ppobot.controller.check;

import com.ppobot.exception.DataException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

@Service
public class RequestCheck {

    public int idCheck(String idStr) throws DataException {
        try {
            int id = Integer.parseInt(idStr);
            return id;
        } catch (NumberFormatException e) {
            throw new DataException();
        }
    }

    public void titleCheck(String title) throws DataException {
        if (title.equals("")) {
            throw new DataException();
        }
    }

    public Timestamp periodOfRelevanceCheck(String periodOfRelevanceStr) throws DataException {
        try {
            DateFormat dtFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date parseDate = dtFrmt.parse(periodOfRelevanceStr);
            Date curDate = new Date();
            Timestamp curTmstmp = new Timestamp(curDate.getTime());
            Timestamp periodOfRelevance = new Timestamp(parseDate.getTime());
            if (periodOfRelevance.before(curTmstmp)) {throw new DataException();}
            return periodOfRelevance;
        } catch (ParseException e) {
            throw new DataException();
        }
    }

    public double distanceCheck(String distanceStr) throws DataException {
        try {
            double distance = Double.parseDouble(distanceStr);
            return distance;
        } catch (NumberFormatException e) {
            throw new DataException();
        }
    }
}

package com.ppobot.controller.check;

import com.ppobot.exception.DataException;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;

@Service
public class UserCheck {

    public int idCheck(String idStr) throws DataException {
        try {
            int id = Integer.parseInt(idStr);
            return id;
        } catch (NumberFormatException e) {
            throw new DataException();
        }
    }

    public void tgNameCheck(String tgName) throws DataException {
        if (tgName.equals("")) {
            throw new DataException();
        }
    }

    public Point2D.Double geoCheck(String geoLatStr, String geoLongStr) throws DataException {
        try {
            Point2D.Double geo = new Point2D.Double(Double.parseDouble(geoLatStr), Double.parseDouble(geoLongStr));
            return geo;
        } catch (NumberFormatException e) {
            throw new DataException();
        }
    }

    public boolean roleCheck(String roleStr, String role) throws DataException {
        if (!roleStr.equals("customer") && !roleStr.equals("executor") && !roleStr.equals("administrator")) {
            throw new DataException();
        }
        return roleStr.equals(role);
    }
}

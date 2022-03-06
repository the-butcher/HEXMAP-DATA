package com.igorion.hexmap.forecast;

import java.util.Date;

public interface IForecast {

    Date getDate();

    String getGkz();

    double getForecast();

    double getCi68Upper();

    double getCi68Lower();

}

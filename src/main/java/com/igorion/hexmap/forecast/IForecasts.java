package com.igorion.hexmap.forecast;

import java.util.Date;
import java.util.Optional;

public interface IForecasts {

    Optional<IForecast> optForecast(Date date, String gkz);

    Date getMaxDate();

}

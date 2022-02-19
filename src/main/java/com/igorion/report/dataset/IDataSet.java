package com.igorion.report.dataset;

import java.util.List;
import java.util.Optional;

import com.igorion.type.live.ILiveType;

public interface IDataSet<X, Y> extends ILiveType {

    /**
     * get the number of values contained in this instance "horizontally" (in a table that would resemble "column-count")
     * @return
     */
    default int getDimX() {
        return getKeysX().size();
    }

    /**
     * get all keys of this dataset in x-direction (in a table that would resemble "column" names)
     * @return
     */
    List<X> getKeysX();

    /**
     * get the number of values contained in this instance "vertically" (in a table that would resemble "row-count")
     * @return
     */
    default int getDimY() {
        return getKeysY().size();
    }

    /**
     * get the column identifiable by the given X-Key<br>
     * the result will be a sub-dataset having the y-dimension of this dataset as x-dimension and exactly 1 as y-dimension
     * @param keyX
     * @return
     */
    Optional<IDataEntry<Y, X>> optEntryX(X keyX);

    /**
     * get all keys of this dataset in y-direction (in a table that would resemble "row" names/identifiers)
     * @return
     */
    List<Y> getKeysY();

    List<IDataEntry<X, Y>> getEntriesY();

    /**
     * get the row identifiable by the given Y-Key<br>
     * the result will be a sub-dataset having the same x-dimension that this dataset has and exactly 1 as y-dimension
     *
     * @param keyY
     * @return
     */
    Optional<IDataEntry<X, Y>> optEntryY(Y keyY);

}

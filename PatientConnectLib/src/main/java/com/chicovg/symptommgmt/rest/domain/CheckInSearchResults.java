package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;

import java.util.List;

/**
 * Created by victorguthrie on 11/29/14.
 */
public class CheckInSearchResults {

    @BundleField(type= BundleFieldType.INTEGER)
    private int totalRecords;
    @BundleField(type= BundleFieldType.COLLECTION)
    private List<CheckIn> checkIns;

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<CheckIn> getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(List<CheckIn> checkIns) {
        this.checkIns = checkIns;
    }
}

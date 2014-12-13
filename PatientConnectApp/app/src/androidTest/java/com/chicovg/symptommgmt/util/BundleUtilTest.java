package com.chicovg.symptommgmt.util;

import android.os.Bundle;
import com.chicovg.symptommgmt.rest.domain.CheckIn;
import com.chicovg.symptommgmt.rest.domain.CheckInResponse;
import com.chicovg.symptommgmt.rest.domain.CheckInStatus;
import com.chicovg.symptommgmt.rest.domain.PainLevel;

import java.util.Date;
import java.util.LinkedList;

import static org.junit.Assert.assertNotNull;

public class BundleUtilTest {

    @org.junit.Test
    public void testToBundle() throws Exception {

        CheckIn checkIn = new CheckIn();
        checkIn.setId(1);
        checkIn.setClientId(1);
        checkIn.setOverallPainLevel(PainLevel.SEVERE);
        checkIn.setStatus(CheckInStatus.COMPLETED);
        checkIn.setTimestamp(new Date());
        checkIn.setResponses(new LinkedList<CheckInResponse>());
        CheckInResponse response = new CheckInResponse();
        response.setId(1);
        response.setClientId(1);
        response.setCheckInId(1);
        response.setQuestionId(1);
        response.setMedication(null);
        response.setResponse("some response");
        checkIn.getResponses().add(response);

        Bundle bundle = BundleUtil.toBundle(checkIn);
        assertNotNull(bundle);
    }

    @org.junit.Test
    public void testFromBundle() throws Exception {

    }
}
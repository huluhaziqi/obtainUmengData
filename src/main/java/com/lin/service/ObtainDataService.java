package com.lin.service;

import com.lin.model.entity.ActityUserSummaryEntity;
import com.lin.model.entity.StatDailyUseLengthTimeEntity;
import org.apache.http.HttpResponse;

public interface ObtainDataService {

    boolean isLoginSuccess();

    HttpResponse getLoginResponse();

    ActityUserSummaryEntity getAndroidReport();

    ActityUserSummaryEntity getIosReport();

    StatDailyUseLengthTimeEntity getAndroidStatDailyUserLengthTimeReport();
    StatDailyUseLengthTimeEntity getIosStatDailyUserLengthTimeReport();

}

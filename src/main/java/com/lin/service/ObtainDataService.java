package com.lin.service;

import com.lin.model.entity.ActityUserSummaryEntity;
import org.apache.http.HttpResponse;

public interface ObtainDataService {

    boolean isLoginSuccess();

    HttpResponse getLoginResponse();

    ActityUserSummaryEntity getAndroidReport();

    ActityUserSummaryEntity getIosReport();

}

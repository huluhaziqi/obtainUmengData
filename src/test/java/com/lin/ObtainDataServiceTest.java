package com.lin;


import com.lin.model.entity.ActityUserSummaryEntity;
import com.lin.service.ObtainDataService;
import com.lin.util.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ObtainDataServiceTest {
    private Logger logger = LoggerFactory.getLogger(ObtainDataServiceTest.class);
    @Autowired
    private ObtainDataService obtainDataService;

    @Test
    public void testGetLoginResponse(){
        HttpResponse httpResponse = obtainDataService.getLoginResponse();
        HttpEntity httpEntity = httpResponse.getEntity();
        String result = null;
        try {
             result = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("result = " + result);
    }

    @Test
    public void testIndexOf(){
        String string = "ab lin xiao bin";
        String subStr = "lin";
        int index = string.indexOf(subStr);
        System.out.println(index);
    }

    @Test
    public void testGetReport(){
        try {
            boolean login = obtainDataService.isLoginSuccess();
            logger.info("login {}",login);
            if(login) {
                ActityUserSummaryEntity actityUserSummaryEntityOfAndroid = obtainDataService.getAndroidReport();
                logger.info("actityUserSummaryEntityOfAndroid {}", JsonUtils.obj2JsonStr(actityUserSummaryEntityOfAndroid));
                ActityUserSummaryEntity actityUserSummaryEntityOfIos = obtainDataService.getIosReport();
                logger.info("actityUserSummaryEntityOfIos {}", JsonUtils.obj2JsonStr(actityUserSummaryEntityOfIos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }

}

package com.linkshortener;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class LambdaHandlerTests {
//    private final Context context;
//
//    public LambdaHandlerTests(@Autowired Context context) {
//        this.context = context;
//    }

//    @Test
//    void whenTheUsersPathIsInvokedViaLambda_thenShouldReturnAList() throws IOException {
//        LambdaHandler lambdaHandler = new LambdaHandler();
//        AwsProxyRequest req = new AwsProxyRequest();
//        req.setPath("/l/cafc2");
//        req.setHttpMethod("GET");
//        AwsProxyResponse resp = lambdaHandler.handleRequest(req, context);
//        Assertions.assertNotNull(resp.getBody());
//        Assertions.assertEquals(200, resp.getStatusCode());
//    }
}

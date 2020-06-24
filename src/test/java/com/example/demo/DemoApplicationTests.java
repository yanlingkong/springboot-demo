package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

import com.tencentcloudapi.live.v20180801.LiveClient;

import com.tencentcloudapi.live.v20180801.models.DescribeLiveStreamStateRequest;
import com.tencentcloudapi.live.v20180801.models.DescribeLiveStreamStateResponse;
/**
 * test测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
        try{

            Credential cred = new Credential("AKIDLM8iGbv1we8PRXbzJGRPQDpLbeYVRI5c", "e5ITTJwKU5WaP8lBORGuJm352lMk1CA4");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("live.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            LiveClient client = new LiveClient(cred, "ap-beijing", clientProfile);

            String dto = "http://laliu.miduolegou.cn/live/1400287096_68589037490937858.flv";
            String [] ss = dto.split("/");
            String aa = ss[4];
            String StreamName = aa.replace(".flv","");
            String params = "{\"AppName\":\"live\",\"DomainName\":\"tuiliu.miduolegou.cn.livepush.myqcloud.com\",\"StreamName\":\"1400287096_68589037490937858\"}";
            DescribeLiveStreamStateRequest req = DescribeLiveStreamStateRequest.fromJsonString(params, DescribeLiveStreamStateRequest.class);

            DescribeLiveStreamStateResponse resp = client.DescribeLiveStreamState(req);

            System.out.println(DescribeLiveStreamStateRequest.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

}


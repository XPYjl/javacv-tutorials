package com.papergame.camera.common.config;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FrameGrabberConfig
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/24 13:37
 * @Version 1.0
 */
@Configuration
public class FrameGrabberConfig {
    @Resource
    private ApplicationArguments arguments;

    @Bean
    public List<FrameGrabber> getFrameGrabber() throws FrameGrabber.Exception {
        String[] sourceArgs = arguments.getSourceArgs();
        int num= Integer.parseInt(sourceArgs[0]);
        System.out.println("FrameGrabberConfig系统参数："+num);

        List<FrameGrabber> result = new ArrayList<>();
        long time1 = System.currentTimeMillis();
        for(int i=0;i<num;i++){
            FrameGrabber frameGrabber = new OpenCVFrameGrabber(i);
            // 摄像头有可能有多个分辨率，这里指定
            // 可以指定宽高，也可以不指定反而调用grabber.getImageWidth去获取，
            //frameGrabber.setImageWidth(1280);
            //frameGrabber.setImageHeight(720);
           // frameGrabber.setFrameRate(30);
            frameGrabber.start();
            // 开启抓取器
            result.add(frameGrabber);
            System.out.println("开启抓取器success:"+i);
        }
        long time2 = System.currentTimeMillis();
        System.out.println(time2-time1);
        return  result;
    }

}

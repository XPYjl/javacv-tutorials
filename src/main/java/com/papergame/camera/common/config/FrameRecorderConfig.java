package com.papergame.camera.common.config;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

/**
 * @ClassName FrameRecorderConfig
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/24 14:06
 * @Version 1.0
 */
@Deprecated
public class FrameRecorderConfig {


    private ApplicationArguments arguments;

    public List<FrameRecorder> getFrameRecorder() throws FrameRecorder.Exception {

        List<FrameRecorder> result = new ArrayList<>();
        String[] sourceArgs = arguments.getSourceArgs();
        int num= Integer.parseInt(sourceArgs[0]);
        System.out.println("FrameRecorderConfig系统参数："+num);

        for(int i=0;i<num;i++){
            FrameRecorder recorder =  new FFmpegFrameRecorder( "D:\\video\\摄像头"+i+"_"
                    + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+ ".mp4",        // 存放文件的位置
                    1280,   // 分辨率的宽，与视频源一致
                    720,  // 分辨率的高，与视频源一致
                    0);                      // 音频通道，0表示无

            // 文件格式
            recorder.setFormat("mp4");
            // 帧率与抓取器一致
            recorder.setFrameRate(30);
            // 编码格式
            recorder.setPixelFormat(AV_PIX_FMT_YUV420P);
            // 编码器类型
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
            // 视频质量，0表示无损
            recorder.setVideoQuality(0);

            recorder.start();

            result.add(recorder);
            System.out.println("recorder--start:"+i);

        }
        return  result;
    }
}

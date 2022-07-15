package com.papergame.camera.controller;

import com.papergame.camera.controller.example.PreviewCamera;
import com.papergame.camera.controller.example.RecordCameraSaveMp4;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;


/**
 * @ClassName StartController
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/23 16:59
 * @Version 1.0
 */
@Controller
@RequestMapping("/testcamera")
@Slf4j
public class StartController {

    private volatile Boolean flag=true;

    private int frame=30;

    private List<CanvasFrame> canvasFrameList;

    private List<FrameRecorder> frameRecorderList;

    @Autowired
    private List<TargetDataLine> targetDataLines;

    @Autowired
    private TargetDataLine line;

    private List<TargetDataLine> targetDataLineList;

    @Autowired
    private List<FrameGrabber> frameGrabberList;


    public StartController(List<CanvasFrame> canvasFrameList, List<FrameRecorder> frameRecorderList, List<TargetDataLine> targetDataLineList) {
        this.canvasFrameList = canvasFrameList;
        this.frameRecorderList = frameRecorderList;
        this.targetDataLineList = targetDataLineList;
    }



    @Async("threadExecutor")
    public void recordCameraSaveMp4(int i,String name) throws Exception {
        String[] deStrings = VideoInputFrameGrabber.getDeviceDescriptions();
        flag=true;
        System.out.println(i+":startCamera:"+deStrings[i]);
        long startTime = System.currentTimeMillis();

        // 实例化、初始化帧抓取器
        // 实例化帧抓取器
        log.info("rameGrabber grabber = new OpenCVFrameGrabber(i):;"+i);
        FrameGrabber grabber = frameGrabberList.get(i);

        //TargetDataLine line = targetDataLines.get(i);
        // 实例化、初始化输出操作相关的资源，
        // 具体怎么输出由子类决定，例如窗口预览、存视频文件等

        CanvasFrame previewCanvas= new CanvasFrame("摄像头预览"+i, i);
        previewCanvas.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        previewCanvas.setAlwaysOnTop(true);

        canvasFrameList.add(previewCanvas);
        //AudioService audioService = new AudioService();
        String filename =  "D:\\video\\"+deStrings[i]+"_"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+ ".mp4";
        if(StringUtils.isNotBlank(name)||!"".equals(name)){
            filename="D:\\video\\"+name+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"_Cam"+i+".mp4";
        }

        FrameRecorder recorder =  new FFmpegFrameRecorder(filename,// 存放文件的位置
                1280,   // 分辨率的宽，与视频源一致
                720,  // 分辨率的高，与视频源一致
                0);   // 音频通道，0表示无

        // 文件格式
        recorder.setFormat("mp4");
        // 帧率与抓取器一致
        recorder.setFrameRate(frame);
        // 编码格式
        recorder.setPixelFormat(AV_PIX_FMT_YUV420P);
        // 编码器类型
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
        // 视频质量，0表示无损
        recorder.setVideoQuality(0);

        /**
         * 开始设置音频
         */
        // 码率恒定
        recorder.setAudioOption("crf", "0");
        // 最高音质
        recorder.setAudioQuality(0);
        // 192 Kbps
        recorder.setAudioBitrate(192000);

        // 采样率
        recorder.setSampleRate(44100);

        // 立体声
        recorder.setAudioChannels(2);
        // 编码器
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

        // 每次取得的原始数据大小
        int audioBufferSize = 44100 * 2;

        // 初始化数组，用于暂存原始音频采样数据
        byte[] audioBytes = new byte[audioBufferSize];

        recorder.start();


        frameRecorderList.add(recorder);
        log.info("初始化完成，耗时[{}]毫秒",System.currentTimeMillis()-startTime);

        // 两帧输出之间的间隔时间，默认是1000除以帧率，子类可酌情修改
        int interVal = (int)(1000/ frame);

        Frame captureFrame;
        Mat mat;
        // 超过指定时间就结束循环
        while (flag) {
            long stime = System.currentTimeMillis();
            if(i==88){
                int nBytesRead = 0;
                while (nBytesRead == 0 && flag) {
                    // 音频数据是从数据线中取得的
                    nBytesRead = line.read(audioBytes, 0, line.available());
                }

                // 如果nBytesRead<1，表示isFinish标志被设置true，此时该结束了
                if (nBytesRead<1) {
                    return;
                }

                // 采样数据是16比特，也就是2字节，对应的数据类型就是short，
                // 所以准备一个short数组来接受原始的byte数组数据
                // short是2字节，所以数组长度就是byte数组长度的二分之一
                int nSamplesRead = nBytesRead / 2;
                short[] samples = new short[nSamplesRead];

                // 两个byte放入一个short中的时候，谁在前谁在后？这里用LITTLE_ENDIAN指定拜访顺序，
                ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                // 将short数组转为ShortBuffer对象，因为帧录制器的入参需要该类型
                ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

                // 音频帧交给帧录制器输出
                ((FFmpegFrameRecorder) recorder).recordSamples(44100, 2, sBuff);
            }
            // 取一帧
            captureFrame = grabber.grab();

            if (null==captureFrame) {
                log.error("帧对象为空");
                break;
            }
           //OpenCVFrameConverter.ToIplImage openCVConverter = new OpenCVFrameConverter.ToIplImage();
            // 将帧对象转为mat对象
           // mat = openCVConverter.convertToMat(captureFrame);
            previewCanvas.showImage(captureFrame);
            // 子类输出
            recorder.record(captureFrame);
            long etime = System.currentTimeMillis();
            int t1 = (int) (1000/ this.frame -(etime-stime)-1);
            System.out.println(t1);
            // 适当间隔，让肉眼感受不到闪屏即可
            if(t1>0) {
              Thread.sleep(t1);
            }
        }
    }

    public void stopCamera() {
        try {
            flag=false;
            // 子类需要释放的资源
            frameRecorderList.stream().forEach(e -> {
                try {
                    e.close();
                } catch (FrameRecorder.Exception ex) {
                    ex.printStackTrace();
                }
            });

            canvasFrameList.stream().forEach(e -> {
                e.dispose();
            });

            frameRecorderList.clear();
            canvasFrameList.clear();
        } catch (Exception exception) {
            log.error("do releaseOutputResource error", exception);
        }

    }
}

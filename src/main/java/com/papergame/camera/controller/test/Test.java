package com.papergame.camera.controller.test;

import com.papergame.camera.JavaCVApplication;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.springframework.boot.SpringApplication;

import javax.swing.*;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;

/**
 * @ClassName Test
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/27 16:20
 * @Version 1.0
 */
public class Test {

    public static void main(String[] args) throws InterruptedException, FrameGrabber.Exception, FrameRecorder.Exception {
       // UvcCameraUtils.showUvcCameraFrame();
       // SpringApplication.run(JavaCVApplication.class, args);
        recordCamera("D:\\vedio",25);
    }
    public static void recordCamera(String outputFile, double frameRate) throws FrameGrabber.Exception, FrameRecorder.Exception, InterruptedException {
        System.setProperty("java.awt.headless", "false");

        FrameGrabber grabber = FrameGrabber.createDefault(0);//本机摄像头默认0
        grabber.start();//开启抓取器
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器
        // 抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
        IplImage grabbedImage = converter.convert(grabber.grab());
        int width = grabbedImage.width();
        int height = grabbedImage.height();
        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
        recorder.setVideoCodec(AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264，编码
        recorder.setFormat("flv");//封装格式，如果是推送到rtmp就必须是flv封装格式
        recorder.setFrameRate(frameRate);
        recorder.start();//开启录制器
        long startTime=0;
        long videoTS=0;
        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        Frame rotatedFrame=converter.convert(grabbedImage);//不知道为什么这里不做转换就不能推到rtmp
        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            rotatedFrame = converter.convert(grabbedImage);
            frame.showImage(rotatedFrame);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            recorder.setTimestamp(videoTS);
            recorder.record(rotatedFrame);
            Thread.sleep(40);
        }
        frame.dispose();//关闭窗口
        recorder.close();//关闭推流录制器，close包含release和stop操作
        grabber.close();//关闭抓取器
    }

}

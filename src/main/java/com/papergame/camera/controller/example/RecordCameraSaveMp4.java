package com.papergame.camera.controller.example;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

/**
 * @ClassName RecordCameraSaveMp4
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/23 11:28
 * @Version 1.0
 */
public class RecordCameraSaveMp4 extends AbstractCameraApplication {



    // 存放视频文件的完整位置，请改为自己电脑的可用目录
    private static final String RECORD_FILE_PATH = "D:\\video\\"
            + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+ ".mp4";


    // 帧录制器
    protected FrameRecorder recorder;

    protected CanvasFrame previewCanvas;

    @Override
    protected void initOutput() throws Exception {

        System.out.println(CanvasFrame.getDefaultGamma() );
        System.out.println(grabber.getGamma() );
        previewCanvas = new CanvasFrame("摄像头预览", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        // previewCanvas = new CanvasFrame("摄像头");
        previewCanvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        previewCanvas.setAlwaysOnTop(true);

        // 实例化FFmpegFrameRecorder
        recorder = new FFmpegFrameRecorder(RECORD_FILE_PATH,        // 存放文件的位置
                getCameraImageWidth(),   // 分辨率的宽，与视频源一致
                getCameraImageHeight(),  // 分辨率的高，与视频源一致
                0);                      // 音频通道，0表示无

        // 文件格式
        recorder.setFormat("mp4");

        // 帧率与抓取器一致
        recorder.setFrameRate(getFrameRate());

        // 编码格式
        recorder.setPixelFormat(AV_PIX_FMT_YUV420P);

        // 编码器类型
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);

        // 视频质量，0表示无损
        recorder.setVideoQuality(0);

        // 初始化
        recorder.start();

    }

    @Override
    protected void output(Frame frame) throws Exception {
        // 存盘
        previewCanvas.showImage(frame);
        recorder.record(frame);
    }

    @Override
    protected void releaseOutputResource() throws Exception {
        recorder.close();
    }


    public static void main(String[] args) {
        // 30表示抓取和录制的操作执行30秒，注意，这是程序执行的时长，不是录制视频的时长
        new RecordCameraSaveMp4().action(30);

    }
}

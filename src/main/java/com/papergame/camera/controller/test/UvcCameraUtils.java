package com.papergame.camera.controller.test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.WindowConstants;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;
import javax.swing.JOptionPane;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class UvcCameraUtils {
    public static int frameIndex = 0;
    // video device name， "Integrated Webcam" 是电脑自带摄像头名，可在设备管理器--相机，查看名称
    private static final String videoDeviceName = "Integrated Webcam";

    /**只能打开电脑内置摄像头**/
    public static void showUvcCameraFrame() throws InterruptedException,
            FrameGrabber.Exception {
        // 0表示摄像头id
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();//开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("电脑摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);
        while (true) {
            if (!canvas.isDisplayable()) {//窗口是否关闭
                grabber.stop();//停止抓取
                grabber.close();
                System.exit(-1);//退出
            }

            Frame frame = grabber.grab();
            canvas.showImage(frame);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
            Thread.sleep(50);//50毫秒刷新一次图像
        }
    }

    /**根据名称显示摄像头视频画面**/
    public static void showUvcCameraFrame(boolean isSaveImage) throws FrameGrabber.Exception, InterruptedException {
        frameIndex = 0;
        int deviceIndex = -1;
        // 获取设备名称
        String[] deStrings = VideoInputFrameGrabber.getDeviceDescriptions();
        System.out.println(deStrings);
        if (deStrings != null && deStrings.length > 0) {
            for (int i=0; i < deStrings.length; i++) {
                System.out.println("descriptions index=" + i + ", value=" + deStrings[i]);
                if (videoDeviceName.equals(deStrings[i])) {
                    deviceIndex = i;
                    break;
                }
            }
        }
        if (deviceIndex < 0) {
            JOptionPane.showMessageDialog(null, "<html><font size=8>" + "没有找到指定设备");
            return;
        }
        // 参数根据设备管理器中，Cameras下面，设备的摄像头排次，如果是第二个参数为1
        VideoInputFrameGrabber grabber = VideoInputFrameGrabber.createDefault(deviceIndex);
        // 摄像头画面宽高
        grabber.setImageWidth(360);
        grabber.setImageHeight(640);
        // 图像格式
        grabber.setFormat("YUY2");
        grabber.start();
        CanvasFrame canvasFrame = new CanvasFrame("摄像头：" + videoDeviceName);
        canvasFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvasFrame.setAlwaysOnTop(true);
        while (true) {
            if (canvasFrame.isDisplayable()) {
                grabber.stop();
                grabber.close();
                System.exit(-1);
            }
            Frame frame = null;
            try {
                frame = grabber.grab();
            } catch (Exception e) {
                System.out.println("showUvcCameraFrame error=" + e.getMessage() + ", isSaveImage=" + isSaveImage);
            }
            if (frame != null) {
                canvasFrame.showImage(frame);
                // 在D盘保存两张图
                if (isSaveImage && frameIndex < 2) {
                    writeFrameToFile(frame, "index_" + frameIndex);
                    frameIndex++;
                }
            }
            Thread.sleep(30);
        }
    }

    public static void writeFrameToFile(Frame frame, String fileNamePrefix) {
        writeFrameToFile("D:\\", frame, fileNamePrefix);
    }

    public static void writeFrameToFile(String filePatn, Frame frame, String fileNamePrefix) {
        if (filePatn == null || "".equals(filePatn)) {
            return;
        } else if (!isFileExist(filePatn)) {
            File file = new File(filePatn);
            file.mkdirs();
        }
        File targetFile = new File(filePatn + File.separator + fileNamePrefix + ".jpg");
        String imgSuffix = "jpg";
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage srcBi = converter.getBufferedImage(frame);
        int owidth = srcBi.getWidth();
        int oheight = srcBi.getHeight();
        int width = owidth;
        int height = oheight;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Image image = srcBi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        bi.getGraphics().drawImage(image, 0, 0, null);
        try {
            ImageIO.write(bi, imgSuffix, targetFile);
        } catch (IOException e) {
            System.out.println("writeFrameToFile image write error=" + e.getMessage()
                    + ", frame=" + frame + ", fileNamePrefix=" + fileNamePrefix);
            e.printStackTrace();
        }
        if (converter != null) {
            converter.close();
        }
    }

    /**从帧数据中获取Image对象**/
    public static Image getImageFromFrame(Frame frame) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage srcBi = converter.getBufferedImage(frame);
        int owidth = srcBi.getWidth();
        int oheight = srcBi.getHeight();
        // 对截取的帧进行等比例缩放
        int width = owidth;
        int height = oheight;// (int) (((double) width / owidth) * oheight);
        Image image = srcBi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        if (converter != null) {
            converter.close();
        }
        return image;
    }

    /**文件是否存在**/
    public static boolean isFileExist(String pathname) {
        File file = new File(pathname);
        return isFileExist(file);
    }

    /**文件是否存在**/
    public static boolean isFileExist(File file) {
        if (null != file) {
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }
}

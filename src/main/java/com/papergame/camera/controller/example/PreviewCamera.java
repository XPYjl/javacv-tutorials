package com.papergame.camera.controller.example;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;

import javax.swing.*;

/**
 * @ClassName PreviewCamera
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/23 11:24
 * @Version 1.0
 */
public class PreviewCamera extends AbstractCameraApplication {

    protected CanvasFrame previewCanvas;

    @Override
    protected void initOutput() throws Exception {
        previewCanvas = new CanvasFrame("摄像头预览", CanvasFrame.getDefaultGamma() / grabber.getGamma());
       // previewCanvas = new CanvasFrame("摄像头");
        previewCanvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        previewCanvas.setAlwaysOnTop(true);

    }

    @Override
    protected void output(Frame frame) throws Exception {
        previewCanvas.showImage(frame);
    }

    @Override
    protected void releaseOutputResource() throws Exception {
        if (null!= previewCanvas) {
            previewCanvas.dispose();
        }
    }

    public static void main(String[] args) {
        new PreviewCamera().action(1000);
    }
}

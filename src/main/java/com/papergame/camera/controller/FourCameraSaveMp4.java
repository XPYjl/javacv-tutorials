package com.papergame.camera.controller;

import com.papergame.camera.controller.StartController;
import com.papergame.camera.controller.example.PreviewCamera;
import com.papergame.camera.controller.example.RecordCameraSaveMp4;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @ClassName FourCameraSaveMp4
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/24 10:23
 * @Version 1.0
 */

@Slf4j
@Controller
@RequestMapping("/camera")
public class FourCameraSaveMp4 extends JDialog implements ApplicationRunner {

    @Resource
    private ApplicationArguments arguments;
    @Autowired
    private StartController startController;



    @Deprecated
    @RequestMapping("/start1")
    public void start(){
        log.info("mp4");
        new RecordCameraSaveMp4().action(30);
    }

    @Deprecated
    @RequestMapping("/start2")
    public void start2(){
        log.info("noMp4");
        new PreviewCamera().action(1000);
    }


    @RequestMapping("/start")
    public void startFourCamera(String name) throws Exception {
        System.out.println(">>>>>"+name);
        String[] sourceArgs = arguments.getSourceArgs();
        int num= Integer.parseInt(sourceArgs[0]);
        for(int i=0;i<num;i++){
            startController.recordCameraSaveMp4(i,name);
        }

    }

    @RequestMapping("/stop")
    public void stopFourCamera() {
        startController.stopCamera();
        log.info("输出结束");
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        JFrame frame=new JFrame("控制面板");    //创建Frame窗口
        frame.setSize(400, 200);
        JPanel jp=new JPanel();    //创建JPanel对象
        JButton startBtn=new JButton("开始录制");    //创建JButton对象
        JButton endBtn=new JButton("结束录制");    //创建JButton对象

        JLabel label = new JLabel("视频保存的文件名");
        label.setBounds(20,20,100,50);

        JTextField jt = new JTextField("请输入需要保存到本地的视频文件名");
        //设置默认提示信息，字体颜色设置淡灰色
        jt.setText("");
        jt.setColumns(28);//文本长度
        jt.setFont(new Font("微软雅黑",Font.PLAIN,16));

        startBtn.addActionListener(new ActionListener() {
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                startFourCamera(jt.getText());
                //JOptionPane.showMessageDialog(frame, "开始录制了~");
            }
        });

        endBtn.addActionListener(new ActionListener() {
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                stopFourCamera();
                // JOptionPane.showMessageDialog(frame, "结束录制了~");
            }
        });

        jp.add(label);
        jp.add(jt);
        jp.add(startBtn);
        jp.add(endBtn);
        frame.add(jp);
        frame.setBounds(300, 200, 600, 300);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

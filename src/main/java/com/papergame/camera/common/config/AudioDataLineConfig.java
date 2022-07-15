package com.papergame.camera.common.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AudioDataLineConfig
 * @Description TODO
 * @Author v-xupengyuan
 * @Date 2022/6/27 17:04
 * @Version 1.0
 */
@Configuration
public class AudioDataLineConfig {
    @Resource
    private ApplicationArguments arguments;
    @Bean
    public TargetDataLine createLine() throws LineUnavailableException {
        String[] sourceArgs = arguments.getSourceArgs();
        int num= Integer.parseInt(sourceArgs[0]);
        List<TargetDataLine> result = new ArrayList<>();

        // 音频格式的参数
        AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);

        // 获取数据线所需的参数
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

        // 从音频捕获设备取得其数据的数据线，之后的音频数据就从该数据线中获取
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

     /*   Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if( mixer.isLineSupported(Port.Info.MICROPHONE) ) {
                Line.Info[] targetLineInfo = mixer.getTargetLineInfo();
                for (Line.Info info : targetLineInfo) {
                    Line line1 = AudioSystem.getLine(info);
                    line1.open();
                }
            }

        }*/
        line.open(audioFormat);

        // 数据线与音频数据的IO建立联系
        line.start();


        return line;
    }

}

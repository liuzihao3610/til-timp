package com.tilchina.timp;

import com.tilchina.wind.generate.Generator;
import com.tilchina.wind.generate.vo.GenerateConfig;

/**
 *
 * @version 1.0.0 2018/3/21
 * @author WangShengguang
 */
public class GenerateCodeUtil {

    public static void main(String[] args) {

        // path: C:\Users\YuSong Xue\Desktop\Git\TIMP
        GenerateConfig config =  new GenerateConfig("TIMP",
                "C:\\Users\\DELL\\IdeaProjects\\til-timp",
                "bd_settle_route,bd_settle_route_b",
                "jdbc:mysql://10.8.11.21:3306/timpdev?useUnicode=true&characterEncoding=UTF-8",
                "dev","Til@2018");
        config.setAuthor("LiuShuqi");
        config.setVersion("1.0.0");

        config.setGenerateContraller(true);
        try {
            Generator.generate(config,"bd_settle_route","bd_settle_route_b");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

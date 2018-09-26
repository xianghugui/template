package com.base.web.controller;

import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class test {
    public static void main(String[] args) {
        try {
            String url = System.getProperty("user.dir") + "\\mirror-object\\src\\main\\resources\\faceFeature";
            System.out.println(test.class.getResource("/faceFeature").getPath());
            String[] args1 = new String[]{"python", url + "\\test.py"};
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

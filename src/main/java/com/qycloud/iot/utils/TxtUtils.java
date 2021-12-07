package com.qycloud.iot.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @Author: WangHao
 * @Date: 2021/11/24/11:17
 * @Description:
 */
@Slf4j
public class TxtUtils {
    public static String readTxt(File file)   {
        InputStreamReader in = null;
        BufferedReader br = null;
        try {
            String txt = "";
            in = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(in);
            StringBuffer content = new StringBuffer();
            while ((txt = br.readLine()) != null) {
                content = content.append(txt);
            }
            return content.toString();
        } catch (Exception e) {
            log.error("读取配置文件异常:", e);
            return null;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String readTxt(String filePath) {

        String content = TxtUtils.readTxt(new File(filePath));
        return content;


    }

    public static void main(String[] args) {

//            String s1 = TxtUtils.readTxt(new File("D:\\wh\\共享盘\\编号.txt"));
            String s1 = TxtUtils.readTxt("D:\\wh\\共享盘\\编号.txt");

            System.out.println(s1);
//            String s2 = TxtUtils.readTxt(ResourceUtils.getFile("classpath:du.txt"));

    }
}

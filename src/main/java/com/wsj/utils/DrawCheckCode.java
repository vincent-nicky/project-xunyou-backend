package com.wsj.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class DrawCheckCode {

    private String checkCode;

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    //随机产生颜色
    public Color getColor(){
        Random random = new Random();
        //获取0-255随机值
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r,g,b);
    }

    //产生验证码值
    public String  getNum(){
        //原来是0-8999，+1000后变成1000-9999
        int ran = (int)(Math.random()*9000)+1000;
        return String.valueOf(ran);
    }

    public BufferedImage doDraw(){
        //绘制验证码
        //参数：长，宽，图片类型
        BufferedImage image = new BufferedImage(80, 30, BufferedImage.TYPE_INT_RGB);
        //画笔
        Graphics graphics = image.getGraphics();
        //画长方形，坐标从0,0,到80，30
        graphics.fillRect(0,0,80,30);
        //绘制50条干扰条
        for(int i=0;i<50;i++){
            Random random = new Random();
            int xBegin = random.nextInt(80);
            int yBegin = random.nextInt(30);
            int xEnd = random.nextInt(xBegin +10);
            int yEnd = random.nextInt(yBegin +10);
            //画笔颜色，随机
            graphics.setColor(getColor());
            //绘制线条
            graphics.drawLine(xBegin,yBegin,xEnd,yEnd);
        }

        //绘制验证码
        //字体加粗，变大
        graphics.setFont(new Font("seif",Font.BOLD,20));
        //画笔颜色
        graphics.setColor(Color.BLACK);
        //得到随机取得的数字
        String checode = getNum();
        checkCode = checode;
        //在数字中间加上空格分开
        StringBuilder buffer = new StringBuilder();
        for(int i=0;i<checode.length();i++){
            buffer.append(checode.charAt(i)).append(" ");
        }
        //在长方形里绘制验证码，15,20是起始坐标
        graphics.drawString(buffer.toString(),15,20);
        return image;
    }

}

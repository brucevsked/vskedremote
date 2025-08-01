package com.vsked.remote;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GlobalObj {

	private static final Logger log = LoggerFactory.getLogger(GlobalObj.class);
	
	private static Robot robot=null;
	private static Rectangle re = null;

	public static String getBase64Data(byte[] dt){
		return Base64.encodeBase64String(dt);
	}
	
	public static Robot getRobot(){
		try {
			return robot==null?new Robot():robot;
		} catch (AWTException e) {
			log.error("getRobot error:",e);
			return null;
		}
	}
	
	public static Rectangle getRe(){
		return re==null?new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()):re;
	}
	
	public static String getImg(){
		byte[] b = null;
		ByteArrayOutputStream os=new ByteArrayOutputStream();//新建流。
		BufferedImage bi=getRobot().createScreenCapture(getRe());//BufferedImage对象。
		try {
			ImageIO.write(bi, "png", os);
			b=os.toByteArray();
		} catch (IOException e) {
			log.error("getImg error:",e);
		}
		return "data:image/jpg;base64,"+getBase64Data(b);
	}
	
}

/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.service.provider;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.kit.StrKit;

/**
 * @author wangjun
 * 2018年9月19日
 */
public class JsoupTest {

	public static void main(String[] args) {
		
		String posterHtml = "<img style=\"position: relative; left: 128px; top: 44px;\" width=\"60px\" height=\"60px\" class=\"img-circle\" src=\"http://wx.qlogo.cn/mmopen/NLOzukWr2a1LaMmJWASVHPZiayia0OaeibKY9ICYqibH08RP4O9qXldeSNwbwJic0wu1Gc8ScT5EoyDazI8YnyfRYc9MdvXXOSp8R/0\"/>"
            	+"<span style=\"position: absolute; width: 150px; height: auto;left: 30px; top: 63px;\">#微信昵称#</span>"
            	+"<div class=\"text_tarea\" style=\"position: absolute; width: 260px; height: auto;left: 30px; top: 183px;\">"
            	+"<textarea style=\"width: 250px; height: 70px;resize:none;\" placeholder=\"文案示例:我刚刚免费领了XXX礼品哦，不要告诉太多人，嘘。。\"></textarea>"
            	+"</div>"
            	+"<img class=\"qrcode\" style=\"top: 250px; left: 19px;\" alt=\"\" width=\"150px\" height=\"150px\" src=\"#(webctx)/resources/img/paramsqrcode.png\"/>"
            	+"<div class=\"text_input\" style=\"position: absolute; width: 260px; height: auto;left: 30px; top: 300px;\">"
            	+"<input style=\"width: 250px;\" placeholder=\"文案示例:每天500份礼品等你拿\"></input>"
            	+"</div>";
		
		Document doc = Jsoup.parse(posterHtml);
		
		//获取微信头像位置信息
		Elements elements = doc.select(".img-circle");
		Element e = elements.get(0);
		String headerStyle = e.attr("style");
		System.out.println("=======headerStyle:" + headerStyle);
		Integer [] pos = getPos(headerStyle);
		System.out.println("=======left:" + pos[0] + ",top:" + pos[1]);
		DrawImg headerImg = new DrawImg(pos[1], pos[0], e.attr("src"));
		
		//获取微信昵称位置信息
		e = doc.selectFirst("span");
		String nickStyle = e.attr("style");
		System.out.println("=======nickStyle:" + nickStyle);
		pos = getPos(nickStyle);
		System.out.println("=======left:" + pos[0] + ",top:" + pos[1]);
		DrawText nickText = new DrawText(pos[1], pos[0], "做个好农民");
		
		//获取活动说明文案
		e = doc.selectFirst(".text_tarea");
		String textareaStyle = e.attr("style");
		System.out.println("=======textareaStyle:" + textareaStyle);
		pos = getPos(textareaStyle);
		System.out.println("=======left:" + pos[0] + ",top:" + pos[1]);
		DrawText contentText = new DrawText(pos[1], pos[0], "我刚刚免费领了一个iphone7 plus哦，超级漂亮，好喜欢。。。");
		
		
		//获取二维码位置信息
		e = doc.selectFirst(".qrcode");
		String qrcodeStyle = e.attr("style");
		System.out.println("=======qrcodeStyle:" + qrcodeStyle);
		pos = getPos(qrcodeStyle);
		System.out.println("=======left:" + pos[0] + ",top:" + pos[1]);
		DrawImg qrcodeImg = new DrawImg(pos[1], pos[0], "http://www.dbumama.com/resources/weixin.png");
		
		//获取活动简要描述信息
		e = doc.selectFirst(".text_input");
		String inputStyle = e.attr("style");
		System.out.println("=======inputStyle:" + inputStyle);
		pos = getPos(inputStyle);
		System.out.println("=======left:" + pos[0] + ",top:" + pos[1]);
		DrawText descText = new DrawText(pos[1], pos[0], "每天500份大礼领不停");
		
		//开始画图
		BufferedImage img = new BufferedImage(533, 800, BufferedImage.TYPE_INT_RGB);//创建图片
		Graphics g = img.getGraphics();//开启画图
		
		try {
			//背景图
			BufferedImage bg = ImageIO.read(new URL("http://image.dbumama.com/upload/image/20180919/1537349830621.jpg"));
			//头像
	        BufferedImage header = ImageIO.read(new URL(headerImg.getUrl()));//读取互联网图片
	        //二维码
	        BufferedImage qrcode = ImageIO.read(new URL(qrcodeImg.getUrl()));//读取本地图片
	        
	        g.drawImage(bg.getScaledInstance(533, 800, Image.SCALE_DEFAULT), 0, 0, null); 		// 绘制缩小后的图   
            g.drawImage(header.getScaledInstance(85, 85, Image.SCALE_DEFAULT), headerImg.getLeft(), headerImg.getTop(), null); 	// 绘制缩小后的图   
            g.drawImage(qrcode.getScaledInstance(265, 265, Image.SCALE_DEFAULT), qrcodeImg.getLeft(), qrcodeImg.getTop(), null); 	// 绘制缩小后的图   
	        
            
            //写昵称
            g.setColor(Color.black);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 24));
            g.drawString(nickText.getContent(), nickText.getLeft(), nickText.getTop());//绘制文字
            
            
            //写活动文案
            g.setColor(Color.black);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 24));
            g.drawString(contentText.getContent(), contentText.getLeft(), contentText.getTop());//绘制文字

            
            //写活动描述
            g.setFont(new Font("微软雅黑", Font.PLAIN, 36));
            g.setColor(Color.decode("0x17994f"));
            g.drawString(descText.getContent(), descText.getLeft(), descText.getTop());
            
            ImageIO.write(img, "jpg", new File("C:\\Users\\wangjun\\Desktop\\3.jpg"));
            
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally {
			g.dispose();
		}
	}
	
	private static Integer [] getPos(String info){
		if(StrKit.isBlank(info)) return null;
		
		if(!info.contains(";")) return null;
		
		String [] resStrArr = new String [2];
		
		String [] posStrArr = info.split(";");
		for(String posStr : posStrArr){
			String pArr [] = posStr.split(":");
			if(pArr[0].trim().equals("left")){
				resStrArr[0] = pArr[1];
			}
			
			if(pArr[0].trim().equals("top")){
				resStrArr[1] = pArr[1];
			}
		}
	
		Integer [] pos = new Integer [2];
		
		Integer left = Integer.valueOf(resStrArr[0].replaceAll("px", "").trim());
		Integer top = Integer.valueOf(resStrArr[1].replaceAll("px", "").trim());
		
		pos[0] = left;
		pos[1] = top;
		
		return pos;
	}
	
	public static void draw(){
		BufferedImage img = new BufferedImage(533, 800, BufferedImage.TYPE_INT_RGB);//创建图片
		Graphics g = img.getGraphics();//开启画图
		try {
			BufferedImage bg = ImageIO.read(new File("C://Users//wangjun//Desktop//tpl//tpl.jpg"));
			//读取本地图片
	        BufferedImage logo = ImageIO.read(new URL("http://wx.qlogo.cn/mmopen/NLOzukWr2a1LaMmJWASVHPZiayia0OaeibKY9ICYqibH08RP4O9qXldeSNwbwJic0wu1Gc8ScT5EoyDazI8YnyfRYc9MdvXXOSp8R/0"));//读取互联网图片
	        BufferedImage er = ImageIO.read(new URL("http://a.hiphotos.baidu.com/image/pic/item/96dda144ad3459825a7754f50ef431adcaef84dc.jpg"));//读取本地图片
	        
	        g.drawImage(bg.getScaledInstance(533, 800, Image.SCALE_DEFAULT), 0, 0, null); 		// 绘制缩小后的图   
            g.drawImage(logo.getScaledInstance(85, 85, Image.SCALE_DEFAULT), 93, 35, null); 	// 绘制缩小后的图   
            g.drawImage(er.getScaledInstance(265, 265, Image.SCALE_DEFAULT), 136, 453, null); 	// 绘制缩小后的图   
	        
            g.setColor(Color.black);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 24));
            g.drawString("我是张勇", 214, 58);//绘制文字
            g.drawString("我为掌控天下代言", 214, 100);

            g.setFont(new Font("微软雅黑", Font.PLAIN, 36));
            g.setColor(Color.decode("0x17994f"));
            g.drawString("掌控天下", 533/2-36*4/2, 200);
            
            ImageIO.write(img, "jpg", new File("C:\\Users\\wangjun\\Desktop\\3.jpg"));
            
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			g.dispose();
		}
	}
	
	public static class DrawImg {
		int top;
		int left;
		String url;
		
		/**
		 * @param top
		 * @param left
		 * @param url
		 */
		public DrawImg(int top, int left, String url) {
			super();
			this.top = top;
			this.left = left;
			this.url = url;
		}

		/**
		 * @return the top
		 */
		public int getTop() {
			return top;
		}

		/**
		 * @param top the top to set
		 */
		public void setTop(int top) {
			this.top = top;
		}

		/**
		 * @return the left
		 */
		public int getLeft() {
			return left;
		}

		/**
		 * @param left the left to set
		 */
		public void setLeft(int left) {
			this.left = left;
		}

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param url the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}
		
	}
	
	public static class DrawText {
		int top;
		int left;
		String content;
		
		/**
		 * @param top
		 * @param left
		 * @param content
		 */
		public DrawText(int top, int left, String content) {
			super();
			this.top = top;
			this.left = left;
			this.content = content;
		}

		/**
		 * @return the top
		 */
		public int getTop() {
			return top;
		}

		/**
		 * @param top the top to set
		 */
		public void setTop(int top) {
			this.top = top;
		}

		/**
		 * @return the left
		 */
		public int getLeft() {
			return left;
		}

		/**
		 * @param left the left to set
		 */
		public void setLeft(int left) {
			this.left = left;
		}

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}
		
	}
}

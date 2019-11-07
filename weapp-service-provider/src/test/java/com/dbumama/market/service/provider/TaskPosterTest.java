package com.dbumama.market.service.provider;

/**
 * @author wangjun
 * 2018年9月19日
 */
public class TaskPosterTest {

	public static void main(String[] args) {
//		BufferedImage img = new BufferedImage(533, 800, BufferedImage.TYPE_INT_RGB);//创建图片
//		Graphics g = img.getGraphics();//开启画图
//		try {
//			BufferedImage bg = ImageIO.read(new File("C://Users//wangjun//Desktop//tpl//tpl.jpg"));
//			//读取本地图片
//	        BufferedImage logo = ImageIO.read(new URL("http://wx.qlogo.cn/mmopen/NLOzukWr2a1LaMmJWASVHPZiayia0OaeibKY9ICYqibH08RP4O9qXldeSNwbwJic0wu1Gc8ScT5EoyDazI8YnyfRYc9MdvXXOSp8R/0"));//读取互联网图片
//	        BufferedImage er = ImageIO.read(new URL("http://a.hiphotos.baidu.com/image/pic/item/96dda144ad3459825a7754f50ef431adcaef84dc.jpg"));//读取本地图片
//	        
//	        g.drawImage(bg.getScaledInstance(533, 800, Image.SCALE_DEFAULT), 0, 0, null); 		// 绘制缩小后的图   
//            g.drawImage(logo.getScaledInstance(85, 85, Image.SCALE_DEFAULT), 93, 35, null); 	// 绘制缩小后的图   
//            g.drawImage(er.getScaledInstance(265, 265, Image.SCALE_DEFAULT), 136, 453, null); 	// 绘制缩小后的图   
//	        
//            g.setColor(Color.black);
//            g.setFont(new Font("微软雅黑", Font.PLAIN, 24));
//            g.drawString("我是张勇", 214, 58);//绘制文字
//            g.drawString("我为掌控天下代言", 214, 100);
//
//            g.setFont(new Font("微软雅黑", Font.PLAIN, 36));
//            g.setColor(Color.decode("0x17994f"));
//            g.drawString("掌控天下", 533/2-36*4/2, 200);
//            
//            ImageIO.write(img, "jpg", new File("C:\\Users\\wangjun\\Desktop\\3.jpg"));
//            
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally {
//			g.dispose();
//		}
		
		/*
		String s  = "我刚刚免费领取了一台华为手机哦，嘘。。。不要告诉太多人台华为手机哦，嘘。。。不要告诉太多人台华为手机哦，嘘。。。不要告诉太多人台华为手机哦，嘘。。。不要告诉太多人台华为手机哦，嘘。。。不要告诉太多人";
		
		System.out.println(textHuanHang(s));
		*/
		
		String rgb = "rgb(213, 64, 54)";
		
		rgb = rgb.substring(4, rgb.length()-1);
		
		System.out.println(rgb);
	}
	
	static String textHuanHang(String s){

		StringBuffer s1=new StringBuffer(s);

		int count = 1;

		for(int index=15;index * count<s1.length(); count ++){

			s1.insert(count*index, '\n');

		}
		return s1.toString();
	}
	
}

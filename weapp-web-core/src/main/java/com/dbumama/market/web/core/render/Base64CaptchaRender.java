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
package com.dbumama.market.web.core.render;

import com.dbumama.market.utils.ResultUtil;
import com.jfinal.captcha.Captcha;
import com.jfinal.captcha.CaptchaManager;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.render.RenderException;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author wangjun
 * 2018年5月6日
 */
public class Base64CaptchaRender extends CaptchaRender{

	/* (non-Javadoc)
	 * @see com.jfinal.captcha.CaptchaRender#render()
	 */
	@Override
	public void render() {
		Captcha captcha = createCaptcha();
		CaptchaManager.me().getCaptchaCache().put(captcha);
		
		Cookie cookie = new Cookie(captchaName, captcha.getKey());
		cookie.setMaxAge(-1);
		cookie.setPath("/");
		response.addCookie(cookie);
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		
		request.getSession().setAttribute("captcha", captcha.getValue());
		
		ServletOutputStream sos = null;
		try {
			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			drawGraphic(captcha.getValue(), image);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        try {
				ImageIO.write(image, "png", baos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Base64 base64 = new Base64();
	        final String result = "data:image/jpg;base64," + base64.encodeAsString(baos.toByteArray());
	        PrintWriter writer = response.getWriter();
	        writer.write(JsonKit.toJson(ResultUtil.genSuccessResult(result)));
            writer.flush();
		} catch (IOException e) {
			if (getDevMode()) {
				throw new RenderException(e);
			}
		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			if (sos != null) {
				try {sos.close();} catch (IOException e) {LogKit.logNothing(e);}
			}
		}
		
	}
	
	
}

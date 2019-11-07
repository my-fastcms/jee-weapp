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
package com.dbumama.market.utils;

import com.jfinal.kit.StrKit;

/**
 * @author wangjun
 * 2018年10月11日
 */
public final class JsoupKit {
	
	final static String WIDTH = "width";
	final static String HEIGHT = "height";
	final static String TOP = "top";
	final static String LEFT = "left";
	
	final static String FONTFAMILY = "font-family";
	final static String FONTSIZE = "font-size";
	final static String FONTCOLOR = "color";

	public static Integer getStyleAttrInt(String style, String attrName){
		if(StrKit.isBlank(style)) return null;
		
		if(!style.contains(";")) return null;
		
		String [] attrArr = style.split(";");
		for(String attr : attrArr){
			String a [] = attr.split(":");
			if(a[0].trim().equals(attrName)){
				return Double.valueOf(a[1].replace("px", "").trim()).intValue();
			}
		}
		return null;
	}
	
	public static String getStyleAttrStr(String style, String attrName){
		if(StrKit.isBlank(style)) return null;
		
		if(!style.contains(";")) return null;
		
		String [] attrArr = style.split(";");
		for(String attr : attrArr){
			String a [] = attr.split(":");
			if(a[0].trim().equals(attrName)){
				return a[1].trim();
			}
		}
		return null;
	}
	
	public static Integer getElmWidth(String style){
		return getStyleAttrInt(style, WIDTH);
	}
	
	public static Integer getElmHeight(String style){
		return getStyleAttrInt(style, HEIGHT);
	}
	
	public static Integer getElmTop(String style){
		return getStyleAttrInt(style, TOP);
	}
	
	public static Integer getElmLeft(String style){
		return getStyleAttrInt(style, LEFT);
	}
	
	public static String getElmColor(String style){
		return getStyleAttrStr(style, FONTCOLOR);
	}
	
	public static String getElmFontFamily(String style){
		return getStyleAttrStr(style, FONTFAMILY);
	}
	
	public static Integer getElmFontSize(String style){
		return getStyleAttrInt(style, FONTSIZE);
	}
}

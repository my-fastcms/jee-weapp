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
package com.dbumama.market.service.utils;

/**
 * @author wangjun 2017年12月25日
 */
public final class LocationUtil {

	/**
	 * 根据用户的起点和终点经纬度计算两点间距离，此距离为相对较短的距离，单位米。
	 * 
	 * @param start
	 *            起点的坐标
	 * @param end
	 *            终点的坐标
	 * @return
	 */
	public static double calculateLineDistance(LngLat start, LngLat end) {
		if ((start == null) || (end == null)) {
			throw new IllegalArgumentException("非法坐标值，不能为null");
		}
		double d1 = 0.01745329251994329D;
		double d2 = start.longitude;
		double d3 = start.latitude;
		double d4 = end.longitude;
		double d5 = end.latitude;
		d2 *= d1;
		d3 *= d1;
		d4 *= d1;
		d5 *= d1;
		double d6 = Math.sin(d2);
		double d7 = Math.sin(d3);
		double d8 = Math.cos(d2);
		double d9 = Math.cos(d3);
		double d10 = Math.sin(d4);
		double d11 = Math.sin(d5);
		double d12 = Math.cos(d4);
		double d13 = Math.cos(d5);
		double[] arrayOfDouble1 = new double[3];
		double[] arrayOfDouble2 = new double[3];
		arrayOfDouble1[0] = (d9 * d8);
		arrayOfDouble1[1] = (d9 * d6);
		arrayOfDouble1[2] = d7;
		arrayOfDouble2[0] = (d13 * d12);
		arrayOfDouble2[1] = (d13 * d10);
		arrayOfDouble2[2] = d11;
		double d14 = Math.sqrt((arrayOfDouble1[0] - arrayOfDouble2[0]) * (arrayOfDouble1[0] - arrayOfDouble2[0])
				+ (arrayOfDouble1[1] - arrayOfDouble2[1]) * (arrayOfDouble1[1] - arrayOfDouble2[1])
				+ (arrayOfDouble1[2] - arrayOfDouble2[2]) * (arrayOfDouble1[2] - arrayOfDouble2[2]));

		return (Math.asin(d14 / 2.0D) * 12742001.579854401D);
	}
	
	public static void main(String[] args) {
		LngLat start = new LngLat(120.754788, 31.332968);
	    LngLat end = new LngLat(120.750496, 31.334894);
	    System.err.println(LocationUtil.calculateLineDistance(start, end) / 1000);
	}

}

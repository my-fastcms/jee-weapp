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

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjun 2018年10月13日
 */
public class RedpackGenTest {
	// 最小红包额度
	private static final int MINMONEY = 1;
	// 最大红包额度
	private static final int MAXMONEY = 200 * 100;
	// 每个红包最大是平均值的倍数
	private static final double TIMES = 2.1;

	/**
	 * @param money
	 * @param count
	 * @return
	 * @Author:lulei
	 * @Description: 拆分红包
	 */
	public List<Integer> splitRedPackets(int money, int count) {
		if (!isRight(money, count)) {
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		// 红包最大金额为平均金额的TIMES倍
		int max = (int) (money * TIMES / count);
		max = max > MAXMONEY ? MAXMONEY : max;
		for (int i = 0; i < count; i++) {
			int one = random(money, MINMONEY, max, count - i);
			list.add(one);
			money -= one;
		}
		return list;
	}

	/**
	 * @param money
	 * @param minS
	 * @param maxS
	 * @param count
	 * @return
	 * @Author:lulei
	 * @Description: 随机红包额度
	 */
	private int random(int money, int minS, int maxS, int count) {
		// 红包数量为1，直接返回金额
		if (count == 1) {
			return money;
		}
		// 如果最大金额和最小金额相等，直接返回金额
		if (minS == maxS) {
			return minS;
		}
		int max = maxS > money ? money : maxS;
		// 随机产生一个红包
		int one = ((int) Math.rint(Math.random() * (max - minS) + minS)) % max + 1;
		int money1 = money - one;
		// 判断该种分配方案是否正确
		if (isRight(money1, count - 1)) {
			return one;
		} else {
			double avg = money1 / (count - 1);
			if (avg < MINMONEY) {
				// 递归调用，修改红包最大金额
				return random(money, minS, one, count);
			} else if (avg > MAXMONEY) {
				// 递归调用，修改红包最小金额
				return random(money, one, maxS, count);
			}
		}
		return one;
	}

	/**
	 * @param money
	 * @param count
	 * @return
	 * @Author:lulei
	 * @Description: 此种红包是否合法
	 */
	private boolean isRight(int money, int count) {
		double avg = money / count;
		if (avg < MINMONEY) {
			return false;
		}
		if (avg > MAXMONEY) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		
		
		for(int i=0;i<100;i++){
			int c = (int) (Math.random() * 100);
			if(c%2 == 0){
				System.out.println("["+i+"]," + c + ",偶数");
			}else{
				System.out.println("["+i+"]," + c + ",奇数");
			}
		}
		
	}
}

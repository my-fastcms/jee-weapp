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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author wangjun
 * 2018年10月13日
 */
public final class RedpackGenKit {

	// 最小红包额度 单位分
	private int min = 100;
	// 最大红包额度 单位分
	private int max = 200 * 100;
	//红包平均值倍数
	private int times = 3;
	
	public RedpackGenKit(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	public List<Integer> splitRedPackets(int money, int count) {
		if (!check(money, count)) {
			return null;
		}
		
		int avg = money/count;//平均值
		
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < count; i++) {
			int randomCount = 0;
			int one = random(money, this.min, this.max, count - i, randomCount);
			list.add(one);
			money -= one;
		}
		
		Collections.sort(list, new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;//降序排列
			}
		});

		for(int i=0, j=0; i<list.size();i++){
			Integer redpack = list.get(i);
			if(redpack < this.min){
				//如果比最小的还要小，就需要补
				Integer _max = list.get(j);
				int diff = this.min - redpack;//求差值
				int _newmax = _max-diff;
				if(_newmax > this.min){
					System.out.println("==============_max:" + _max + ",avg:" + avg);
					//足够大的红包就一直补给别人
					list.set(i, redpack + diff);
					list.set(j, _newmax);
				}else{
					//否则由下一个补
					_max = list.get(j+1);
					list.set(i, redpack + diff);
					list.set(j+1, _max - diff);
					
					j++;
				}
				
				System.out.println("由第" + j + "个补" + diff);
			}
		}
		
		return list;
	}

	/**
	 * @param money
	 * @param minS
	 * @param maxS
	 * @param count
	 * @return
	 * @Description: 随机红包额度
	 */
	private int random(int money, int minS, int maxS, int count, int randomCount) {
		
		if(money <=0) return 0;
		
		// 红包数量为1，直接返回金额
		if (count == 1) {
			return money;
		}
		// 如果最大金额和最小金额相等，直接返回金额
		if (minS == maxS && minS>=this.min && minS<=this.max) {
			return minS;
		}
		int max = maxS > money ? money : maxS;
		// 随机产生一个红包
		int one = ((int) Math.rint(Math.random() * (max - minS) + minS)) % max + 1;
		int money1 = money - one;
		
		double avg = money1 / (count - 1);
		
		// 判断该种分配方案是否正确
		if (one >= this.min && one<=this.max && one<=times*avg) {
			return one;
		}
		
		randomCount ++;//不符合要求的红包，生成10次，10次还不符合要求就返回，防止出现死循环
		if(randomCount >= 10) return one;
		
		if (avg < min) {
			// 递归调用，修改红包最大金额
			return random(money, minS, one, count, randomCount);
		} else if (avg > max) {
			// 递归调用，修改红包最小金额
			return random(money, one, maxS, count, randomCount);
		}
		
		return one;
	}

	/**
	 * @param money
	 * @param count
	 * @return
	 * @Description: 此种红包是否合法
	 */
	private boolean check(int money, int count) {
		if(this.min<=0 || this.max <=0 || this.min > this.max || count <=0 || money <=0) return false;
		double avg = money / count;
		
		if(this.min + 10 > avg) return false;
		
		return avg >= min && avg <= max;
	}
	
	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}
	
	public static void main(String[] args) {
		RedpackGenKit redpack = new RedpackGenKit(40,9800);
		/*for(int i=0; i<100000; i++){
			List<Integer> redpacks = redpack.splitRedPackets(500, 5);
			if(redpacks != null){
				int money = 0;
				for(Integer rp : redpacks){
					System.out.print(rp + ",");
					money += rp;
				}
				System.out.println("=========================第"+(i+1)+"次生成，总金额：" + money + ",红包个数:" + redpacks.size());				
			}
		}*/
		
		List<Integer> redpacks = redpack.splitRedPackets(300000, 5000);
		if(redpacks == null) {
			System.out.println("生成红包错误，请调整金额");
			return;	
		}
		
		/*Collections.sort(redpacks, new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});*/
		
		
//		for(int m=0;m<100000;m++){
			int totalMoney = 0;
			for(int i=0;i<redpacks.size();i++){
				totalMoney += redpacks.get(i);
				System.out.println("=========================第"+(i+1)+"次生成，金额：" + redpacks.get(i));
			}
			System.out.println("===========count:" + redpacks.size() + ",money:" + totalMoney);
//		}
		
		
		/*List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i<20; i++){
			list.add(new Random().nextInt(200));
		}
		
		for(Integer i : list){
			System.out.println("===" + i);
		}
		
		System.out.println("=======================排序前================================");
		
		Collections.sort(list, new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});
		
		for(Integer i : list){
			System.out.println("===" + i);
		}*/
	}

	
}

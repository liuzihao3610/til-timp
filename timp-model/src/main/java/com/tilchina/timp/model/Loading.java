package com.tilchina.timp.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Xiahong
 *
 */
@Getter
@Setter
@ToString
public class Loading  extends SuperVO {
	
	private Long sendUnitId;	//起始地
	private Long receiveUnitId;	//目的地
	private Integer CarCount;	//装载数量
	private Integer handingCount;	//装卸数量
    private Double driving;   //行驶公里数
    
    /**
     * 通过距离排序
     * @param transportPlans
     * @return
     */
	public static List<Loading> sort (List<Loading> loadings) {
		 Collections.sort(loadings, new Comparator<Loading>(){
		    /*    
			 * int compare(Person p1, Person p2) 返回一个基本类型的整型，
	         * 返回负数表示：p1 小于p2，
	         * 返回0 表示：p1和p2相等，
	         * 返回正数表示：p1大于p2*/
		        public int compare(Loading p1, Loading p2) {
		            //按照Person的距离进行升序排列
		            if(p1.getDriving() > p2.getDriving()){
		                return 1;
		            }
		            if(p1.getDriving() == p2.getDriving()){
		                return 0;
		            }
		            return -1;
		        }
		    });
		return loadings;
	 }
	
}

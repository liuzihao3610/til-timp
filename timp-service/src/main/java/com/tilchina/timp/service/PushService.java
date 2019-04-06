package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.Push;

/**
* 极光推送
*
* @version 1.0.0
* @author Xiahong
*/
public interface PushService {
	
    public Push queryById(Long id);
    
    public PageInfo<Push> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<Push> queryList(Map<String, Object> map);
    
	public String addPush(Push record);
	
	public void push(Push record);
	
}

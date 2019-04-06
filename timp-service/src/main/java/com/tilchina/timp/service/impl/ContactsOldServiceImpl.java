package com.tilchina.timp.service.impl;

import com.tilchina.timp.mapper.ContactsOldMapper;
import com.tilchina.timp.service.ContactsOldService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.ContactsOld;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.UnitService;
import com.tilchina.timp.vo.ContactsVO;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;

/**
* 收发货单位联系人
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class ContactsOldServiceImpl extends BaseServiceImpl<ContactsOld> implements ContactsOldService {

	@Autowired
    private ContactsOldMapper contactsmapper;
	
	@Autowired
	private UnitService unitService;
	
	@Override
	protected BaseMapper<ContactsOld> getMapper() {
		return contactsmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(ContactsOld contacts) {
		StringBuilder s = new StringBuilder();
		contacts.setCreateTime(new  Date());
        s.append(CheckUtils.checkDate("YES", "createTime", "创建时间", contacts.getCreateTime()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", contacts.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(ContactsOld contacts) {
        StringBuilder s = checkNewRecord(contacts);
        s.append(CheckUtils.checkPrimaryKey(contacts.getContactsId()));
		return s;
	}
	
	
	@Override
    public void add(ContactsOld record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            contactsmapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }
    }
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		contactsmapper.deleteList(data);
		
	}
	

 	@Override
 	@Transactional
    public void update(List<ContactsOld> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		ContactsOld record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	contactsmapper.updateByPrimaryKeySelective(records.get(i));
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }

    }

	@Transactional
    public void adds(List<ContactsOld> contacts) {
        log.debug("addBatch: {}",contacts);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < contacts.size(); i++) {
                ContactsOld contact = contacts.get(i);
                StringBuilder check = checkNewRecord(contact);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
                contactsmapper.insert(contact);
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            
            
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }
    }
	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(ContactsOld record) {
        log.debug("updateSelective: {}",record);
        try {
        	StringBuilder s = new StringBuilder();
            try {
                s = s.append(CheckUtils.checkLong("NO", "contactsId", "联系人ID", record.getContactsId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                ContactsOld contacts=contactsmapper.selectByPrimaryKey(record.getContactsId());
                if (contacts==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                contactsmapper.updateByPrimaryKeySelective(record);
            } catch (Exception e) {
            	throw new BusinessException(e.getMessage());
            }
            
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }
    }
	
	//通过ID查询
	@Override
	@Transactional
    public ContactsOld queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "contactsId", "联系人ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return contactsmapper.selectByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
    }
	//通过ID删除
	 @Override
	 @Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "contactsId", "联系人ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            contactsmapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }

	@Override
	@Transactional
	public List<ContactsOld> queryByUnitId(Long unitId) {
		try {
			if (unitId==null) {
				throw new BusinessException("参数为空");
			}
			Unit unit=unitService.queryById(unitId);
			if (unit==null) {
				throw new BusinessException("收发货单位不存在");
			}
			
			return contactsmapper.queryByUnitId(unitId);
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	@Transactional
	public ContactsVO appQueryByUnitId(Long startUnitId,Long EndUnitId) {
		ContactsVO contactsVO = null;
		try {
			if (startUnitId == null) {
				throw new BusinessException("参数为空");
			}
			Unit unit = unitService.queryById(startUnitId);
			if (unit == null) {
				throw new BusinessException("收货单位不存在");
			}
			contactsVO = new ContactsVO();
			List<ContactsOld> contactss = contactsmapper.queryByUnitId(startUnitId);
			if( contactss.size() > 0 ) {
				contactsVO.setStartUnitPhone(contactss.get(0).getRefPhone());
				contactsVO.setStartUnitContactsName((contactss.get(0).getRefContactsName()));
			}
			contactss = contactsmapper.queryByUnitId(EndUnitId);
			if(contactsmapper.queryByUnitId(EndUnitId).size() > 0 ) {
				contactsVO.setEndUnitPhone(contactss.get(0).getRefPhone());
				contactsVO.setEndUnitContactsName((contactss.get(0).getRefContactsName()));
			}
		} catch (Exception e) {
			throw e;
		}
		return contactsVO;
		
	}
    @Override
    public List<ContactsVO> queryByStartUnitId(Long startUnitId) {
        List<ContactsVO> ContactsVOs = new ArrayList<>();
        try {
            if (startUnitId == null) {
                throw new BusinessException("参数为空");
            }
            Unit unit = Optional.ofNullable(unitService.queryById(startUnitId))
                    .orElseThrow(() -> new BusinessException("没有找到收发货单位id:" + startUnitId + "的收发货单位信息"));

            List<ContactsOld> contacts = Optional.ofNullable(contactsmapper.queryByUnitId(startUnitId)).orElse(new ArrayList<ContactsOld>());
            contacts.forEach(contacts1 -> {
                ContactsVO cv = new ContactsVO();
                cv.setStartUnitPhone(contacts1.getRefPhone());
                cv.setStartUnitContactsName((contacts1.getRefContactsName()));
                ContactsVOs.add(cv);
            });
        } catch (Exception e) {
            throw e;
        }
        return ContactsVOs;
    }

    @Override
    public List<ContactsVO> queryByEndUnitId(Long endUnitId) {
        List<ContactsVO> ContactsVOs = new ArrayList<>();
        try {
            if (endUnitId == null) {
                throw new BusinessException("参数为空");
            }
            Unit unit = Optional.ofNullable(unitService.queryById(endUnitId))
                    .orElseThrow(() -> new BusinessException("没有找到收发货单位id:" + endUnitId + "的收发货单位信息"));

            List<ContactsOld> contacts = Optional.ofNullable(contactsmapper.queryByUnitId(endUnitId)).orElse(new ArrayList<ContactsOld>());
            contacts.forEach(contacts1 -> {
                ContactsVO cv = new ContactsVO();
                cv.setEndUnitPhone(contacts1.getRefPhone());
                cv.setEndUnitContactsName((contacts1.getRefContactsName()));
                ContactsVOs.add(cv);
            });
        } catch (Exception e) {
            throw e;
        }
        return ContactsVOs;
    }



	/**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<ContactsOld> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<ContactsOld>(contactsmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * 通过收发货单位联系人名称查询
     * @param refUserName
     * @return
     */
    @Override
    @Transactional
    public ContactsOld queryByName(String refUserName) {
        return contactsmapper.queryByName(refUserName);
    }

    @Override
	@Transactional
    public PageInfo<ContactsOld> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try{
            if (map!=null) {
                DateUtil.addTime(map);
            }
            return new PageInfo<ContactsOld>(getMapper().selectList(map));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }


    }
}

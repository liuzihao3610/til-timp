package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.ContactsMapper;
import com.tilchina.timp.model.Contacts;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.ContactsService;
import com.tilchina.timp.service.UnitService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.vo.ContactsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
* 收发货单位联系人
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class ContactsServiceImpl extends BaseServiceImpl<Contacts> implements ContactsService {

	@Autowired
    private ContactsMapper contactsmapper;

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private UnitService unitService;
	
	@Override
	protected BaseMapper<Contacts> getMapper() {

	    return contactsmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Contacts contacts) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "contactsName", "联系人姓名", contacts.getContactsName(), 20));
        s.append(CheckUtils.checkString("YES", "contactsCode", "联系人编码", contacts.getContactsCode(), 20));
        s.append(CheckUtils.checkString("NO", "phone", "手机", contacts.getPhone(), 20));
        s.append(CheckUtils.checkString("YES", "fix", "固定电话", contacts.getFix(), 20));
        s.append(CheckUtils.checkString("YES", "email", "邮箱", contacts.getEmail(), 20));
        s.append(CheckUtils.checkInteger("YES", "qq", "QQ", contacts.getQq(), 10));
        s.append(CheckUtils.checkString("YES", "wechat", "微信号", contacts.getWechat(), 20));
        s.append(CheckUtils.checkString("YES", "wechatId", "微信身份ID(不显示)", contacts.getWechatId(), 20));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", contacts.getCreateDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", contacts.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Contacts contacts) {
        StringBuilder s = checkNewRecord(contacts);
        s.append(CheckUtils.checkPrimaryKey(contacts.getContactsId()));
		return s;
	}

	/**
	 * 通过ID查询
	 * @param id
	 * @return
	 */
	@Override
	public Contacts queryById(Long id) {
		log.debug("query: {}", id);
		return this.getMapper().selectByPrimaryKey(id);
	}

	/**
	 * 分页查询
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageInfo<Contacts> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map, pageNum, pageSize);
		PageHelper.startPage(pageNum, pageSize);
        if (map!=null){
            DateUtil.addTime(map);
        }
        return new PageInfo(contactsmapper.selectList(map));


	}

	/**
	 * 查询所有
	 * @param map
	 * @return
	 */
	@Override
	public List<Contacts> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return this.getMapper().selectList(map);
	}

	/**
	 * 新增联系人
	 * @param record
	 */
	@Override
	@Transactional
	public void add(Contacts record) {
		log.debug("add: {}", record);

		try {
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			Environment environment=Environment.getEnv();
			Contacts contacts=contactsService.getByUnitPhone(record.getUnitId(),record.getPhone());
			if (contacts != null){
				contacts.setContactsName(record.getContactsName());
				contacts.setContactsCode(DateUtil.dateToStringCode(new Date()));
				contacts.setFix(record.getFix());
				contacts.setEmail(record.getEmail());
				contacts.setQq(record.getQq());
				contacts.setWechat(record.getWechat());
				contacts.setWechatId(record.getWechatId());
				contacts.setCreator(environment.getUser().getUserId());
				contacts.setCreateDate(new Date());
				contacts.setCorpId(environment.getCorp().getCorpId());
				contactsmapper.updateByPrimaryKeySelective(contacts);
			}else{
				record.setCreator(environment.getUser().getUserId());
				record.setCreateDate(new Date());
				record.setCorpId(environment.getCorp().getCorpId());
				record.setContactsCode(DateUtil.dateToStringCode(new Date()));
				contactsmapper.insertSelective(record);
			}
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			} else {
				throw e;
			}
		}
	}

	/**
	 * 批量添加联系人
	 * @param records
	 */
	@Override
	@Transactional
	public void add(List<Contacts> records) {
		log.debug("addList: {}", records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;

		try {
			for(int i = 0; i < records.size(); ++i) {
				Contacts record = records.get(i);
				StringBuilder check = this.checkNewRecord(record);
				if (!StringUtils.isBlank(check)) {
					checkResult = false;
					s.append("第" + (i + 1) + "行，" + check + "/r/n");
				}
			}

			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			} else {
				for (Contacts contacts : records) {
					Environment environment=Environment.getEnv();
					contacts.setCreator(environment.getUser().getUserId());
					contacts.setCreateDate(new Date());
					contacts.setCorpId(environment.getCorp().getCorpId());
					contacts.setContactsCode(DateUtil.dateToStringCode(new Date()));
				}
				contactsmapper.insert(records);
			}
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			} else if(e instanceof BusinessException){
				throw e;
			} else{
				throw new BusinessException("操作失败:"+e.getMessage());
			}
		}
	}

	/**
	 * 修改联系人
	 * @param record
	 */
	@Override
	public void updateSelective(Contacts record) {
		log.debug("updateSelective: {}", record);

		try {
			Contacts contacts=contactsmapper.selectByPrimaryKey(record.getContactsId());
			if (contacts==null){
				throw new BusinessException("联系人不存在");
			}
		    record.setContactsCode(null);
			contactsmapper.updateByPrimaryKeySelective(record);
		} catch (Exception var3) {
			if (var3.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", var3);
			} else {
				throw var3;
			}
		}
	}

	/**
	 * 删除联系人
	 * @param id
	 */
	@Override
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		Contacts contacts=contactsmapper.selectByPrimaryKey(id);
		if (contacts==null){
			throw new BusinessException("联系人不存在");
		}
		this.getMapper().deleteByPrimaryKey(id);
	}

	/**
	 * 封存
	 * @param data
	 */
	@Override
	public void sequestration(Long data) {
		Contacts contacts=contactsmapper.selectByPrimaryKey(data);
		if (contacts==null){
			throw new BusinessException("联系人不存在");
		}
		contactsmapper.sequestration(data);
	}

	/**
	 * 取消封存
	 * @param data
	 */
	@Override
	public void unsequestration(Long data) {
		Contacts contacts=contactsmapper.selectByPrimaryKey(data);
		if (contacts==null){
			throw new BusinessException("联系人不存在");
		}
		contactsmapper.unsequestration(data);
	}

	/**
	 * 批量删除
	 * @param data
	 */
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		contactsmapper.deleteList(data);

	}


	/**
	 * 通过收发货单位查询
	 * @param unitId
	 * @return
	 */
	@Override
	@Transactional
	public List<Contacts> queryByUnitId(Long unitId) {
		try {
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
	public ContactsVO appQueryByUnitId(Long startUnitId, Long EndUnitId) {
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
			List<Contacts> contactss = contactsmapper.queryByUnitId(startUnitId);
			if( contactss.size() > 0 ) {
				contactsVO.setStartUnitPhone(contactss.get(0).getPhone());
				contactsVO.setStartUnitContactsName((contactss.get(0).getContactsName()));
			}
			contactss = contactsmapper.queryByUnitId(EndUnitId);
			if(contactsmapper.queryByUnitId(EndUnitId).size() > 0 ) {
				contactsVO.setEndUnitPhone(contactss.get(0).getPhone());
				contactsVO.setEndUnitContactsName((contactss.get(0).getContactsName()));
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

			List<Contacts> contacts = Optional.ofNullable(contactsmapper.queryByUnitId(startUnitId)).orElse(new ArrayList<Contacts>());
			contacts.forEach(contacts1 -> {
				ContactsVO cv = new ContactsVO();
				cv.setStartUnitPhone(contacts1.getPhone());
				cv.setStartUnitContactsName((contacts1.getContactsName()));
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

			List<Contacts> contacts = Optional.ofNullable(contactsmapper.queryByUnitId(endUnitId)).orElse(new ArrayList<Contacts>());
			contacts.forEach(contacts1 -> {
				ContactsVO cv = new ContactsVO();
				cv.setEndUnitPhone(contacts1.getPhone());
				cv.setEndUnitContactsName((contacts1.getContactsName()));
				ContactsVOs.add(cv);
			});
		} catch (Exception e) {
			throw e;
		}
		return ContactsVOs;
	}



	/**
	 * 通过收发货单位联系人名称查询
	 * @param contactsName
	 * @return
	 */
	@Override
	@Transactional
	public Contacts queryByName(String contactsName) {
		Contacts contacts = new Contacts();
		if (!StringUtil.isEmpty(contactsName)) {
			contacts = contactsmapper.queryByName(contactsName);
		}

		return contacts;
	}

	@Override
	public List<Contacts> queryByUnitIds(List<Long> unitIds) {
		try {
			List<Contacts> contactsList = new ArrayList<Contacts>();
			if (!CollectionUtils.isEmpty(unitIds)) {
				contactsList = contactsmapper.queryByUnitIds(unitIds);
			}
			return contactsList;
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 通过收发货单位和手机号码查询
	 * @param unitId
	 * @param phone
	 * @return
	 */
	@Override
	@Transactional
	public Contacts getByUnitPhone(Long unitId, String phone) {
		return contactsmapper.getByUnitPhone(unitId,phone);
	}

}

package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CommonDamageMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
* 通用质损管理主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CommonDamageServiceImpl extends BaseServiceImpl<CommonDamage> implements CommonDamageService {

	@Autowired
    private CommonDamageMapper damageMapper;

	@Autowired
	private CommonDamageDetailService damageDetailService;

	@Autowired
	private CommonDamagePhotoService damagePhotoService;

	@Autowired
	private TransportOrderService transportOrderService;

	@Autowired
	private TransportOrderDetailService transportOrderDetailService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private CorpService corpService;
	
	@Override
	protected BaseMapper<CommonDamage> getMapper() {
		return damageMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CommonDamage commondamage) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表ID", commondamage.getTransportOrderId(), 20));
        s.append(CheckUtils.checkString("YES", "damageCode", "质损号", commondamage.getDamageCode(), 50));
        s.append(CheckUtils.checkDate("YES", "damageDate", "质损日期", commondamage.getDamageDate()));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", commondamage.getCarVin(), 50));
        s.append(CheckUtils.checkString("YES", "voyageNumber", "航次", commondamage.getVoyageNumber(), 50));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", commondamage.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", commondamage.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CommonDamage commondamage) {
        StringBuilder s = checkNewRecord(commondamage);
        s.append(CheckUtils.checkPrimaryKey(commondamage.getDamageId()));
		return s;
	}

	@Override
	public CommonDamage queryById(Long id) {

        StringBuilder sb = new StringBuilder();
        try {

            sb.append(CheckUtils.checkPrimaryKey(id));
            if (!StringUtils.isBlank(sb)) {
                throw new RuntimeException("数据检查失败：" + sb.toString());
            }

            CommonDamage damage = damageMapper.selectByPrimaryKey(id);

            List<CommonDamageDetail> damageDetails = damageDetailService.selectDetailsByDamageId(id);
            List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDamageId(id);

            damageDetails.forEach(damageDetail -> {
                List<CommonDamagePhoto> damageDetailPhotos = damagePhotoService.selectPhotosByDetailId(damageDetail.getDamageDetailId());
                damageDetail.setDamagePhotos(damageDetailPhotos);
            });

            damage.setDamageDetails(damageDetails);
            damage.setDamagePhotos(damagePhotos);

            return damage;
        } catch (Exception e) {
            log.error("{}", e);
	        throw e;
        }
    }

	@Override
	public PageInfo<CommonDamage> queryList(Map<String, Object> map, int pageNum, int pageSize) {

		List<Long> lowerCorpIds = new ArrayList<>();
		ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();

		List<CommonDamage> damages = null;
		try {
			Environment environment = Environment.getEnv();
			Long corpId = environment.getUser().getCorpId();

			queue.add(corpId);
			while (!queue.isEmpty()) {
				Long lowerCorpId = queue.poll();
				lowerCorpIds.add(lowerCorpId);

				queue.addAll(corpService.queryHigherCorpByCorpId(lowerCorpId));
			}

			lowerCorpIds.sort(Comparator.comparing(Long::longValue));
			PageHelper.startPage(pageNum, pageSize);

//			if (map == null) {
//				map = new HashMap<>();
//			}
//			map.put("corpId", environment.getUser().getCorpId());
//			damages = damageMapper.selectList(map);

			if (CollectionUtils.isNotEmpty(lowerCorpIds)) {
				if (Objects.isNull(map)) {
					map = new HashMap<>();
				}
				map.put("lowerCorpIds", lowerCorpIds);
				damages = queryByIds(map);

//				damages.forEach(damage -> {
//					List<CommonDamageDetail> damageDetails = damageDetailService.selectDetailsByDamageId(damage.getDamageId());
//					List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDamageId(damage.getDamageId());
//
//					damageDetails.forEach(damageDetail -> {
//						List<CommonDamagePhoto> damageDetailPhotos = damagePhotoService.selectPhotosByDetailId(damageDetail.getDamageDetailId());
//						damageDetail.setDamagePhotos(damageDetailPhotos);
//					});
//
//					damage.setDamageDetails(damageDetails);
//					damage.setDamagePhotos(damagePhotos);
//				});
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		// PageHelper.startPage(pageNum, pageSize);
		// damages = damages.stream().filter(distinctByKey(damage -> damage.getCarVin())).collect(Collectors.toList());

		return new PageInfo(damages);
	}

	@Override
	public List<CommonDamage> queryList(Map<String, Object> map) {

		List<CommonDamage> damages;
		try {
			Environment environment = Environment.getEnv();
			damages = damageMapper.selectList(map);
			damages = damages.stream()
					.filter(damage -> damage.getCorpId().equals(environment.getUser().getCorpId()))
					.collect(Collectors.toList());

			damages.forEach(damage -> {
				List<CommonDamageDetail> damageDetails = damageDetailService.selectDetailsByDamageId(damage.getDamageId());
				List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDamageId(damage.getDamageId());

				damageDetails.forEach(damageDetail -> {
					List<CommonDamagePhoto> damageDetailPhotos = damagePhotoService.selectPhotosByDetailId(damageDetail.getDamageDetailId());
					damageDetail.setDamagePhotos(damageDetailPhotos);
				});

				damage.setDamageDetails(damageDetails);
				damage.setDamagePhotos(damagePhotos);
			});

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		damages = damages.stream().filter(distinctByKey(damage -> damage.getCarVin())).collect(Collectors.toList());
		return damages;
	}

	@Override
	public List<CommonDamage> appQueryList(Map<String, Object> map) {
		List<CommonDamage> damages;
		try {
			Environment environment = Environment.getEnv();
			damages = damageMapper.selectList(map);
			damages = damages.stream()
					.filter(damage -> damage.getCreator().equals(environment.getUser().getUserId()))
					.filter(damage -> transportOrderService.queryById(damage.getTransportOrderId()) != null)
					.filter(damage -> transportOrderService.queryById(damage.getTransportOrderId()).getBillStatus() == 2 ||
							          transportOrderService.queryById(damage.getTransportOrderId()).getBillStatus() == 3 ||
							          transportOrderService.queryById(damage.getTransportOrderId()).getBillStatus() == 4)
					.collect(Collectors.toList());

			damages.forEach(damage -> {
				List<CommonDamageDetail> damageDetails = damageDetailService.selectDetailsByDamageId(damage.getDamageId());
				List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDamageId(damage.getDamageId());

				damageDetails.forEach(damageDetail -> {
					List<CommonDamagePhoto> damageDetailPhotos = damagePhotoService.selectPhotosByDetailId(damageDetail.getDamageDetailId());
					damageDetail.setDamagePhotos(damageDetailPhotos);
				});

				damage.setDamageDetails(damageDetails);
				damage.setDamagePhotos(damagePhotos);
			});

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		damages = damages.stream().filter(distinctByKey(damage -> damage.getCarVin())).collect(Collectors.toList());
		return damages;
	}

	@Override
	public List<CommonDamage> queryByIds(Map<String, Object> map) {
			if (Objects.nonNull(map)) {
				Object dateObj = map.get("endTime");
				if (Objects.nonNull(dateObj)) {
					String dateStr = dateObj.toString();
					if (StringUtils.isNotBlank(dateStr)) {
						String endTime = String.format("%s 23:59:59", dateStr);
						map.replace("endTime", endTime);
					}
				}
			}
			List<CommonDamage> damages = damageMapper.queryByIds(map);
			return damages;
	}

	@Override
	public void appAdd(CommonDamage commonDamage) {
		StringBuilder sb;
		try {
			Environment environment = Environment.getEnv();

			List<TransportOrderDetail> transportOrderDetails = transportOrderDetailService.selectByCarVin(commonDamage.getCarVin());
			if (CollectionUtils.isEmpty(transportOrderDetails)) {
				throw new RuntimeException(String.format("运单中未查询到该车架号: %s", commonDamage.getCarVin()));
			}

			TransportOrderDetail transportOrderDetail = transportOrderDetails.stream().findFirst().get();

			if (transportOrderDetail == null) {
				throw new BusinessException("9100", commonDamage.getCarVin());
			}
			if (damageMapper.getByCarVin(commonDamage.getCarVin()) != null) {
				throw new BusinessException("9102", commonDamage.getCarVin());
			}

			TransportOrder transportOrder = transportOrderService.queryById(transportOrderDetail.getTransportOrderId());
			if (!transportOrder.getDriverId().equals(environment.getUser().getUserId())) {
				throw new RuntimeException(String.format("车架号：%s对应的运单记录（运单号：%s）非当前司机运输，无法添加质损记录。", commonDamage.getCarVin(), transportOrder.getTransportOrderCode()));
			}

			Long carBrandId = null;
			List<OrderDetail> orderDetails = orderDetailService.queryByCarVin(commonDamage.getCarVin());
			if (CollectionUtils.isNotEmpty(orderDetails)) {
				OrderDetail orderDetail = orderDetails.stream().findFirst().get();
				try {
					carBrandId = orderDetail.getBrandId();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			commonDamage.setCarBrandId(carBrandId);
			commonDamage.setTransportOrderId(transportOrderDetail.getTransportOrderId());
			commonDamage.setDamageCode(String.format("SQ%s", DateUtil.dateToStringCode(new Date())));
			commonDamage.setCreator(environment.getUser().getUserId());
			commonDamage.setCreateDate(new Date());
			commonDamage.setCorpId(environment.getUser().getCorpId());
			commonDamage.setTransportOrderId(transportOrderDetail.getTransportOrderId());

			sb = checkNewRecord(commonDamage);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}

			damageMapper.insertSelective(commonDamage);

			if (commonDamage.getDamageDetails() != null && commonDamage.getDamageDetails().size() > 0) {
				commonDamage.getDamageDetails().forEach(damageDetail -> {
					damageDetail.setDamageId(commonDamage.getDamageId());
					damageDetail.setCorpId(commonDamage.getCorpId());
				});
				damageDetailService.add(commonDamage.getDamageDetails());
			}

			if (commonDamage.getDamagePhotos() != null && commonDamage.getDamagePhotos().size() > 0) {
				commonDamage.getDamagePhotos().forEach(damagePhoto -> {
					damagePhoto.setDamageId(commonDamage.getDamageId());
					damagePhoto.setCorpId(commonDamage.getCorpId());
				});
				damagePhotoService.add(commonDamage.getDamagePhotos());
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void add(CommonDamage commonDamage) {

		StringBuilder sb;
		try {
			Environment environment = Environment.getEnv();

			List<TransportOrderDetail> transportOrderDetails = transportOrderDetailService.selectByCarVin(commonDamage.getCarVin());
			if (CollectionUtils.isEmpty(transportOrderDetails)) {
				throw new RuntimeException(String.format("运单中未查询到该车架号: %s", commonDamage.getCarVin()));
			}

			TransportOrderDetail transportOrderDetail = transportOrderDetails.stream().findFirst().get();

			if (transportOrderDetail == null) {
				throw new BusinessException("9100", commonDamage.getCarVin());
			}
			if (damageMapper.getByCarVin(commonDamage.getCarVin()) != null) {
				throw new BusinessException("9102", commonDamage.getCarVin());
			}

			TransportOrder transportOrder = transportOrderService.queryById(transportOrderDetail.getTransportOrderId());
			if (!transportOrder.getCorpId().equals(environment.getUser().getCorpId())) {
				throw new RuntimeException(String.format("车架号：%s对应的运单记录（运单号：%s）非当前公司运输，无法添加质损记录。", commonDamage.getCarVin(), transportOrder.getTransportOrderCode()));
			}

			Long carBrandId = null;
			List<OrderDetail> orderDetails = orderDetailService.queryByCarVin(commonDamage.getCarVin());
			if (CollectionUtils.isNotEmpty(orderDetails)) {
				OrderDetail orderDetail = orderDetails.stream().findFirst().get();
				carBrandId = orderDetail.getBrandId();
			}

			commonDamage.setCarBrandId(carBrandId);
			commonDamage.setTransportOrderId(transportOrderDetail.getTransportOrderId());
			commonDamage.setDamageCode(String.format("SQ%s", DateUtil.dateToStringCode(new Date())));
			commonDamage.setCreator(environment.getUser().getUserId());
			commonDamage.setCreateDate(new Date());
			commonDamage.setCorpId(environment.getUser().getCorpId());
			commonDamage.setTransportOrderId(transportOrderDetail.getTransportOrderId());

			sb = checkNewRecord(commonDamage);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}

			damageMapper.insertSelective(commonDamage);

			if (commonDamage.getDamageDetails() != null && commonDamage.getDamageDetails().size() > 0) {
				commonDamage.getDamageDetails().forEach(damageDetail -> {
					damageDetail.setDamageId(commonDamage.getDamageId());
					damageDetail.setCorpId(commonDamage.getCorpId());
				});
				damageDetailService.add(commonDamage.getDamageDetails());
			}

			if (commonDamage.getDamagePhotos() != null && commonDamage.getDamagePhotos().size() > 0) {
				commonDamage.getDamagePhotos().forEach(damagePhoto -> {
					damagePhoto.setDamageId(commonDamage.getDamageId());
					damagePhoto.setCorpId(commonDamage.getCorpId());
				});
				damagePhotoService.add(commonDamage.getDamagePhotos());
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void add(List<CommonDamage> records) {
		super.add(records);
	}

	@Transactional
	@Override
	public void updateSelective(CommonDamage commonDamage) {

        StringBuilder sb = new StringBuilder();
        try {
            sb.append(CheckUtils.checkLong("NO", "damageId", "质损ID", commonDamage.getDamageId(), 20));
            if (!StringUtils.isBlank(sb)) {
                throw new RuntimeException("数据检查失败：" + sb.toString());
            }
            damageMapper.updateByPrimaryKeySelective(commonDamage);
        } catch (Exception e) {
            log.error("{}", e);
	        throw e;
        }
	}

	@Transactional
	@Override
	public void update(CommonDamage record) {
		super.update(record);
	}

	@Transactional
	@Override
	public void update(List<CommonDamage> records) {
		super.update(records);
	}

	@Transactional
	@Override
	public void deleteById(Long id) {

		damageMapper.deleteByPrimaryKey(id);

		List<Long> damageDetailIds = damageDetailService.selectDamageDetailIdByDamageId(id);
		List<Long> damagePhotoIds = damagePhotoService.selectPhotoIdByDamageId(id);

		try {
			damageDetailIds.forEach(damageDetailId -> damageDetailService.deleteById(damageDetailId));
			damagePhotoIds.forEach(damagePhotoId -> damagePhotoService.deleteById(damagePhotoId));
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public CommonDamage getByCarVin(String carVin) {

		Environment environment = Environment.getEnv();
		Map<String, Object> params = new HashMap(){{
			put("driverId", environment.getUser().getUserId());
		}};
		List<TransportOrder> orders = transportOrderService.queryList(params);
		if (CollectionUtils.isEmpty(orders)) {
			throw new RuntimeException("当前司机没有运单记录");
		}

		boolean isCarVinExist = false;
		TransportOrderDetail transportOrderDetail = null;
		for (TransportOrder transportOrder : orders) {
			params = new HashMap(){{
				put("transportOrderId", transportOrder.getTransportOrderId());
			}};
			List<TransportOrderDetail> orderDetails = transportOrderDetailService.queryList(params);
			if (CollectionUtils.isNotEmpty(orderDetails)) {
				Optional<TransportOrderDetail> optionalTransportOrderDetail = orderDetails.stream()
						.filter(orderDetail -> (orderDetail.getCarVin().equals(carVin))).findFirst();
				if (optionalTransportOrderDetail.isPresent()) {
					if (transportOrder.getBillStatus() != 2 && transportOrder.getBillStatus() != 3 && transportOrder.getBillStatus() != 4) {
						throw new RuntimeException("运单单据未处于已发送，已接单或在途状态，请检查后重试。");
					}
					transportOrderDetail = optionalTransportOrderDetail.get();
					if (transportOrderDetail.getCarStatus() != 6 &&
						transportOrderDetail.getCarStatus() != 7 &&
						transportOrderDetail.getCarStatus() != 8 &&
						transportOrderDetail.getCarStatus() != 9 &&
						transportOrderDetail.getCarStatus() != 10) {
						throw new RuntimeException("车辆状态未处于司机接单，已预约，已装车，在途或到店状态，请检查后重试。");
					}
					isCarVinExist = true;
					break;
				} else {
					// throw new RuntimeException(String.format("当前未完成运单中未查询到对应车架号：%s", carVin));
				}
			} else {
				// throw new RuntimeException("当前运单下无运单明细");
			}
		}

		if (isCarVinExist == false) {
			throw new RuntimeException(String.format("当前未完成运单中未查询到对应车架号：%s", carVin));
		}

		CommonDamage damage = damageMapper.getByCarVin(carVin);
		if (damage != null) {
			List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDamageId(damage.getDamageId());
			damage.setDamagePhotos(damagePhotos);
			damage.setDamageDetails(damageDetailService.selectDetailsByDamageId(damage.getDamageId()));
		} else {
			try {
				damage = new CommonDamage();
				damage.setDamageId(null);
				damage.setCarVin(carVin);
				damage.setTransportOrderId(transportOrderDetail.getTransportOrderId());
				damage.setRefTransOrderCode(transportOrderDetail.getTransportOrderCode());
			} catch (Exception e) {
				log.error("{}", e);
			}
		}

		return damage;
	}

	@Transactional
	@Override
	public Long appAddDamageWithPhotos(CommonDamage damage, MultipartFile[] files, Integer[] photoType, String[] photoDesc) throws Exception {

		Environment environment = Environment.getEnv();

		if (damage == null || files.length < 1) {
			log.error("damage:{} files:{}", damage, files.length);
			throw new BusinessException("9005", "damage or files");
		}

		List<TransportOrderDetail> transportOrderDetails = transportOrderDetailService.selectByCarVin(damage.getCarVin());
		if (CollectionUtils.isEmpty(transportOrderDetails)) {
			throw new RuntimeException(String.format("运单中未查询到该车架号: %s", damage.getCarVin()));
		}

		TransportOrderDetail transportOrderDetail = transportOrderDetails.stream().findFirst().get();

		if (transportOrderDetail == null) {
			throw new BusinessException("9100", damage.getCarVin());
		}
		if (damageMapper.getByCarVin(damage.getCarVin()) != null) {
			throw new BusinessException("9102", damage.getCarVin());
		}

		TransportOrder transportOrder = transportOrderService.queryById(transportOrderDetail.getTransportOrderId());
		if (!transportOrder.getDriverId().equals(environment.getUser().getUserId())) {
			throw new RuntimeException(String.format("车架号：%s对应的运单记录（运单号：%s）非当前司机运输，无法添加质损记录。", damage.getCarVin(), transportOrder.getTransportOrderCode()));
		}

		try {
			Environment env = Environment.getEnv();

			damage.setTransportOrderId(transportOrderDetail.getTransportOrderId());
			damage.setDamageCode(String.format("SQ%s", DateUtil.dateToStringCode(new Date())));
			damage.setCreator(env.getUser().getUserId());
			damage.setCreateDate(new Date());
			damage.setCorpId(env.getUser().getCorpId());

			List<CommonDamagePhoto> damagePhotos = new ArrayList<>();
			for (int i = 0; i < files.length; i++) {
				CommonDamagePhoto damagePhoto = new CommonDamagePhoto();
				if (files[i].getSize() > 0) {
					damagePhoto.setFlag(0);
					damagePhoto.setPhotoPath(FileUtil.uploadFile(files[i], damage.getCarVin()));
					if (photoType != null && photoType[i] != null) damagePhoto.setPhotoType(photoType[i]);
					if (photoDesc != null && photoDesc[i] != null) damagePhoto.setPhotoDesc(photoDesc[i]);
					damagePhotos.add(damagePhoto);
				}
			}

			damage.setDamagePhotos(damagePhotos);
			appAdd(damage);

			return damage.getDamageId();
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void addDamageWithPhotos(CommonDamage damage,
	                                MultipartFile[] files,
	                                Integer[] photoType,
	                                String[] photoDesc) throws Exception {

		if (damage == null || files.length < 1) {
			log.error("damage:{} files:{}", damage, files.length);
			throw new BusinessException("9005", "damage or files");
		}

        try {
            Environment env = Environment.getEnv();
            damage.setCreator(env.getUser().getUserId());
            damage.setCreateDate(new Date());
            damage.setCorpId(env.getUser().getCorpId());

            List<CommonDamagePhoto> damagePhotos = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                CommonDamagePhoto damagePhoto = new CommonDamagePhoto();
                if (files[i].getSize() > 0) {
                    damagePhoto.setFlag(0);
                    damagePhoto.setPhotoPath(FileUtil.uploadFile(files[i], damage.getCarVin()));
	                if (photoType != null && photoType[i] != null) damagePhoto.setPhotoType(photoType[i]);
	                if (photoDesc != null && photoDesc[i] != null) damagePhoto.setPhotoDesc(photoDesc[i]);
                    damagePhotos.add(damagePhoto);
                }
            }

            damage.setDamagePhotos(damagePhotos);
            add(damage);
        } catch (Exception e) {
            log.error("{}", e);
	        throw e;
        }
    }

	@Transactional
	@Override
	public void uploadDamagePhotos(Long damageId, MultipartFile[] files, Integer[] photoType, String[] photoDesc) {

		try {
            Environment environment = Environment.getEnv();
			CommonDamage damage = damageMapper.selectByPrimaryKey(damageId);

            List<CommonDamagePhoto> damagePhotos = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                CommonDamagePhoto damagePhoto = new CommonDamagePhoto();
                try {
                    if (files[i].getSize() > 0) {
                        damagePhoto.setDamageId(damageId);
                        damagePhoto.setCorpId(environment.getUser().getCorpId());
                        damagePhoto.setFlag(0);
                        damagePhoto.setPhotoPath(FileUtil.uploadFile(files[i], damage.getCarVin()));
                        if (photoType != null && photoType[i] != null) damagePhoto.setPhotoType(photoType[i]);
                        if (photoDesc != null && photoDesc[i] != null) damagePhoto.setPhotoDesc(photoDesc[i]);
                    }
                } catch (Exception e) {
                    log.error("{}", e);
                }
                damagePhotos.add(damagePhoto);
            }
            damagePhotoService.add(damagePhotos);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
	{
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}

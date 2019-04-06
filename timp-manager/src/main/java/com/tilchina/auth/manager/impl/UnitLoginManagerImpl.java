package com.tilchina.auth.manager.impl;

import com.tilchina.auth.manager.UnitLoginManager;
import com.tilchina.auth.vo.UserInfoVO;
import com.tilchina.catalyst.utils.BCryptUtil;
import com.tilchina.catalyst.utils.IpUtils;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.Block;
import com.tilchina.timp.enums.ErrorTimes;
import com.tilchina.timp.expection.LoginErrorException;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.IntegerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @version 1.0.0 2018/7/12
 * @author Xiahong
 */
@Service
public class UnitLoginManagerImpl implements UnitLoginManager {


    @Autowired
    private UnitLoginService unitLoginService;

    @Autowired
    private UnitService unitService;


    @Autowired
    private UnitRelationService unitRelationService;

    @Override
    public UserInfoVO login(HttpServletRequest request, HttpServletResponse response, Map<String,Object> map, String password) {
        UnitLogin unitLogin = unitLoginService.queryByUnitCodeOrDealerCode(map);
        if(unitLogin == null){
            throw new LoginErrorException("901");
        }
        if(unitLogin.getBlock().intValue() == Block.BLOCK.getIndex()){
            throw new LoginErrorException("905");
        }
        UserInfoVO info = new UserInfoVO();
        if (BCryptUtil.check(password, unitLogin.getPassword())){
            Unit unit = unitService.queryById(unitLogin.getUnitId());
            List<UnitRelation> unitRelations = unitRelationService.queryByUnitId(unit.getUnitId());

            Environment env = new Environment();
            env.setUser(new User());
            env.setCorp(new Corp());
            env.setUnit(unit);
            env.setUnitRelations(unitRelations);
           /* env.setAdmin(IntegerUtil.getBoolean(env.getUser().getAdmin()));
            env.setSuperManager(IntegerUtil.getBoolean(env.getUser().getSuperManager()));*/
            Environment.setEnv(env);

            String ip = IpUtils.getIp(request);
            Date loginTime = new Date();
            String userAgent = request.getHeader("User-Agent");

            unitLoginService.login(ip,userAgent,loginTime);

            info.setUnit(unit);
            info.setUnitRelations(unitRelations);
            HttpSession session =request.getSession();
            session.setAttribute("env",env);

        }else{
            if(unitLogin.getErrorTimes().intValue() < ErrorTimes.UNLOCKED.getIndex()){
                unitLogin.setErrorTimes(unitLogin.getErrorTimes()+1);
                unitLoginService.updateSelective(unitLogin);
            }else {
                unitLogin.setErrorTimes(ErrorTimes.BLOCK.getIndex());
                unitLogin.setBlock(Block.BLOCK.getIndex());
                unitLoginService.updateSelective(unitLogin);
            }

            throw new LoginErrorException("902");
        }

        return info;
    }

}

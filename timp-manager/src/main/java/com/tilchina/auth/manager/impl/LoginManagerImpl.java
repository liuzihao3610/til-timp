package com.tilchina.auth.manager.impl;

import com.tilchina.auth.manager.LoginManager;
import com.tilchina.auth.vo.UserInfoVO;
import com.tilchina.catalyst.utils.BCryptUtil;
import com.tilchina.catalyst.utils.IpUtils;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.Admin;
import com.tilchina.timp.enums.Block;
import com.tilchina.timp.enums.ErrorTimes;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.expection.LoginErrorException;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.service.impl.RegistServiceImpl;
import com.tilchina.timp.util.IntegerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 *
 *
 * @version 1.0.0 2018/3/25
 * @author WangShengguang
 */
@Service
public class LoginManagerImpl implements LoginManager {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private CorpService corpService;

    @Autowired
    private RegistService registService;

    @Autowired
    private CorpManagementService corpManagementService;

    @Override
    public UserInfoVO login(HttpServletRequest request, HttpServletResponse response, String userCode, String password) {
        Login login = loginService.queryByUserCode(userCode,0);
        if(login == null){
            throw new LoginErrorException("901");
        }
        if(login.getBlock().intValue() == Block.BLOCK.getIndex()){
            throw new LoginErrorException("905");
        }
        UserInfoVO info = new UserInfoVO();
        if (!BCryptUtil.check(password, login.getPassword())){
            User user = userService.queryById(login.getUserId());
            Corp corp = corpService.queryById(user.getCorpId());
            List<Map<String, Object>> corpList = corpManagementService.getCorpListByUserId(user.getUserId());
           /* List<Regist> regists = registService.queryTreeByUserId(user.getUserId());*/
            List<Regist> regists = registService.queryByUserId(user.getUserId());
            // 如果是系统管理员则拥有所有权限
            if (user.getAdmin().intValue() != Admin.ADMIN.getIndex()){
                regists = registService.queryList(new HashMap<>());
            }
            Environment env = new Environment();
            env.setUser(user);
            env.setCorp(corp);
            env.setAdmin(IntegerUtil.getBoolean(env.getUser().getAdmin()));
            env.setSuperManager(IntegerUtil.getBoolean(env.getUser().getSuperManager()));
            env.setRegists(regists);
            Environment.setEnv(env);

            String ip = IpUtils.getIp(request);
            Date loginTime = new Date();
            String userAgent = request.getHeader("User-Agent");

            loginService.login(ip,userAgent,loginTime);
            info.setUser(user);
            info.setCorp(corp);

            // 仅当用户为超级用户的时候返回可切换公司列表
            if (IntegerUtil.getBoolean(user.getSuperManager())) {
                info.setCorpList(corpList);
            }

            info.setRegists(regists);
            info.setCorpList(corpList);
            HttpSession session = request.getSession();
            session.setAttribute("env",env);
//            CookieUtils.addCookie(response,LoginConstant.PRARM_LOGIN_TIME,String.valueOf(loginTime.getTime()),LoginConstant.PARAM_DOMAIN,-1);
        }else{
            if(login.getErrorTimes().intValue() < ErrorTimes.UNLOCKED.getIndex()){
                login.setErrorTimes(login.getErrorTimes()+1);
                loginService.updateSelective(login);
            }else {
                login.setErrorTimes(ErrorTimes.BLOCK.getIndex());
                login.setBlock(Block.BLOCK.getIndex());
                loginService.updateSelective(login);
            }
            throw new LoginErrorException("902");
        }

        return info;
    }

    @Transactional
    @Override
    public UserInfoVO cutCorp(HttpServletRequest request, HttpServletResponse response,Long corpId) {
        try {
            Environment environment = Optional.ofNullable( Environment.getEnv())
                                                .orElseThrow(() ->  new  BusinessException("没有获取到有效的Environment信息。"));
            if (environment.getUser() == null || environment.getCorp() == null){
                throw new BusinessException("没有获取到用户档案信息或公司档案信息。/r/n目前用户档案信息："+environment.getUser()+",公司档案信息："+environment.getCorp());
            }
            List<CorpManagement> corpManagements = corpManagementService.queryByUserId(environment.getUser().getUserId());
            Map<Long,CorpManagement> corpManagementMap = new HashMap<Long,CorpManagement>();
            corpManagements.forEach(corpManagement -> corpManagementMap.put(corpManagement.getManagementCorpId(),corpManagement));
            if (corpManagementMap.get(corpId) == null){
                throw new BusinessException("公司管理档案中用户："+environment.getUser().getUserName()+"可管理公司中没有"+environment.getCorp().getCorpName()+"公司。");
            }
            Environment env = new Environment();
            Corp corp = corpService.queryById(corpId);
            User user = userService.queryById(environment.getUser().getUserId());
            List<Map<String, Object>> corpList = corpManagementService.getCorpListByUserId(user.getUserId());
            env.setCorp(corp);
            env.setUser(user);
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setUser(user);
            userInfoVO.setCorp(corp);

            // 仅当用户为超级用户的时候返回可切换公司列表
            if (IntegerUtil.getBoolean(user.getSuperManager())) {
                userInfoVO.setCorpList(corpList);
            }

            Environment.setEnv(env);
            HttpSession session = request.getSession();
            session.setAttribute("env",env);
            return userInfoVO;
        } catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }
    }

}

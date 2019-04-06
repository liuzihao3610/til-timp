package com.tilchina.timp.common;

import com.tilchina.timp.model.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @version 1.0.0 2018/3/25
 * @author WangShengguang
 */
@Data
public class Environment implements Serializable{

    private static ThreadLocal<Environment> threadLocal = new ThreadLocal<>();

    private User user;

    private Unit unit;

    private Corp corp;

    private List<UnitRelation> unitRelations;

    private UnitRelation unitRelation;

    private Transporter transporter;

    private boolean superManager;

    private boolean admin;
    // 权限列表
    private List<Regist> regists;

    public static Environment getInstance(){
        if(threadLocal.get() == null){
            threadLocal.set(new Environment());
        }
        return threadLocal.get();
    }

    public static void setEnv(Environment env){
        threadLocal.set(env);
    }

    public static Environment getEnv(){
        return getInstance();
    }

    }

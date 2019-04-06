package com.tilchina.timp.support;

import com.tilchina.timp.vo.CityAreaGroupVO;
import com.tilchina.timp.vo.TransporterCountVO;

import java.util.List;

/**
 * 
 * 
 * @version 1.0.0 2018/5/18
 * @author WangShengguang   
 */ 
public class AssemblyCitySupportTest {

    public void run(CityAreaGroupVO group, List<TransporterCountVO> counts){

    }

    //M选N，剩下的继续选，直到选不出来

//    private boolean check(PendingOrderVO order, AssemblyResultVO temp, int cargoIndex){
//        Cargo cargo = temp.getCargos().get(cargoIndex);
//
//        Integer useLong = cargo.getCargoLong();
//        if(cargoIndex != 0 && temp.getLastLong() > 0){
//            useLong = useLong + temp.getLastLong();
//        }
//        Car car = order.getCar();
//        if(useLong >= car.getCarLong() && cargo.getCargoWidth() >= car.getCarWidth() && cargo.getCargoHigh() >= car.getCarHigh()){
//            if(1 == cargo.getCarryOver().intValue()){
//                temp.setLastLong(useLong - car.getCarLong());
//            }
//            return true;
//        }
//        return false;
//    }
}

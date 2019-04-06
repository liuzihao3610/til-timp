package com.tilchina.test;

import com.tilchina.catalyst.utils.PBKDF2;


/**
 * Created by demon on 2018/5/3.
 */
public class LoginTest {

    public static void generate(String token){
        try {
            String result = PBKDF2.generate(token, 2);
            String[] rs = result.split(":");
            String salt = rs[1];
            String hashed = rs[2];
            String ide = "";//PBKDF2.generate(i,salt,2);
            String[] is = ide.split(":");
            String ihashed = is[2];
            System.out.println(ide);
            System.out.println(hashed);
            System.out.println(ihashed);
            System.out.println(salt);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void generate(String token,String salt){
        try {
            String result = null;//PBKDF2.generate(token, salt, 2);
            System.out.println(result);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void validate(){
        try {
            boolean result = PBKDF2.validatePassword("awifie0:a3:ac:32:1f:09",
                    "db3cb63f97959c38106d7b604b640cfa293b224559b09c1c240f3fcd6ef008b9", "5b42403763643834353836", 2);
            System.out.println(result);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginTest.generate("d30440ed4e664f999e17638a9401ebe5","75d29dd05099dd194156278a38a90249");
        LoginTest.generate("aimei868374039482831","75d29dd05099dd194156278a38a90249");
//        LoginTest.generate("392d7b6683a74f9ea0950feb124735e8","awifie0:a3:ac:32:1f:09");
//        LoginTest.validate();
    }
}

package io.nutz.demo.simple;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.nutz.demo.simple.bean.User;

@IocBean(create="init")
public class MainLauncher {

    @Inject
    protected Dao dao;

    @Ok("raw")
    @At("/time/now")
    public long now() {
        return System.currentTimeMillis();
    }

    @Ok("raw")
    @At("/shiro/test")
    public boolean isAuthenticated(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        return subject.isAuthenticated();
    }

    public void init() {
        Daos.createTablesInPackage(dao, User.class, false);
        dao.insert(newUser("admin", "123456"));
        dao.insert(newUser("wendal", "123123"));
    }

    protected static User newUser(String name, String password) {
        User user = new User();
        user.setName(name);
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        String salt = rng.nextBytes().toBase64();
        user.setSalt(salt);
        String hashedPasswordBase64 = new Sha256Hash(password, salt, 1024).toBase64();
        user.setPassword(hashedPasswordBase64);
        user.setCreateTime(new Date());
        return user;
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}

package me.j360.disboot.miniprogram.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.Maps;
import me.j360.framework.boot.shiro.AbstractTokenShiroConfiguration;
import me.j360.framework.boot.shiro.JwtSignature;
import me.j360.framework.boot.shiro.dao.RedissonSessionStorageDAO;
import me.j360.framework.boot.shiro.dao.SessionStorageDAO;
import me.j360.framework.boot.shiro.filter.TokenAuthcFilter;
import me.j360.framework.boot.shiro.filter.TokenContextFilter;
import me.j360.framework.boot.shiro.realm.TokenRealm;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: min_xu
 * @date: 2019/1/11 11:26 AM
 * 说明：
 */

@Configuration
public class ShiroConfig extends AbstractTokenShiroConfiguration {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    public SessionStorageDAO sessionStorageDAO;


    @Bean
    public SessionStorageDAO sessionStorageDAO() {
        RedissonSessionStorageDAO sessionStorageDAO = new RedissonSessionStorageDAO();
        sessionStorageDAO.setRedissonClient(redissonClient);
        return sessionStorageDAO;
    }

    @Bean
    public Realm realm() {
        TokenRealm realm = new TokenRealm(sessionStorageDAO);
        realm.setCredentialsMatcher(new SimpleCredentialsMatcher());
        return realm;
    }

    @Override
    public Map<String, String> getFilterPathFilterMap() {
        Map<String, String> filters = Maps.newLinkedHashMap();
        filters.put("/api/auth/guest", "anon");
        filters.put("/api/auth/login", "context, tokenAuthc[guest]");
        filters.put("/**", "context, tokenAuthc[user]");
        return filters;
    }

    @Override
    public Map<String, Filter> getCustomFilters() {
        HashMap<String, Filter> filters = Maps.newLinkedHashMap();
        filters.put("context", tokenContextFilter());
        filters.put("tokenAuthc", tokenAuthcFilter());
        return filters;
    }




}

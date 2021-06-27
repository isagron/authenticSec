package com.isagron.security.services.features;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.isagron.security.configuration.properties.LoginAttemptProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final static int ATTEMPT_INCREMENT = 1;

    @Autowired
    private LoginAttemptProperties loginAttemptProperties;

    private LoadingCache<String, Integer> loginAttemptCache;

    /**
     * Set the size of the cache according to property configuration
     */
    @PostConstruct
    private void init(){
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(loginAttemptProperties.getCacheExpireTimeInMin(), TimeUnit.MINUTES)
                .maximumSize(loginAttemptProperties.getSizeOfCache()).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    /**
     * remove user from cache
     * @param userName - the user name
     */
    public void removeUser(String userName) {
        //remove the entry from the cache
        loginAttemptCache.invalidate(userName);
    }

    /**
     * Add user to cache
     * @param userName - the user name
     */
    public void addUser(String userName){
        try{
            int attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(userName);
            loginAttemptCache.put(userName, attempts);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean isExceed(String userName) {
        try{
            return loginAttemptCache.get(userName) >= loginAttemptProperties.getMaximumNumber();
        } catch (ExecutionException e) {
            return false;
        }
    }
}

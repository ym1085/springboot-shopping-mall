package com.shoppingmall.utils;

import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisJwtUtils {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;

    /**
     * Redis에 key(user=account | token=access key), value(refresh token | 'logout' keyword) 기반 값 저장
     */
    public void setValues(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, duration, TimeUnit.SECONDS);
    }

    /**
     * username(account) 기반 토큰 refresh token 정보 반환
     */
    public String getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * username(account) 기반 value 삭제
     */
    public boolean deleteValues(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * username(account) 기반 KEY 존재 유무 확인
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setBlackList(String key, String data, Integer duration, TimeUnit timeUnit) {
        redisBlackListTemplate.opsForValue().set(key, data, duration, timeUnit);
    }

    public String getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(key));
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }

    /**
     * reissue api 호출 시 요청된 refresh token 과 redis에 저장된 refresh token을 비교
     * 동일하지 않으면 refresh token이 탈취된 것으로 판단하여 예외 발생
     */
    public void checkRefreshToken(String userName, String refreshToken) {
        String redisRefreshToken = this.getValues(userName);
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new TokenExpiredException("redis token is invalid!");
        }
    }
}

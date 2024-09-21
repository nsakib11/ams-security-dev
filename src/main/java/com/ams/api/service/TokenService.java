package com.ams.api.service;

import com.ams.api.config.AmsAuthJwtConfig;
import com.ams.api.entity.Token;
import com.ams.api.entity.User;
import com.ams.api.exception.NotFoundException;
import com.ams.api.exception.TokenInvalidException;
import com.ams.api.exception.TokenRefreshException;
import com.ams.api.jwt.JwtUtils;
import com.ams.api.repository.TokenRepository;
import com.ams.api.repository.UserRepository;
import com.ams.api.service.impl.CustomerUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private AmsAuthJwtConfig amsAuthJwtConfig;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<Token> findByRefreshToken(String token) {
        return tokenRepository.findByRefreshToken(token);
    }

    @Cacheable(cacheNames = "authCache", key = "#userDetails.getId()")
    public Token saveToken(CustomerUserDetails userDetails) {
        Token token = new Token();
        String jwt = jwtUtils.generateJwtToken(userDetails);
        token.setUser(userRepository.findById(userDetails.getId()).get());
        token.setExpiryDate(Instant.now().plusMillis(amsAuthJwtConfig.getJwtConfig().getRefreshExpirationMs()));
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setToken(jwt);
        token = tokenRepository.save(token);
        return token;
    }

    public Token verifyExpiration(Token token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            tokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    @CacheEvict(cacheNames = "authCache", key = "#userId")
    public int deleteByUserId(Long userId) {
        return tokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Transactional
    @CachePut(cacheNames = "authCache", key = "#userId")
    public Token updateTokenByUserId(String requestRefreshToken, Long userId, String jwtToken) throws TokenInvalidException, NotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Token token = tokenRepository.findByRefreshToken(requestRefreshToken).orElseThrow(() -> new TokenInvalidException("Invalid refresh token"));
        token.setToken(jwtToken);
        return tokenRepository.save(token);
    }

    @Cacheable(cacheNames = "authCache", key = "#userId")
    public Token getTokenByJwtToken(Long userId, String token) throws NotFoundException {
        logger.info("Get token from db");
        return tokenRepository.findByToken(token).orElseThrow(() -> new NotFoundException("Token not found"));
    }

    @Transactional
    @CachePut(cacheNames = "authCache", key = "#userId")
    public Token findByUser(Long userId) throws TokenInvalidException, NotFoundException {
        User user =userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Token token = tokenRepository.findByUser(user).orElse(null);
       return token;
    }


}

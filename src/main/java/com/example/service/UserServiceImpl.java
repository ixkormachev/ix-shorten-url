package com.example.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.AddUrlRequest;
import com.example.dto.RegisterResponseHolder;
import com.example.dto.StatisticResponseHolder;
import com.example.model.Redirection;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RedirectionRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.apache.commons.lang.RandomStringUtils;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private static final String BASE_URL;
    private static final String HELP_URL;

    static {
        String canonicalHostName = "127.0.0.1";
        InetAddress iAddress;
        try {
            iAddress = InetAddress.getLocalHost();
            canonicalHostName = iAddress.getCanonicalHostName();
        } catch (final UnknownHostException e) {
            e.printStackTrace();
        }
        BASE_URL = "http://" + canonicalHostName + ":8080/";
        HELP_URL = "http://" + canonicalHostName + ":8080/help/";
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RedirectionRepository redirectionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User
            findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void
            saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    @Override
    public StatisticResponseHolder
            createStatisticForUserEmail(String email) {
        final List<Redirection> redirections = redirectionRepository.findByUserEmail(email);
        return redirections.isEmpty() ? new StatisticResponseHolder() : createStatisticResponseHolder(redirections);
    }

    private StatisticResponseHolder
            createStatisticResponseHolder(List<Redirection> redirections) {
        StatisticResponseHolder statisticResponseHolder = new StatisticResponseHolder();
        final Map<String, Long> response = statisticResponseHolder.getStatisticResponse().getRedirectStatistics();
        for (final Redirection r : redirections) {
            response.put(r.getUrl(), r.getRedirectCount());
        }
        return statisticResponseHolder;
    }

    @Override
    public Redirection
            getRedirectionForShortUrl(String shortUrl) {
        Redirection result;
        try {
            result = redirectionRepository.findByShortUrl(shortUrl);
        } catch (final NoResultException ex) {
            return createNewRedirectionResult();
        }
        result.setRedirectCount(result.getRedirectCount() + 1);
        redirectionRepository.save(result);
        return result;
    }

    private Redirection
            createNewRedirectionResult() {
        Redirection result;
        result = new Redirection();
        result.setUrl(HELP_URL);
        result.setRedirectionCode(302);
        return result;
    }

    @Override
    public RegisterResponseHolder
            registerUrl(String userEmail, AddUrlRequest registerRequest) {
        final RegisterResponseHolder registerResponseHolder = new RegisterResponseHolder();
        Redirection redirection;

        redirection = redirectionRepository.findByUserEmailAndUrl(userEmail, registerRequest.getUrl());
        if (redirection == null) {
            return processRedirectionObjectDoesNotExistCase(userEmail, registerRequest, registerResponseHolder);
        }

        if (isDoNothingCase(registerRequest, redirection))
            return registerResponseHolder;
        else
            return processUpdateRedirectionObjectCase(registerRequest, registerResponseHolder, redirection);
    }

    private boolean
            isDoNothingCase(AddUrlRequest registerRequest, Redirection redirection) {
        return registerRequest.getRedirectionType() == redirection.getRedirectionCode();
    }

    private RegisterResponseHolder
            processRedirectionObjectDoesNotExistCase(String userEmail,
                    AddUrlRequest registerRequest,
                    RegisterResponseHolder registerResponseHolder) {
        final Redirection redirection = createNewRedirectionObjectAndAddItToDb(userEmail, registerRequest);
        registerResponseHolder = processRegisterResponseHolderForNewObject(registerResponseHolder, redirection);
        return registerResponseHolder;
    }

    private RegisterResponseHolder
            processRegisterResponseHolderForNewObject(RegisterResponseHolder registerResponseHolder,
                    Redirection redirection) {
        registerResponseHolder.getResponseResult().setResult(redirection.getShortUrl());
        registerResponseHolder.setStatus(HttpStatus.CREATED);
        return registerResponseHolder;
    }

    private RegisterResponseHolder
            processUpdateRedirectionObjectCase(AddUrlRequest registerRequest,
                    RegisterResponseHolder registerResponseHolder,
                    Redirection redirection) {
        redirection.setRedirectionCode(registerRequest.getRedirectionType());
        redirectionRepository.save(redirection);

        registerResponseHolder.getResponseResult().setResult(BASE_URL + redirection.getShortUrl());
        registerResponseHolder.setStatus(HttpStatus.OK);
        return registerResponseHolder;
    }

    private Redirection
            createNewRedirectionObjectAndAddItToDb(String userEmail, AddUrlRequest registerRequest) {
        Redirection redirection;
        redirection = new Redirection();
        redirection.setUserEmail(userEmail);
        redirection.setUrl(registerRequest.getUrl());
        redirection.setShortUrl(RandomStringUtils.randomAlphabetic(8));
        redirection.setRedirectionCode(302);
        redirectionRepository.save(redirection);

        Redirection result = new Redirection(redirection);

        result.setShortUrl(BASE_URL + redirection.getShortUrl());
        return result;
    }
}

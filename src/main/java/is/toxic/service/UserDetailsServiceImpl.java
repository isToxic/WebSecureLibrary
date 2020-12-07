package is.toxic.service;

import is.toxic.dao.AppRoleDAO;
import is.toxic.dao.AppUserDAO;
import is.toxic.entity.AppRole;
import is.toxic.entity.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserDAO appUserDAO;
    private final AppRoleDAO appRoleDAO;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AppUser appUser = appUserDAO.findUserAccount(userName);
        if (appUser == null) {
            log.error("User not found: {}", userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }
        log.info("User found: {}", appUser);
        List<String> roleNames = appRoleDAO.getRoleNames(appUser.getUserId());
        List<GrantedAuthority> grantList = new ArrayList<>();
        if (roleNames != null) {
            for (String role : roleNames) {
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }
        }
        return new User(appUser.getUserName(), appUser.getEncrytedPassword(), grantList);
    }

    public boolean saveUser(AppUser user) {
        AppUser userFromDB = appUserDAO.findUserAccount(user.getUserName());
        if (userFromDB != null) {
            return false;
        }
        user.setEncrytedPassword(passwordEncoder().encode(user.getEncrytedPassword()));
        user.setEnabled(true);
        appUserDAO.save(user);
        return true;
    }
}

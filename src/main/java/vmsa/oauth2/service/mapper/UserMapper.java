package vmsa.oauth2.service.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import vmsa.oauth2.service.impl.UserDetailsImpl;

import java.util.List;
import java.util.Map;

@Mapper
@Repository("UserMapper")
public interface UserMapper {
    UserDetails findByUsername(String username);

    void deleteUser(String username);

    void insertUser(UserDetailsImpl userDetailsImpl);

    void insertAllUser(Map<String, List> users);

}

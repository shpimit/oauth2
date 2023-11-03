package vmsa.oauth2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import vmsa.oauth2.service.impl.UserDetailsImpl;
import vmsa.oauth2.service.mapper.UserMapper;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class Oauth2ApplicationTests {

	@Resource(name="UserMapper")
	private UserMapper userMapper;

	@Test
	void contextLoads() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		System.out.println(passwordEncoder.encode("gvpro : " + passwordEncoder.encode("gcat")));
	}

	@Test
	public void insertUserTest() throws Exception {

		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		UserDetailsImpl userDetailsImpl = new UserDetailsImpl(new Long(999), "vaatz", passwordEncoder.encode("vaatz123"), 1, "ADMIN", "ROLE_ADMIN", 1, new Date(System.currentTimeMillis()));

		userMapper.insertUser(userDetailsImpl);
	}

	@Test
	public void insertAllUserTest() throws Exception {

		userMapper.deleteUser("sunho");
		userMapper.deleteUser("admin");
		userMapper.deleteUser("manager");

		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		// create users
		UserDetailsImpl sunho   = new UserDetailsImpl(1L, "sunho", passwordEncoder.encode("sunho123"), 1, "ROLE_USER", "ACCESS_TEST1", 1, new Date(System.currentTimeMillis()));
		UserDetailsImpl admin   = new UserDetailsImpl(2L, "admin", passwordEncoder.encode("admin123"), 1, "ROLE_ADMIN", "ACCESS_TEST1,ACCESS_TEST2", 1, new Date(System.currentTimeMillis()));
		UserDetailsImpl manager = new UserDetailsImpl(3L, "manager", passwordEncoder.encode("manager"), 1, "ROLE_ADMIN", "ACCESS_TEST1", 1, new Date(System.currentTimeMillis()));

		List<UserDetailsImpl> userList = Arrays.asList(sunho, admin, manager);

		Map<String, List> userMap = new HashMap<>();
		userMap.put("users", userList);

		userMapper.insertAllUser(userMap);
	}

}

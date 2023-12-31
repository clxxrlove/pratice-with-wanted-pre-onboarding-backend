package preonboarding.wanted.backend.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import preonboarding.wanted.backend.domain.user.dto.EnterpriseUserDto;
import preonboarding.wanted.backend.domain.user.dto.UserDto;
import preonboarding.wanted.backend.domain.user.model.EnterpriseUser;
import preonboarding.wanted.backend.domain.user.model.User;
import preonboarding.wanted.backend.domain.user.repository.EnterpriseUserRepository;
import preonboarding.wanted.backend.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
public class EnterpriseUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EnterpriseUserRepository enterpriseUserRepository;

    @Autowired
    public EnterpriseUserService(PasswordEncoder passwordEncoder, UserRepository userRepository, EnterpriseUserRepository enterpriseUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.enterpriseUserRepository = enterpriseUserRepository;
    }

    public EnterpriseUser join(UserDto userDto, EnterpriseUserDto enterpriseUserDto) {
        User user = User.builder()
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .build();

        validateDuplicateUser(userDto.toEntity());
        EnterpriseUser enterpriseUser = new EnterpriseUser(null, enterpriseUserDto.name(), enterpriseUserDto.country(), enterpriseUserDto.region(), user);
        return enterpriseUserRepository.save(enterpriseUser);
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    public Optional<EnterpriseUser> findEnterpriseUserById(Long id) {
        return enterpriseUserRepository.findById(id);
    }

    public void deleteEnterpriseUserById(Long id) {
        userRepository.deleteUserById(id);
    }
}

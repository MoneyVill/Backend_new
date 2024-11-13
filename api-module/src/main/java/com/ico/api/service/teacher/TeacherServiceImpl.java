package com.ico.api.service.teacher;

import com.ico.api.dto.user.TeacherSignUpRequestDto;
import com.ico.api.user.JwtTokenProvider;
import com.ico.core.code.Role;
import com.ico.core.code.Status;
import com.ico.core.entity.Certification;
import com.ico.core.entity.Teacher;
import com.ico.core.exception.CustomException;
import com.ico.core.exception.ErrorCode;
import com.ico.core.repository.CertificationRepository;
import com.ico.core.repository.StudentRepository;
import com.ico.core.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Random;

/**
 * Teacher ServiceImpl
 *
 * @author 강교철
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final CertificationRepository certificationRepository;
//    private final S3UploadService s3;

    @Override
    @Transactional
    public Long signUp(TeacherSignUpRequestDto requestDto) {
        // 교사 회원가입
        Teacher teacher = Teacher.builder()
                .identity(requestDto.getIdentity())
                .password(requestDto.getPassword())
                .name(requestDto.getName())
                .status(Status.WAITING)
                .role(Role.TEACHER)
                .build();

        if (teacherRepository.findByIdentity(requestDto.getIdentity()).isPresent()
                || studentRepository.findByIdentity(requestDto.getIdentity()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_ID); // ID 중복 확인
        }

        if (!requestDto.getPassword().equals(requestDto.getCheckedPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_WRONG); // 비밀번호 확인
        }

        teacher.encodeTeacherPassword(passwordEncoder);
        teacherRepository.save(teacher);

        return teacher.getId();
    }

    @Override
    @Transactional
    public void certifiedImage(HttpServletRequest request, MultipartFile file) {
        String token = jwtTokenProvider.parseJwt(request);
        Long teacherId = jwtTokenProvider.getId(token);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<Certification> optionalCertification = certificationRepository.findByTeacherId(teacherId);
        // 기존 인증서를 삭제하고 교사 상태를 승인 대기 상태로 변경
        if (optionalCertification.isPresent()) {
            teacher.setStatus(Status.WAITING);
            teacherRepository.save(teacher);
            certificationRepository.delete(optionalCertification.get());
        }

        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_IMAGE);
        }

        String image = "test";
        Certification certification = Certification.builder()
                .teacher(teacher)
                .image(image)
                .build();
        certificationRepository.save(certification);
    }
}

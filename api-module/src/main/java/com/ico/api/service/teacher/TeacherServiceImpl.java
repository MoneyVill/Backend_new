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
    public Long signUp(TeacherSignUpRequestDto requestDto, MultipartFile file) {
        // 교사 회원가입
        Teacher teacher = Teacher.builder()
                .identity(requestDto.getIdentity())
                .password(requestDto.getPassword())
                .name(requestDto.getName())
                .status(Status.WAITING)
                .role(Role.TEACHER)
                .phoneNum(requestDto.getPhoneNum())
                .build();

        if (teacherRepository.findByIdentity(requestDto.getIdentity()).isPresent()
                || studentRepository.findByIdentity(requestDto.getIdentity()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_ID);
        }

        if (!requestDto.getPassword().equals(requestDto.getCheckedPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }

        teacher.encodeTeacherPassword(passwordEncoder);
        teacherRepository.save(teacher);

        // 교사 인증서 저장
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_IMAGE);
        }
        String image = "test";
        Certification certification = Certification.builder()
                .teacher(teacher)
                .image(image)
                .build();
        certificationRepository.save(certification);

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
        // 교사인증서를 보낸 사람이 요청하면 전에 보낸 교사인증서를 삭제해줌
        if (optionalCertification.isPresent()) {
            // S3 서버에서 파일 삭제
//            s3.deleteFile(optionalCertification.get().getImage());

            // 승인 상태 변경
            teacher.setStatus(Status.WAITING);
            teacherRepository.save(teacher);

            // Certification 삭제
            certificationRepository.delete(optionalCertification.get());
        }
        // 교사 인증서 저장
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

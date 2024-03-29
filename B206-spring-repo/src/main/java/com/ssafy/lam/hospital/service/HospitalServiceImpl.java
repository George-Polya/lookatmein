package com.ssafy.lam.hospital.service;

import com.ssafy.lam.common.EncodeFile;
import com.ssafy.lam.config.MultipartConfig;
import com.ssafy.lam.customer.domain.Customer;
import com.ssafy.lam.customer.domain.CustomerRepository;
import com.ssafy.lam.favorites.domain.FavoritesRepository;
import com.ssafy.lam.file.domain.UploadFile;
import com.ssafy.lam.file.service.UploadFileService;
import com.ssafy.lam.hospital.domain.*;
import com.ssafy.lam.hospital.dto.*;
import com.ssafy.lam.reviewBoard.domain.ReviewBoard;
import com.ssafy.lam.reviewBoard.dto.ReviewListDisplay;
import com.ssafy.lam.user.domain.User;
import com.ssafy.lam.user.domain.UserRepository;
import com.ssafy.lam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DoctorRepository doctorRepository;
    private final CareerRepository careerRepository;
    private final HospitalCategoryRepository hospitalCategoryRepository;
    private final UploadFileService uploadFileService;
    private final DoctorCategoryRepository doctorCategoryRepository;
    private final FavoritesRepository favoritesRepository;
    MultipartConfig multipartConfig = new MultipartConfig();
    private String uploadPath = multipartConfig.multipartConfigElement().getLocation();

    private final CustomerRepository customerRepository;


    private Logger log = LoggerFactory.getLogger(HospitalServiceImpl.class);
    @Override
    public Hospital createHospital(HospitalDto hospitalDto, List<CategoryDto> categoryDto, MultipartFile registrationFile) {
        log.info("createHospital : {}", hospitalDto);
        List<String> roles = new ArrayList<>();
        roles.add("HOSPITAL");
        User user = User.builder()
                .name(hospitalDto.getHospitalInfo_name())
                .userId(hospitalDto.getHospitalInfo_id())
                .password(hospitalDto.getHospitalInfo_password())
                .userType("HOSPITAL")
                .roles(roles)
                .build();
        userService.createUser(user);
        Hospital hospital = Hospital.builder()
                .user(user)
                .tel(hospitalDto.getHospitalInfo_phoneNumber())
                .address(hospitalDto.getHospitalInfo_address())
                .intro(hospitalDto.getHospitalInfo_introduce())
                .email(hospitalDto.getHospitalInfo_email())
                .openTime(hospitalDto.getHospitalInfo_open())
                .closeTime(hospitalDto.getHospitalInfo_close())
                .url(hospitalDto.getHospitalInfo_url())
                .build();
        UploadFile uploadFile = uploadFileService.store(registrationFile);
        hospital.setRegistrationFile(uploadFile);
        hospital = hospitalRepository.save(hospital);
        for (CategoryDto category : categoryDto) {
            log.info("category : {}", category);
            HospitalCategory hospitalCategoryEntity = HospitalCategory.builder()
                    .part(category.getPart())
                    .hospital(hospital)
                    .build();
            hospitalCategoryRepository.save(hospitalCategoryEntity);
        }
        return hospital;    
    }
    @Override
    public HospitalDto getHospital(long userSeq) {
        Hospital hospital = hospitalRepository.findByUserUserSeq(userSeq).get();
        Long hospitalSeq = hospital.getHospitalSeq();
        List<HospitalCategory> hospitalCategoryList  = hospitalCategoryRepository.findAllByHospitalHospitalSeq(hospitalSeq);
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for(HospitalCategory hc : hospitalCategoryList) {
            categoryDtoList.add(new CategoryDto(hc.getPart()));
        }
        HospitalDto dto = HospitalDto.builder()
                .hospitalInfo_seq(hospital.getHospitalSeq())
                .hospitalInfo_id(hospital.getUser().getUserId())
                .hospitalInfo_password(hospital.getUser().getPassword())
                .hospitalInfo_name(hospital.getUser().getName())
                .hospitalInfo_phoneNumber(hospital.getTel())
                .hospitalInfo_introduce(hospital.getIntro())
                .hospitalInfo_email(hospital.getEmail())
                .hospitalInfo_address(hospital.getAddress())
                .hospitalInfo_open(hospital.getOpenTime())
                .hospitalInfo_close(hospital.getCloseTime())
                .hospitalInfo_url(hospital.getUrl())
                .hospitalInfo_rejected(hospital.isRejected())
                .hospitalInfo_category(categoryDtoList)
                .build();

        if(hospital.getProfileFile() != null){
            Path path = Paths.get(uploadPath +"/" + hospital.getProfileFile().getName());
            try{
                String base64 = EncodeFile.encodeFileToBase64(path);
                String type = hospital.getProfileFile().getType();
                dto.setHospitalProfileBase64(base64);
                dto.setHospitalProfileType(type);

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dto;

    }
    @Override
    public Hospital updateHospital(long userSeq, HospitalDto hospitalDto, MultipartFile profile) {
        User user = userRepository.findById(userSeq).get();
        Hospital hospital = hospitalRepository.findByUserUserSeq(userSeq).get();
        if(profile != null){
            UploadFile uploadFile = uploadFileService.store(profile);
            hospital.setProfileFile(uploadFile);
        }


        user.setPassword(hospitalDto.getHospitalInfo_password());
        user.setName(hospitalDto.getHospitalInfo_name());
        hospital.setTel(hospitalDto.getHospitalInfo_phoneNumber());
        hospital.setEmail(hospitalDto.getHospitalInfo_email());
        hospital.setOpenTime(hospitalDto.getHospitalInfo_open());
        hospital.setCloseTime(hospitalDto.getHospitalInfo_close());
        hospital.setAddress(hospitalDto.getHospitalInfo_address());
        hospital.setUrl(hospitalDto.getHospitalInfo_url());
        if(hospital.isRejected()) {
            hospital.setRejected(false);
        }
        userRepository.save(user);
        return hospitalRepository.save(hospital);
    }
    ////////////
    @Override
    public List<Hospital> getAllHospitalInfo() {
        return hospitalRepository.findByIsApprovedTrue();
    }
    @Override
    public Doctor createDoctor(Long hospitalSeq, DoctorDto doctorDto, MultipartFile doctorProfile) {
        log.info("createDoctor : {}", doctorDto);
        Hospital hospital = hospitalRepository.findById(hospitalSeq).orElse(null);

        Doctor doctor = Doctor.builder()
                .docInfoName(doctorDto.getDoc_info_name())
                .docInfoCategory(doctorDto.getDoc_info_category())
                .hospital(hospital)
                .build();
        if(doctorProfile != null){
            UploadFile uploadFile = uploadFileService.store(doctorProfile);
            doctor.setProfile(uploadFile);
        }
        return doctorRepository.save(doctor);
    }
    @Override
    public HospitalDetailDto getHospitalInfo(Long hospitalSeq) { // 고객이 병원 페이지 조회
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(hospitalSeq);
        if (hospitalOptional.isPresent()) {
            Hospital hospital = hospitalOptional.get();

            double avgScore = hospitalRepository.findAvgByHospitalSeq(hospitalSeq).orElse(0.0);
            int cntReviews = hospitalRepository.countByHospitalSeq(hospitalSeq);
            HospitalDetailDto hospitalDetailDto = HospitalDetailDto.builder()
                    .hospitalInfo_seq(hospitalSeq)
                    .hospitalInfo_name(hospital.getUser().getName())
                    .hospitalInfo_phoneNumber(hospital.getTel())
                    .hospitalInfo_introduce(hospital.getIntro())
                    .hospitalInfo_address(hospital.getAddress())
                    .hospitalInfo_email(hospital.getEmail())
                    .hospitalInfo_open(hospital.getOpenTime())
                    .hospitalInfo_close(hospital.getCloseTime())
                    .hospitalInfo_url(hospital.getUrl())
                    .userSeq(hospital.getUser().getUserSeq())
                    .hospitalInfo_avgScore(avgScore)
                    .hospitalInfo_cntReviews(cntReviews)
                    .build();
            if(hospital.getProfileFile() != null){
                Path path = Paths.get(uploadPath +"/" + hospital.getProfileFile().getName());
                try{
                    String base64 = EncodeFile.encodeFileToBase64(path);
                    String type = hospital.getProfileFile().getType();
                    hospitalDetailDto.setProfileBase64(base64);
                    hospitalDetailDto.setProfileType(type);

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return hospitalDetailDto;
        }
        return null;
    }
    @Override
    public HospitalDetailDto getHospitalLikeInfo(Long hospitalSeq, Long userSeq) { // 고객이 병원 페이지 조회 + 찜
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(hospitalSeq);
        if (hospitalOptional.isPresent()) {
            Hospital hospital = hospitalOptional.get();
            double avgScore = hospitalRepository.findAvgByHospitalSeq(hospitalSeq).orElse(0.0);
            HospitalDetailDto hospitalDetailDto = HospitalDetailDto.builder()
                    .hospitalInfo_seq(hospitalSeq)
                    .hospitalInfo_name(hospital.getUser().getName())
                    .hospitalInfo_phoneNumber(hospital.getTel())
                    .hospitalInfo_introduce(hospital.getIntro())
                    .hospitalInfo_address(hospital.getAddress())
                    .hospitalInfo_open(hospital.getOpenTime())
                    .hospitalInfo_close(hospital.getCloseTime())
                    .hospitalInfo_url(hospital.getUrl())
                    .userSeq(hospital.getUser().getUserSeq())
                    .hospitalInfo_avgScore(avgScore)
                    .hospitalInfo_cntReviews(hospitalRepository.countByHospitalSeq(hospitalSeq))
                    .hospitalInfo_isLiked(favoritesRepository.findFavoritesByUserSeqAndHospitalSeqAndIsLikedTrue(hospitalSeq, userSeq).isLiked())
                    .build();
            return hospitalDetailDto;
        } else {
            return null;
        }
    }
    @Override
    public List<ReviewListDisplay> getReviewsByHospital(Long hospitalSeq) {
        List<ReviewBoard> reviews = hospitalRepository.findReviewsByHospitalSeq(hospitalSeq);

        List<ReviewListDisplay> reviewListDisplays = new ArrayList<>();
        for(ReviewBoard r : reviews) {
            ReviewListDisplay display = ReviewListDisplay.builder()
                    .reviewBoard_seq(r.getSeq())
                    .customer_name(r.getUser().getName())
                    .reviewBoard_title(r.getTitle())
                    .reviewBoard_regDate(r.getRegdate())
                    .reviewBoard_cnt(r.getCnt())
                    .reviewBoard_score(r.getScore())
                    .reviewBoard_doctor(r.getDoctor())
                    .reviewBoard_region(r.getRegion())
                    .reviewBoard_surgery(r.getSurgery())
                    .reviewBoard_hospital(r.getHospital())
                    .reviewBoard_expected_price(r.getExpectedPrice())
                    .reviewBoard_surgery_price(r.getSurgeryPrice())
                    .build();

            User user = r.getUser();
            Customer customer = customerRepository.findByUserUserSeq(user.getUserSeq()).get();
            if(customer.getProfile() != null){
                UploadFile profileFile = customer.getProfile();
                Path path = Paths.get(uploadPath + "/"+profileFile.getName());
                try{
                    String customerProfileBase64 = EncodeFile.encodeFileToBase64(path);
                    String customerProfileType = profileFile.getType();
                    display.setCustomerProfileBase64(customerProfileBase64);
                    display.setCustomerProfileType(customerProfileType);

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            reviewListDisplays.add(display);

        }



        return reviewListDisplays;

    }
    @Override
    public List<Doctor> getHospitalDoctorList(Long hospitalSeq) {
        List<Doctor> doctorList = hospitalRepository.findDoctorByHospitalSeq(hospitalSeq).orElse(null);
        return doctorList;
    }

}
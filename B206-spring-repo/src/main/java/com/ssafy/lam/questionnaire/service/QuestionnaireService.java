package com.ssafy.lam.questionnaire.service;

import com.ssafy.lam.file.domain.UploadFile;
import com.ssafy.lam.file.service.UploadFileService;
import com.ssafy.lam.questionnaire.domain.QuesionnareRepository;
import com.ssafy.lam.questionnaire.domain.Questionnaire;
import com.ssafy.lam.questionnaire.dto.QuestionnaireRequestDto;
import com.ssafy.lam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {
    private Logger log = LoggerFactory.getLogger(QuestionnaireService.class);
    @Autowired
    private QuesionnareRepository quesionnareRepository;
    @Autowired
    private UploadFileService uploadFileService;


    public Questionnaire createQuestionnaire(QuestionnaireRequestDto questionRequestDto, MultipartFile file) {
//        User customer = User.builder().userSeq(questionRequestDto.getCusomter_seq()).build();
//        User hospital = User.builder().userSeq(questionRequestDto.getHospital_seq()).build();

        UploadFile uploadFile = null;
        if(file != null)
            uploadFile = uploadFileService.store(file);
        Questionnaire questionnaire = Questionnaire.builder()
                .blood(questionRequestDto.getQuestionnaire_blood())
                .remark(questionRequestDto.getQuestionnaire_remark())
                .title(questionRequestDto.getQuestionnaire_title())
                .content(questionRequestDto.getQuestionnaire_content())
                .uploadFile(uploadFile)
                .build();

        Questionnaire saveQuesionnaire = quesionnareRepository.save(questionnaire);
        return saveQuesionnaire;

    }

    public Questionnaire getQuestionnaireDetail(Long questionSeq) {
        Questionnaire questionnaire = quesionnareRepository.findById(questionSeq)
                .orElseThrow(() -> new IllegalArgumentException("없는 문진서임 : " + questionSeq));

        return questionnaire;
    }
}

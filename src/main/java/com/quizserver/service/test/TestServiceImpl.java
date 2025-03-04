package com.quizserver.service.test;

import com.quizserver.dto.QuestionDTO;
import com.quizserver.dto.TestDTO;
import com.quizserver.dto.TestDetailsDTO;
import com.quizserver.entities.Question;
import com.quizserver.entities.Test;
import com.quizserver.repository.QuestionsRepository;
import com.quizserver.repository.TestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionsRepository questionsRepository;

    public TestDTO createTest(TestDTO dto) {
        Test test = new Test();

        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setTime(dto.getTime());

        return testRepository.save(test).getDTO();
    }

    public QuestionDTO addQuestionInTest(QuestionDTO dto) {
        Optional<Test> optionalTest = testRepository.findById(dto.getId());
        if(optionalTest.isPresent()) {
            Question question = new Question();
            question.setTest(optionalTest.get());
            question.setQuestionText(dto.getQuestionText());
            question.setOptionA(dto.getOptionA());
            question.setOptionB(dto.getOptionB());
            question.setOptionC(dto.getOptionC());
            question.setOptionD(dto.getOptionD());
            question.setCorrectOption(dto.getCorrectOption());
            return questionsRepository.save(question).getDto();
        }
        throw new EntityNotFoundException("Test not found.");
    }

    public List<TestDTO> getAllTests() {
        return testRepository.findAll().stream().peek(
                test -> test.setTime(test.getQuestions().size() * test.getTime())).collect(Collectors.toList())
                .stream().map(Test::getDTO).collect(Collectors.toList());
    }

    public TestDetailsDTO getAllQuestionsByTest(Long id) {
        Optional<Test> optionalTest = testRepository.findById(id);
        TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
        if(optionalTest.isPresent()) {
            TestDTO testDTO = optionalTest.get().getDTO();
            testDTO.setTime(optionalTest.get().getTime() * optionalTest.get().getQuestions().size());

            testDetailsDTO.setTestDTO(testDTO);
            testDetailsDTO.setQuestions(optionalTest.get().getQuestions().stream().map(Question::getDto).toList());
            return testDetailsDTO;
        }
        return testDetailsDTO;
    }

}

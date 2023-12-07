package com.bonfire.challenge.controller;

import com.bonfire.challenge.Specification.ChallengeSpecification;
import com.bonfire.challenge.entity.Challenge;
import com.bonfire.challenge.repository.ChallengeRepository;
import com.bonfire.challenge.service.ChallengeService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeRepository challengeRepository;
    private final ChallengeService challengeService;

    //챌린지 목록
    @GetMapping("/list")
    public ResponseEntity<?> challengeList(
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String frequency,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String searchWord,
            @RequestParam(defaultValue = "id") String sortCondition,
            @RequestParam(defaultValue = "0") Integer page)
    {
        // 검색 조건
        Specification<Challenge> spec = challengeService.searchSpec(category,frequency, term, searchWord);
        // 정렬
        Sort sortResult = challengeService.sortList(sortCondition);

        PageRequest pageRequest = PageRequest.of(page,10, sortResult);
        Page<Challenge> allChallenge=challengeRepository.findAll(spec,pageRequest);

        if(allChallenge != null){
            return new ResponseEntity<>(allChallenge, HttpStatus.OK);
        }else{
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

    }

//    // 카테고리별 목록
//    @GetMapping("/category")
//    public ResponseEntity<?> challenge(
//            @RequestParam("category") Integer category,
//            @RequestParam(required = false) String frequency,
//            @RequestParam(required = false) String term,
//            @RequestParam(required = false) String searchWord,
//            @RequestParam(defaultValue = "id") String sortCondition,
//            @RequestParam(defaultValue = "0") Integer page)
//    {
//        // 검색 조건
//        Specification<Challenge> spec = challengeService.searchSpec(frequency, term, searchWord);
//        // 정렬
//        Sort sortResult = challengeService.sortList(sortCondition);
//
//        PageRequest pageRequest = PageRequest.of(page,10);
//        Page<Challenge> challenge=challengeRepository.findByCategory(category,pageRequest);
//
//        if(challenge != null){
//            return new ResponseEntity<>(challenge, HttpStatus.OK);
//        }else{
//            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
//        }
//
//    }

    @GetMapping("/select/{id}")
    public ResponseEntity<?> challenge(@PathVariable Long id){
        Optional<Challenge> challenge=challengeRepository.findById(id);
        if(challenge != null){
            return new ResponseEntity<Challenge>((Challenge) challenge.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

    }

    @PutMapping("/update/{id}")
    public void updateChallenge(@PathVariable Long id, @RequestBody Challenge challenge){
        Optional<Challenge> savedChallenge = challengeRepository.findById(id);
        if(savedChallenge.isPresent()){

        }
    }
    @PostMapping("/write")
    public ResponseEntity<?> writeChallenge(@RequestBody Challenge challenge){
        challengeRepository.save(challenge);
        List<Challenge> challengeList=challengeRepository.findAll();

        return new ResponseEntity<List<Challenge>>(challengeList, HttpStatus.OK);
    }

//    @PostMapping("/imageUpload")
//    public void editorImageUpload(
//            @RequestParam MultipartFile upload, HttpServletResponse res, HttpServletRequest req){
//        OutputStream out = null;
//        PrintWriter printWriter = null;
//
//        res.setCharacterEncoding("utf-8");
//        res.setContentType("text/html;charset=utf-8");
//
//        try{
//            UUID uuid = UUID.randomUUID();
//            String fileName = upload.getOriginalFilename();
//            byte[] bytes = upload.getBytes();
//            String imgUploadPath = "/resources" + File.separator + uuid + "." + extension;
//
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }




        //String uploadPath = request.getServletContext().getRealPath("/resources/imageUpload/");

//    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long id){
        challengeRepository.deleteById(id);
        return new ResponseEntity<>("delete success", HttpStatus.OK);
    }
}



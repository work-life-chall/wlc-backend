package com.bonfire.challenge.controller;

import com.bonfire.challenge.entity.Challenge;
import com.bonfire.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeRepository challengeRepository;

    @GetMapping("/select")
    public ResponseEntity<?> challengeList(){
        List<Challenge> challengeList=challengeRepository.findAll();
        if(challengeList != null){
            return new ResponseEntity<List<Challenge>>(challengeList, HttpStatus.OK);
        }else{
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

    }
    @GetMapping("/select/{id}")
    public ResponseEntity<?> challenge(@PathVariable Long id){
        Optional<Challenge> challenge=challengeRepository.findById(id);
        if(challenge != null){
            return new ResponseEntity<Challenge>(challenge.get(), HttpStatus.OK);
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
    @DeleteMapping("/delete/{id}")
    public void deleteChallenge(@PathVariable Long id){
        challengeRepository.deleteById(id);
    }
}



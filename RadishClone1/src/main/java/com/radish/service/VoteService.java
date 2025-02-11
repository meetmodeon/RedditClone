package com.radish.service;

import com.radish.dto.VoteDto;
import com.radish.exceptions.PostNotFoundException;
import com.radish.exceptions.SpringRedditException;
import com.radish.model.Post;
import com.radish.model.Vote;
import com.radish.model.VoteType;
import com.radish.repository.PostRepository;
import com.radish.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto){
        Post post=postRepository.findById(voteDto.getPostId())
                .orElseThrow(()-> new PostNotFoundException("Post Not Found with ID - "+ voteDto.getPostId()));
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post ,authService.getCurrentUser());
        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())){
            throw new SpringRedditException("You have already "+ voteDto.getVoteType()+ "'d for this post");
        }

        if(post.getVoteCount() == null){
            post.setVoteCount(0);
        }

        if(VoteType.UPVOTE.equals(voteDto.getVoteType())){
            post.setVoteCount(post.getVoteCount()+1);
        }else {
            post.setVoteCount(post.getVoteCount()-1);
        }
        voteRepository.save(mapToVote(voteDto,post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post){
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}

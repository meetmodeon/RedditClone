package com.radish.service;

import com.radish.dto.SubredditDto;
import com.radish.exceptions.SpringRedditException;
import com.radish.model.Subreddit;
import com.radish.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        Subreddit subreddit= modelMapper.map(subredditDto,Subreddit.class);
        Subreddit save= subredditRepository.save(subreddit);
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll(){
        List<Subreddit> subreddits=subredditRepository.findAll();
        List<SubredditDto> subredditDtos=modelMapper.map(subreddits,new TypeToken<List<SubredditDto>>(){}.getType());
        return subredditDtos;
    }

    public SubredditDto getSubreddit(Long id){
        Subreddit subreddit= subredditRepository.findById(id)
                .orElseThrow(()-> new SpringRedditException("No subreddit found with ID -"+ id));
        SubredditDto subredditDto= modelMapper.map(subreddit,SubredditDto.class);
        return subredditDto;
    }
}

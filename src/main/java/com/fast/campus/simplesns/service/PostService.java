package com.fast.campus.simplesns.service;

import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SnsApplicationException;
import com.fast.campus.simplesns.model.entity.PostEntity;
import com.fast.campus.simplesns.model.entity.UserEntity;
import com.fast.campus.simplesns.repository.PostEntityRepository;
import com.fast.campus.simplesns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

	private final PostEntityRepository postEntityRepository;
	private final UserEntityRepository userEntityRepository;

	@Transactional
	public void create(String title, String body, String userName) {
		UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
				new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
		postEntityRepository.save(PostEntity.of(title, body, userEntity));
	}

	@Transactional
	public void modify(String title, String body, String userName, Integer PostId) {
		UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
				new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
		// post exist

		// post permission
	}
}

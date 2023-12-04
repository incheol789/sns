package com.fast.campus.simplesns.service;

import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SnsApplicationException;
import com.fast.campus.simplesns.fixture.PostEntityFixture;
import com.fast.campus.simplesns.fixture.UserEntityFixture;
import com.fast.campus.simplesns.model.entity.PostEntity;
import com.fast.campus.simplesns.model.entity.UserEntity;
import com.fast.campus.simplesns.repository.PostEntityRepository;
import com.fast.campus.simplesns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class PostServiceTest {

	@Autowired
	private PostService postService;

	@MockBean
	private PostEntityRepository postEntityRepository;

	@MockBean
	private UserEntityRepository userEntityRepository;

//	@Test
//	void 포스트작성이_성공한경우() {
//		// given
//		String title = "title";
//		String body = "body";
//		String userName = "userName";
//
//		// when
//		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
//		when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));
//
//		Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));
//
//		// then
//	}

	@Test
	void 포스트작성시_요청한유저가_존재하지않는경우() {
		// given
		String title = "title";
		String body = "body";
		String userName = "userName";

		// when
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
		when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

		// then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
		Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
	}

	@Test
	void 포스트수정이_성공한경우() {
		// given
		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();
		// when

		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
		when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

		// then
		Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
	}

	@Test
	void 포스트수정시_포스트가_존재하지않는_경우() {
		// given
		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();
		// when

		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

		// then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
				() -> postService.modify(title, body, userName, postId));
		Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

	}

	@Test
	void 포스트수정시_권한이_없는_경우() {
		// given
		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

		// when
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		// then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
				() -> postService.modify(title, body, userName, postId));
		Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

	}

	@Test
	void 포스트삭제가_성공한경우() {
		// given
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();

		// when
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		// then
		Assertions.assertDoesNotThrow(() -> postService.delete(userName, 1));
	}

	@Test
	void 포스트삭제시_포스트가_존재하지않는_경우() {
		// given
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();

		// when

		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

		// then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
				() -> postService.delete(userName, 1));
		Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

	}

	@Test
	void 포스트삭제시_권한이_없는_경우() {
		// given
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

		// when
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		// then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
				() -> postService.delete(userName, 1));
		Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

	}

	@Test
	void 피드목록요청이_성공한경우() {

		// when
		Pageable pageable = mock(Pageable.class);
		when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

		// then
		Assertions.assertDoesNotThrow(() -> postService.list(pageable));
	}

	@Test
	void 내_피드목록요청이_성공한경우() {

		// given
		Pageable pageable = mock(Pageable.class);
		UserEntity user = mock(UserEntity.class);

		// when
		when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
		when(postEntityRepository.findAllByUser(eq(user), eq(pageable))).thenReturn(Page.empty());

		// then
		Assertions.assertDoesNotThrow(() -> postService.my("", pageable));
	}

}

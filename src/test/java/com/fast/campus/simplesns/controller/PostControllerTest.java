package com.fast.campus.simplesns.controller;

import com.fast.campus.simplesns.controller.request.PostCreateRequest;
import com.fast.campus.simplesns.controller.request.PostModifyRequest;
import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SnsApplicationException;
import com.fast.campus.simplesns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PostService postService;

	@Test
	@WithMockUser
	void 포스트작성() throws Exception {
		// given
		String title = "title";
		String body = "body";

		// when

		// then
		mockMvc.perform(post("/api/v1/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
				).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithAnonymousUser
	void 포스트작성시_로그인하지않은경우() throws Exception {
		// given
		String title = "title";
		String body = "body";

		// when (로그인하지 않은 경우)

		// then
		mockMvc.perform(post("/api/v1/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
				).andDo(print())
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser
	void 포스트수정() throws Exception {
		// given
		String title = "title";
		String body = "body";

		// when

		// then
		mockMvc.perform(put("/api/v1/posts/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
				).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithAnonymousUser
	void 포스트수정시_로그인하지않은경우() throws Exception {
		// given
		String title = "title";
		String body = "body";

		// when

		// then
		mockMvc.perform(put("/api/v1/posts/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
				).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void 포스트수정시_본인이_작성한_글이_아니라면_에러발생() throws Exception {
		// given
		String title = "title";
		String body = "body";

		// when(mocking)
		doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(eq(title), eq(body), any(), eq(1));

		// then
		mockMvc.perform(put("/api/v1/posts/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
				).andDo(print())
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser
	void 포스트수정시_수정하려는_글이_없는경우_에러발생() throws Exception {
		// given
		String title = "title";
		String body = "body";

		// when(mocking)
		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(eq(title), eq(body), any(), eq(1));

		// then
		mockMvc.perform(put("/api/v1/posts/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
				).andDo(print())
				.andExpect(status().isNotFound());
	}
}

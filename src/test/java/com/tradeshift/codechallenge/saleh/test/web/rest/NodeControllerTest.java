package com.tradeshift.codechallenge.saleh.test.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradeshift.codechallenge.saleh.business.service.NodeService;
import com.tradeshift.codechallenge.saleh.dto.NodeDto;
import com.tradeshift.codechallenge.saleh.exception.InvalidNodeException;
import com.tradeshift.codechallenge.saleh.exception.InvalidParentException;
import com.tradeshift.codechallenge.saleh.exception.ResultError;
import com.tradeshift.codechallenge.saleh.exception.RootAlreadyExistException;
import com.tradeshift.codechallenge.saleh.web.rest.NodeController;
import com.tradeshift.codechallenge.saleh.web.rest.RestResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NodeController.class)
@RunWith(SpringRunner.class)
public class NodeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private NodeService nodeService;

	private NodeDto root = new NodeDto(1, "root", null, 0);
	private NodeDto ch1 = new NodeDto(2, "ch1", root, 1);
	private NodeDto ch2 = new NodeDto(3, "ch2", root, 1);
	private NodeDto ch1_1 = new NodeDto(4, "ch1.1", ch1, 2);
	private NodeDto ch1_2 = new NodeDto(5, "ch1.2", ch1, 2);
	private NodeDto ch1_1_1 = new NodeDto(6, "ch1.1.1", ch1, 3);
	private NodeDto ch1_1_2 = new NodeDto(7, "ch1.1.2", ch1, 3);
	private NodeDto ch1_1_1_1 = new NodeDto(8, "ch1.1.1.1", ch1, 4);
	private NodeDto ch1_1_1_2 = new NodeDto(9, "ch1.1.1.2", ch1, 4);
	List<NodeDto> all = new ArrayList<>();
	List<NodeDto> ch1_1Descendant = new ArrayList<>();

	@Before
	public void initTest() {
		all.add(root);
		all.add(ch1);
		all.add(ch2);
		all.add(ch1_1);
		all.add(ch1_2);
		all.add(ch1_1_1);
		all.add(ch1_1_2);
		all.add(ch1_1_1_1);
		all.add(ch1_1_1_2);

		ch1_1Descendant.add(ch1_1_1);
		ch1_1Descendant.add(ch1_1_2);
		ch1_1Descendant.add(ch1_1_1_1);
		ch1_1Descendant.add(ch1_1_1_2);
	}

	@Test
	public void testGetRoot_ValidUrlAndMethodAndContentType_Returns200AndRoot() throws Exception {

		given(nodeService.getRoot()).willReturn(root);
		ResultActions resultActions = mockMvc.perform(get("/rest/v1/node/root")
				.contentType("application/json")).andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<NodeDto> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<NodeDto>>() {
		});

		assertThat(response.getErrorCode()).isEqualTo(0);
		assertThat(response.getData().getId()).isEqualTo(root.getId());
		assertThat(response.getData().getName()).isEqualTo(root.getName());
		assertThat(response.getData().getHeight()).isEqualTo(root.getHeight());
	}

	@Test
	public void testGetRoot_ValidUrlAndContentTypeAndInvalidMethod_Returns400AndErrorCode500() throws Exception {

		given(nodeService.getRoot()).willReturn(root);
		ResultActions resultActions = mockMvc.perform(post("/rest/v1/node/root")
				.contentType("application/json")).andExpect(status().isBadRequest());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<Object> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<Object>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.InternalError.getCode());
	}

	@Test
	public void testSave_ValidUrlAndMethodAndContentTypeAndData_Returns200AndRoot() throws Exception {
		given(nodeService.save(any(NodeDto.class))).will(saveNodeDto);
		ResultActions resultActions = mockMvc.perform(post("/rest/v1/node/save")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(new NodeDto(null, "Root", null, 0))))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<NodeDto> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<NodeDto>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.Success.getCode());
		assertThat(response.getData().getId()).isNotNull();
		assertThat(response.getData().getParent()).isNull();
		assertThat(response.getData().getName()).isEqualTo("Root");
		assertThat(response.getData().getHeight()).isEqualTo(root.getHeight());
	}


	@Test
	public void testSave_ValidUrlAndMethodAndContentTypeAndDuplicateRoot_Returns400AndErrorCode102()
			throws Exception {
		given(nodeService.save(any(NodeDto.class))).willThrow(new RootAlreadyExistException());
		ResultActions resultActions = mockMvc.perform(post("/rest/v1/node/save")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(new NodeDto(null, "Root", null, 0))))
				.andExpect(status().isBadRequest());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<Object> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<Object>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.RootAlreadyExist.getCode());
	}

	@Test
	public void testSave_ValidUrlAndMethodAndContentTypeAndChildData_Returns200AndNodeDto() throws Exception {
		given(nodeService.save(any(NodeDto.class))).will(saveNodeDto);
		ResultActions resultActions = mockMvc.perform(post("/rest/v1/node/save")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(
						new NodeDto(null, ch1.getName(), root, null))))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<NodeDto> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<NodeDto>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.Success.getCode());
		assertThat(response.getData().getId()).isNotNull();
		assertThat(response.getData().getParent().getId()).isEqualTo(root.getId());
		assertThat(response.getData().getName()).isEqualTo(ch1.getName());
		assertThat(response.getData().getHeight()).isEqualTo(ch1.getHeight());
	}

	@Test
	public void testSave_ValidUrlAndMethodAndContentTypeAndInvalidParent_Returns400AndErrorCode101()
			throws Exception {
		given(nodeService.save(any(NodeDto.class))).willThrow(new InvalidParentException());
		ResultActions resultActions = mockMvc.perform(post("/rest/v1/node/save")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(ch1)))
				.andExpect(status().isBadRequest());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<Object> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<Object>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.InvalidParent.getCode());
	}

	@Test
	public void testSave_ValidUrlAndMethodAndContentTypeAndInvalidChild_Returns400AndErrorCode100()
			throws Exception {
		given(nodeService.save(any(NodeDto.class))).willThrow(new InvalidNodeException());
		ResultActions resultActions = mockMvc.perform(post("/rest/v1/node/save")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(ch1)))
				.andExpect(status().isBadRequest());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<Object> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<Object>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.InvalidNode.getCode());
	}

	@Test
	public void testGetAll_ValidUrlAndMethodAndContentTypeAndTreeExist_Returns200AndListOfNodes() throws Exception {
		given(nodeService.getAll()).willReturn(all);
		ResultActions resultActions = mockMvc.perform(get("/rest/v1/node/all")
				.contentType("application/json"))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<List<NodeDto>> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<List<NodeDto>>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.Success.getCode());
		assertThat(response.getData().size()).isGreaterThan(0);
	}

	@Test
	public void testGetDescendant_ValidUrlAndMethodAndContentTypeAndChildExist_Returns200AndListOfNodes()
			throws Exception {
		given(nodeService.getDescendant(any(Integer.class))).willReturn(ch1_1Descendant);
		ResultActions resultActions = mockMvc.perform(get("/rest/v1/node/descendant/2")
				.contentType("application/json"))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<List<NodeDto>> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<List<NodeDto>>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.Success.getCode());
		assertThat(response.getData().size()).isGreaterThan(0);
	}

	@Test
	public void testGetDescendant_ValidUrlAndMethodAndContentTypeAndChildNotExist_Returns400AndErrorCode100()
			throws Exception {
		given(nodeService.getDescendant(any(Integer.class))).willThrow(new InvalidNodeException());
		ResultActions resultActions = mockMvc.perform(get("/rest/v1/node/descendant/2")
				.contentType("application/json"))
				.andExpect(status().isBadRequest());
		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		RestResult<Object> response = objectMapper.readValue(contentAsString,
				new TypeReference<RestResult<Object>>() {
				});

		assertThat(response.getErrorCode()).isEqualTo(ResultError.InvalidNode.getCode());
	}

	Answer<NodeDto> saveNodeDto = invocation -> {
		Object[] args = invocation.getArguments();
		NodeDto input = (NodeDto) args[0];
		if (input.getId() == null) {
			input.setId(Math.abs(new Random().nextInt()));
		}
		if (input.getParent() != null && input.getParent().getHeight() != null) {
			input.setHeight(input.getParent().getHeight() + 1);
		}
		return input;
	};
}

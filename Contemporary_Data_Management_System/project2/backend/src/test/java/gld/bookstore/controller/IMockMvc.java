package gld.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class IMockMvc {
    MockMvc mockMvc;
    ObjectMapper objectMapper;

    public IMockMvc(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public <Request,Response> Response testPost(String path, Request requestBody, int status, Class<Response> Response) throws Exception {
        String response = mockMvc.perform(
                MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(status))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response,Response);
    }

    public <Request,Response> Response testPost(String path, Request requestBody, int status, Class<Response> Response, String token) throws Exception {
        String response = mockMvc.perform(
                MockMvcRequestBuilders.post(path)
                        .header("token",token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(status))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response,Response);
    }

    public <Request,Response> Response testPatch(String path, Request requestBody, int status, Class<Response> Response, String token) throws Exception {
        String response = mockMvc.perform(
                MockMvcRequestBuilders.patch(path)
                        .header("token",token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(status))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response,Response);
    }

    public <Response> Response testGet(String path, int status, Class<Response> Response, String token) throws Exception {
        String response = mockMvc.perform(
                MockMvcRequestBuilders.get(path)
                        .header("token",token)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(status))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response,Response);
    }

    public <Response> Response testDelete(String path, int status, Class<Response> Response, String token) throws Exception {
        String response = mockMvc.perform(
                MockMvcRequestBuilders.delete(path)
                        .header("token",token)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(status))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response,Response);
    }
}

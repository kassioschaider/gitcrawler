package br.com.kassioschaider.gitcrawler.controller;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GitCrawlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFileCounter() throws Exception {
        URI uri = new URI("/counter");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"linkRepository\": \"https://github.com/kassioschaider/price_stalker\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("[{\"extension\":\"gitignore\",\"count\":12,\"lines\":42,\"bytes\":434},{\"extension\":\"css\",\"count\":1,\"lines\":8,\"bytes\":128000},{\"extension\":\"vue\",\"count\":1,\"lines\":23,\"bytes\":552},{\"extension\":\"js\",\"count\":4,\"lines\":89,\"bytes\":339789.20},{\"extension\":\"gitattributes\",\"count\":1,\"lines\":5,\"bytes\":111},{\"extension\":\"example\",\"count\":1,\"lines\":44,\"bytes\":737},{\"extension\":\"txt\",\"count\":1,\"lines\":2,\"bytes\":24},{\"extension\":\"ico\",\"count\":1,\"lines\":0,\"bytes\":0},{\"extension\":\"php\",\"count\":79,\"lines\":3830,\"bytes\":118462.88},{\"extension\":\"yml\",\"count\":1,\"lines\":13,\"bytes\":174},{\"extension\":\"no extension\",\"count\":1,\"lines\":53,\"bytes\":1689.60},{\"extension\":\"json\",\"count\":2,\"lines\":90,\"bytes\":2805.76},{\"extension\":\"md\",\"count\":1,\"lines\":72,\"bytes\":4198.4},{\"extension\":\"xml\",\"count\":1,\"lines\":33,\"bytes\":1157.12},{\"extension\":\"editorconfig\",\"count\":1,\"lines\":15,\"bytes\":213},{\"extension\":\"lock\",\"count\":1,\"lines\":5243,\"bytes\":188416},{\"extension\":\"scss\",\"count\":2,\"lines\":27,\"bytes\":475},{\"extension\":\"htaccess\",\"count\":1,\"lines\":21,\"bytes\":593},{\"extension\":\"config\",\"count\":1,\"lines\":28,\"bytes\":1198.08}]", content);
    }

    @Test
    public void testInvalidLinkRepositoryMalformedUrl() throws Exception {
        URI uri = new URI("/counter");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"linkRepository\": \"github.com/kassioschaider/price_stalker\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("Invalid link repository! Malformed URL!", content);
    }

    @Test
    public void testInvalidLinkRepositoryUnknownHost() throws Exception {
        URI uri = new URI("/counter");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"linkRepository\": \"https://githubhub.com/kassioschaider/price_stalker\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("Invalid link repository! Unknown Host!", content);
    }
}
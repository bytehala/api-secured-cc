package eg.bytehala.apisecuredcc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApiSecuredCcApplicationTests {

	@Autowired
    private MockMvc mvc;

	@Test
	public void contextLoads() {
	}

	@Test
    public void testRoot() throws Exception {
	    this.mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    public void mustGetAToken() throws Exception {
	    String accessToken = obtainAccessToken();
	    assertNotNull(accessToken);
    }

    @Test
    public void testClientCredentials() throws Exception {
//	    this.mvc.perform(get)
        String accessToken = obtainAccessToken();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("access_token", accessToken);

        this.mvc.perform(post("/api/test")
                .params(params)
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk());

    }

//    private String getResultS

    private String obtainAccessToken() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("scope", "readwrite");
//        params.add("client_secret", "testClientSecret");

        ResultActions result
                = mvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic("fooClientIdPassword","testClientSecret"))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }


}


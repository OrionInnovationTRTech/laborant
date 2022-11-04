package tr.com.orioninc.laborant;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import tr.com.orioninc.laborant.model.Lab;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
class InternProjectApplicationTests extends TestBuilder {

	public InternProjectApplicationTests(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
	}

	@Test
	public void getAllLabs() throws Exception {
		String uri = "/api/getAllLabs";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		Assertions.assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		Lab[] lablist = super.mapFromJson(content, Lab[].class);
		Assertions.assertTrue(lablist.length > 0);
	}
	@Test
	public void getLabByName() throws Exception {
		String uri = "/api/getLabByName?labName=lab1";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		Assertions.assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		Lab lab = super.mapFromJson(content, Lab.class);
		Assertions.assertEquals("lab1", lab.getLabName());
	}

}

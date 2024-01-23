package greencity.controller;

import greencity.TestConst;
import greencity.service.NewsSubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@ExtendWith(MockitoExtension.class)
public class NewsSubscriberControllerTest {
    private final String NEWS_SUBSCRIBER_LINK = "/newsSubscriber";
    @Mock
    NewsSubscriberService newsSubscriberService;

    @InjectMocks
    NewsSubscriberController newsSubscriberController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newsSubscriberController)
            .build();
    }


    @Test
    public void saveTest() throws Exception {
        String email = TestConst.EMAIL;

        mockMvc.perform(post(NEWS_SUBSCRIBER_LINK)
                .param("email",email))
            .andExpect(status().isCreated());

        verify(newsSubscriberService).save(email);
    }
}

package exmpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Sql("/sql/task_rest_controller/test_data.sql")
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ComponentScan
@Transactional
public class TasksRestControllerIT {

    @Autowired
    MockMvc mockMvc;


    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/api/tasks");
        //when
        this.mockMvc.perform(requestBuilder)
        //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                        [
                        {
                            "id": "19b8a399-e261-4e73-9bdd-7b0f5ff5eebf",
                            "details": "Первая задача",
                            "completed": false
                            
                        },
                        {
                            "id": "ee62b986-8593-4de5-8cd3-adb58e659592",
                            "details": "Вторая задача",
                            "completed": true
                        }
                        ]
                        """)
                );

    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "details": "Третья задача"
                    }
                    """);

        //when
        this.mockMvc.perform(requestBuilder)
        //then
                .andExpectAll(status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                        {
                            "details": "Третья задача",
                            "completed": false
                        }
                        """),
                        jsonPath("$.id").exists()
                );

    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .content("""
                    {
                        "details": null
                    }
                    """);

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                        {
                            "errors": ["Task details must be set"]
                        }
                        """, true)
                );
    }
}

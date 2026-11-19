package ar.edu.unsam.programacion3.pythoncito.cucumber;
import ar.edu.unsam.programacion3.pythoncito.PythoncitoApplication;

import io.cucumber.spring.CucumberContextConfiguration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@CucumberContextConfiguration
@SpringBootTest(
    classes = PythoncitoApplication.class
)

@AutoConfigureMockMvc
public class CucumberSpringConfiguration {
}


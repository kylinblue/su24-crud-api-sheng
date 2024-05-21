package com.csc340.restapidemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.json.JacksonJsonParser;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class RestApiController {

    HashMap<Integer, Student> studentDatabase = StudentDBController.loadDB();

    /**
     * Hello World API endpoint.
     *
     * @return response string.
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    /**
     * Greeting API endpoint.
     *
     * @param name the request parameter
     * @return the response string.
     */
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "Dora") String name) {
        return "Hola, soy " + name;
    }


    /**
     * List all students.
     *
     * @return the list of students.
     */
    @GetMapping("students/all")
    public Object getAllStudents() {
        studentDatabase.putAll(StudentDBController.loadDB());
        if (studentDatabase.isEmpty()) {
            studentDatabase.put(1, new Student(1, "sample1", "csc", 3.86));
            StudentDBController.writeDB(studentDatabase);
        }
        return studentDatabase;
    }

    /**
     * Get one student by ID
     *
     * @param id the unique student id.
     * @return the student.
     */
    @GetMapping("students/{id}")
    public Student getStudentById(@PathVariable int id) {
        studentDatabase.putAll(StudentDBController.loadDB());
        return studentDatabase.get(id);
    }


    /**
     * Create a new Student entry.
     *
     * @param student the new Student
     * @return the List of Students.
     */
    @PostMapping("students/create")
    public Object createStudent(@RequestBody Student student) {
        studentDatabase.putAll(StudentDBController.loadDB());
        studentDatabase.put(student.getId(), student);
        StudentDBController.writeDB(studentDatabase);
        return studentDatabase.values();
    }

    /**
     * Delete a Student by id
     *
     * @param id the id of student to be deleted.
     * @return the List of Students.
     */
    @DeleteMapping("students/delete/{id}")
    public Object deleteStudent(@PathVariable int id) {
        studentDatabase.putAll(StudentDBController.loadDB());
        studentDatabase.remove(id);
        StudentDBController.writeDB(studentDatabase);
        return studentDatabase.values();
    }

    /**
     *
     * @param id the id of student to be updated, student the updated Student info.
     * @return the List of Students.
     */
    @PutMapping("students/update/{id}")
    public Object updateStudent(@PathVariable int id, @RequestBody Student student){
        studentDatabase.putAll(StudentDBController.loadDB());
        studentDatabase.replace(id, student);
        StudentDBController.writeDB(studentDatabase);
        return studentDatabase.values();
    }

    /**
     * Get a quote from quotable and make it available our own API endpoint
     *
     * @return The quote json response
     */
    @GetMapping("/quote")
    public Object getQuote() {
        try {
            String url = "https://api.quotable.io/random";
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            //We are expecting a String object as a response from the above API.
            String jSonQuote = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(jSonQuote);

            //Parse out the most important info from the response and use it for whatever you want. In this case, just print.
            String quoteAuthor = root.get("author").asText();
            String quoteContent = root.get("content").asText();
            System.out.println("Author: " + quoteAuthor);
            System.out.println("Quote: " + quoteContent);

            return root;

        } catch (JsonProcessingException ex) {
            Logger.getLogger(RestApiController.class.getName()).log(Level.SEVERE,
                    null, ex);
            return "error in /quote";
        }
    }

    /**
     * Get a list of universities from hipolabs and make them available at our own API
     * endpoint.
     *
     * @return json array
     */
    @GetMapping("/vehicles")
    public Object getVehicleMakes() {
        try {
            String url = "https://vpic.nhtsa.dot.gov/api/vehicles/getallmanufacturers?ManufacturerType=Intermediate&format=json&page=2";
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            String jsonListResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(jsonListResponse).get("Results");

            //The response from the above API is a JSON Array, which we loop through.
            for (JsonNode rt : root) {
                //Extract relevant info from the response and use it for what you want, in this case just print to the console.
                String name = rt.get("Mfr_Name").asText();
                String mfrID = rt.get("Mfr_ID").asText();
                System.out.println("Manufacturer ID: " + mfrID + ": Manufacturer name: " + name);
            }

            return root;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(RestApiController.class.getName()).log(Level.SEVERE,
                    null, ex);
            return "error in /vehicles";
        }
    }
}

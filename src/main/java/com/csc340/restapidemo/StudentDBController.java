package com.csc340.restapidemo;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class StudentDBController {
    /**
     * Load the database, create one if it doesn't exist.
     *
     */
    public static HashMap<Integer, Student> loadDB(){
        try {
            File studentDB = new File("databases/studentDB");
            if (studentDB.createNewFile()){
                System.out.println("File created: " + studentDB.getName());
            }
            else {
                System.out.println("File already exists, load.");
                return studentDBtoHashMap();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<Integer, Student> studentDBtoHashMap() throws FileNotFoundException {
        HashMap<Integer, Student> studentDBMap = new HashMap<>();
        Scanner reader = new Scanner(new FileReader("databases/studentDB"));
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] parts = line.split(":");
            Student student = new Student(Integer.parseInt(parts[1]),
                    parts[2], parts[3], Double.parseDouble(parts[4]));
            studentDBMap.put(student.getId(), student);
        }
        return studentDBMap;
    }

    /**
     * Write map
     */
    public static void writeDB(HashMap<Integer, Student> studentDatabase) {
        try {
            //File studentDB = new File("databases/studentDB");
            hashMapToStudentDB(studentDatabase);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hashMapToStudentDB(HashMap<Integer, Student> studentDatabase) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("databases/studentDB"));
        for (HashMap.Entry<Integer, Student> entry : studentDatabase.entrySet()) {
            writer.write(entry.getKey() + ":" +
                    entry.getValue().getId() + ":" +
                    entry.getValue().getName() + ":" +
                    entry.getValue().getMajor() + ":" +
                    entry.getValue().getGpa() + "\n");
        }
        writer.close();
    }
}

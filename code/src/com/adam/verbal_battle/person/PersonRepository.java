package com.adam.verbal_battle.person;

import com.adam.verbal_battle.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PersonRepository {

    private static PersonRepository INSTANCE = null;

    private List<Person> personList = new ArrayList<>(20);

    public void loadPersons() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                PersonRepository.class.getResourceAsStream("/persons.txt")))) {
            String line;
            int i=1;
            while ((line = reader.readLine()) != null) {
                String[] lineSplits = line.split("\t");
                String name = lineSplits[0];
                int intelligence = Integer.parseInt(lineSplits[1]);
                Character character = Character.findByDesc(lineSplits[2]);
                Person person = new Person(i++, name, intelligence, character);
                personList.add(person);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String formatPersonsWithHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("序号\t姓名\t\t智力\t性格").append(System.lineSeparator());
        int i=1;
        for(Person person: personList) {
            stringBuilder.append(i++).append("\t").append(person).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public Person getPerson(int index) {
        Assert.assertTrue(personList != null, "getPerson personList not initialized");
        Assert.assertTrue(index > 0 && index <= personList.size(), "getPerson index invalid");
        return personList.get(index - 1);
    }

    public int getPersonSize() {
        Assert.assertTrue(personList != null, "getPersonSize personList not initialized");
        return personList.size();
    }

    public static PersonRepository getINSTANCE() {
        if(INSTANCE == null) {
            synchronized (PersonRepository.class) {
                if(INSTANCE == null) {
                    INSTANCE = new PersonRepository();
                }
            }
        }
        return INSTANCE;
    }
}

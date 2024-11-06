package com.adam.verbal_battle.person;

public class Person {

    private int index;
    private String name;
    private int intelligence;
    private Character character;

    public Person(int index, String name, int intelligence, Character character) {
        this.index = index;
        this.name = name;
        this.intelligence = intelligence;
        this.character = character;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public Character getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName());
        if(getName().length() > 2) {
            stringBuilder.append("\t");
        } else {
            stringBuilder.append("\t\t");
        }
        stringBuilder.append(getIntelligence()).append("\t").append(getCharacter().getDesc());
        return stringBuilder.toString();
    }
}

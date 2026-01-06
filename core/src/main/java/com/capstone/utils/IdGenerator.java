package com.capstone.utils;
import java.util.UUID;

public final class IdGenerator {
    private IdGenerator(){}
    public static String id(){ return UUID.randomUUID().toString(); }
}


package com.epra.eprascript;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Parser<Double> parser = NumberParsers.DOUBLE_PARSER;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            System.out.println(parser.parse(scanner.nextLine()));
        }
    }
}
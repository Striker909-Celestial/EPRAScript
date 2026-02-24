package com.epra.eprascript;

import com.epra.eprascript.parsers.Parser;
import com.epra.eprascript.parsers.alphanumeric.CharParser;
import com.epra.eprascript.parsers.function.AssignmentParser;
import com.epra.eprascript.parsers.function.FunctionParser;
import com.epra.eprascript.parsers.Token;
import com.epra.eprascript.parsers.math.ArithmeticParsers;
import com.epra.eprascript.parsers.modifiers.Combinator;

import java.util.Scanner;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        CharParser whitespace = new CharParser(' ');
        Scanner scanner = new Scanner(System.in);
        Parser<?> parser = Combinator.SEQUENCE.combine(
                new AssignmentParser<>(
                        "-?\\d+(\\.\\d+)?",
                        ArithmeticParsers.ARITHMETIC
                ),
                AssignmentParser.ASSIGNMENT_FETCHER,
                ArithmeticParsers.ARITHMETIC
        );
        while (true) {
            System.out.print("> ");
            String text = parser.recursiveReplaceAll(scanner.nextLine(), FunctionParser::supplierToAddress);
            Token<Supplier<?>> supplier = FunctionParser.FUNCTION_FETCHER.parse(text);
            if (supplier.success()) {
                System.out.println(supplier.value().get());
            } else {
                System.out.println("Invalid function");
            }
        }
    }
}
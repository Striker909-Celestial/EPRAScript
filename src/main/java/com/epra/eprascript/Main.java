package com.epra.eprascript;

import com.epra.eprascript.parsers.Parser;
import com.epra.eprascript.parsers.alphanumeric.CharParser;
import com.epra.eprascript.parsers.alphanumeric.RegExParser;
import com.epra.eprascript.parsers.function.AssignmentParser;
import com.epra.eprascript.parsers.function.FunctionParser;
import com.epra.eprascript.parsers.Token;
import com.epra.eprascript.parsers.math.ArithmeticParsers;
import com.epra.eprascript.parsers.math.NumberParsers;
import com.epra.eprascript.parsers.modifiers.Combinator;

import java.util.Scanner;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        CharParser whitespace = new CharParser(' ');
        Scanner scanner = new Scanner(System.in);
        Parser<?> parser = Combinator.SEQUENCE.combine(
                AssignmentParser.ASSIGNMENT_FETCHER,
                ArithmeticParsers.ARITHMETIC
        );
        AssignmentParser<?> assignment = new AssignmentParser<>(
                ".+",
                new Parser<>(
                        s -> {
                            String str = parser.recursiveReplaceAll(s, FunctionParser::supplierToAddress);
                            if (!new RegExParser("[^\\d\\.-]").parse(str).success()) {
                                return new Token<>(NumberParsers.DOUBLE_PARSER.parse(str).value(), "", "", true);
                            }
                            return new Token<>(
                                    FunctionParser.FUNCTION_FETCHER.parse(str).value().get(),
                                    "", "", true
                            );
                        }
                )
        );
        while (true) {
            System.out.print("> ");
            String input = whitespace.replaceAll(scanner.nextLine(), "");
            Token <?> aToken = assignment.parse(input);
            if (aToken.success()) {
                System.out.println("Assigned value " + input.split("=")[1] + " to address " + input.split("=")[0]);
                continue;
            }
            String text = parser.recursiveReplaceAll(input, FunctionParser::supplierToAddress);
            Token<Supplier<?>> supplier = FunctionParser.FUNCTION_FETCHER.parse(text);
            //System.out.println(text);
            //System.out.println(supplier);
            if (supplier.success()) {
                System.out.println(supplier.value().get());
            } else {
                System.out.println("Invalid function");
            }
        }
    }
}
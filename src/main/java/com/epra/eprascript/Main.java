package com.epra.eprascript;

import com.epra.eprascript.basic_parsers.CharParser;
import com.epra.eprascript.basic_parsers.FunctionParser;
import com.epra.eprascript.basic_parsers.Parser;
import com.epra.eprascript.basic_parsers.Token;
import com.epra.eprascript.math_parsers.ArithmeticParsers;

import java.util.Scanner;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        CharParser whitespace = new CharParser(' ');
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String text = ArithmeticParsers.ARITHMETIC.recursiveReplaceAll(whitespace.replaceAll(scanner.nextLine(), ""),
                    FunctionParser::supplierToAddress);
            Token<Supplier<?>> supplier = FunctionParser.FUNCTION_FETCHER.parse(text);
            if (supplier.success()) {
                System.out.println(supplier.value().get());
            } else {
                System.out.println("Invalid function");
            }
        }
    }
}